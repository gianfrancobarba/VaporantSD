package com.vaporant.benchmark;

import com.vaporant.model.ContenutoBean;
import com.vaporant.model.OrderBean;
import com.vaporant.model.ProductBean;
import com.vaporant.repository.ContenutoDaoImpl;
import com.vaporant.repository.OrderDaoImpl;
import com.vaporant.repository.ProductModelDM;
import org.openjdk.jmh.annotations.*;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * JMH Benchmark for Order Processing Workflow
 * 
 * Simulates the complete order creation process from OrderControl.doPost():
 * 1. Save order (OrderDaoImpl.saveOrder)
 * 2. Save order contents for each product (ContenutoDaoImpl.save)
 * 3. Update product storage quantity (ProductModelDM.updateQuantityStorage)
 * 
 * This benchmark measures:
 * - Transaction overhead with multiple DAO operations
 * - Database write performance
 * - Scaling with different numbers of products per order
 * 
 * Pattern: Uses H2 in-memory database for fast, repeatable tests
 */
@BenchmarkMode({ Mode.Throughput, Mode.AverageTime })
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
// Reduced iterations to prevent stock depletion with high-throughput operations
@Warmup(iterations = 2, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 3, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Fork(1)
public class OrderProcessingBenchmark {

    /**
     * Number of products in each order
     * Tests scaling: more products = more DAO write operations
     */
    @Param({ "1", "5", "10" })
    public int numProductsInOrder;

    private DataSource dataSource;
    private OrderDaoImpl orderDao;
    private ContenutoDaoImpl contenutoDao;
    private ProductModelDM productDao;

    // Pre-created products for the order (populated in setup)
    private List<ProductBean> availableProducts;

    /**
     * Setup: Initialize H2 database and DAOs
     * Executed BEFORE EACH ITERATION (warmup or measurement iteration)
     * 
     * Level.Iteration is a compromise between Level.Invocation (too slow - resets
     * per op) and Level.Trial (can deplete stock during iterations). This resets
     * the database after each 1-second iteration, providing fresh state without
     * excessive overhead.
     * 
     * With 100,000 initial stock and ~4000 ops/sec throughput, each iteration
     * consumes ~4000 units, allowing 25 iterations before depletion.
     */
    @Setup(Level.Iteration)
    public void setup() throws SQLException {
        // Create/reuse H2 in-memory database
        if (dataSource == null) {
            dataSource = H2TestDatabaseUtil.createDataSource();
        }

        // Drop existing schema (if any)
        try {
            H2TestDatabaseUtil.dropSchema(dataSource);
        } catch (SQLException e) {
            // Ignore if schema doesn't exist yet
        }

        // Recreate fresh schema
        H2TestDatabaseUtil.initializeSchema(dataSource);

        // Populate with fresh test data (20 products)
        H2TestDatabaseUtil.populateTestData(dataSource, 20);

        // Initialize DAOs
        orderDao = new OrderDaoImpl(dataSource);
        contenutoDao = new ContenutoDaoImpl(dataSource);

        // ProductModelDM no longer has @Autowired constructor after merge
        // Use reflection to set the private 'ds' field
        productDao = new ProductModelDM();
        try {
            java.lang.reflect.Field dsField = ProductModelDM.class.getDeclaredField("ds");
            dsField.setAccessible(true);
            dsField.set(productDao, dataSource);
        } catch (Exception e) {
            throw new SQLException("Failed to set DataSource on ProductModelDM", e);
        }

        // Load available products from DB
        availableProducts = new ArrayList<>(productDao.doRetrieveAll(null));
    }

    /**
     * Benchmark: Complete order creation workflow
     * 
     * Simulates OrderControl.doPost() logic:
     * 1. Create and save OrderBean
     * 2. Create and save ContenutoBean for each product
     * 3. Update product storage quantities
     * 
     * @return Order ID (prevents dead code elimination)
     */
    @Benchmark
    public int benchmarkCompleteOrderWorkflow() throws SQLException {
        // Step 1: Create and save order
        OrderBean order = new OrderBean();
        order.setId_utente(4); // Test user ID from H2TestDatabaseUtil
        order.setId_indirizzo(4); // Test address ID
        order.setPrezzoTot(calculateTotalPrice());
        order.setDataAcquisto(LocalDate.now());
        order.setMetodoPagamento("PayPal");

        orderDao.saveOrder(order);

        // Get the generated order ID
        int orderId = orderDao.getIdfromDB();
        order.setId_ordine(orderId);

        // Step 2 & 3: For each product in order, save content and update storage
        for (int i = 0; i < numProductsInOrder && i < availableProducts.size(); i++) {
            ProductBean product = availableProducts.get(i);

            // Create and save ContenutoBean
            ContenutoBean contenuto = new ContenutoBean();
            contenuto.setId_ordine(orderId);
            contenuto.setId_prodotto(product.getCode());
            contenuto.setQuantita(1); // Order 1 quantity of each product
            contenuto.setPrezzoAcquisto(product.getPrice());
            contenuto.setIvaAcquisto(22); // 22% IVA

            contenutoDao.saveContenuto(contenuto);
            // Note: saveContenuto already calls updateStorage internally
        }

        // Return order ID to prevent dead code elimination
        return orderId;
    }

    /**
     * Benchmark: Only save order (no contents)
     * Baseline to isolate order save performance
     */
    @Benchmark
    public int benchmarkSaveOrderOnly() throws SQLException {
        OrderBean order = new OrderBean();
        order.setId_utente(4);
        order.setId_indirizzo(4);
        order.setPrezzoTot(100.0);
        order.setDataAcquisto(LocalDate.now());
        order.setMetodoPagamento("Carta di credito/debito");

        orderDao.saveOrder(order);
        int orderId = orderDao.getIdfromDB();

        return orderId;
    }

    /**
     * Benchmark: Save order + contents (no product update)
     * Measures order + content saving overhead
     */
    @Benchmark
    public int benchmarkSaveOrderWithContents() throws SQLException {
        // Create order
        OrderBean order = new OrderBean();
        order.setId_utente(4);
        order.setId_indirizzo(4);
        order.setPrezzoTot(calculateTotalPrice());
        order.setDataAcquisto(LocalDate.now());
        order.setMetodoPagamento("PayPal");

        orderDao.saveOrder(order);
        int orderId = orderDao.getIdfromDB();
        order.setId_ordine(orderId);

        // Save contents only
        for (int i = 0; i < numProductsInOrder && i < availableProducts.size(); i++) {
            ProductBean product = availableProducts.get(i);

            ContenutoBean contenuto = new ContenutoBean();
            contenuto.setId_ordine(orderId);
            contenuto.setId_prodotto(product.getCode());
            contenuto.setQuantita(1);
            contenuto.setPrezzoAcquisto(product.getPrice());
            contenuto.setIvaAcquisto(22);

            contenutoDao.saveContenuto(contenuto);
        }

        return orderId;
    }

    /**
     * Helper: Calculate total price for order
     */
    private double calculateTotalPrice() {
        double total = 0.0;
        for (int i = 0; i < numProductsInOrder && i < availableProducts.size(); i++) {
            ProductBean product = availableProducts.get(i);
            total += product.getPrice() * product.getQuantity();
        }
        // Add 22% IVA
        return total * 1.22;
    }

    /**
     * Teardown: Clean up resources
     * Note: H2 in-memory DB auto-cleans on connection close
     */
    @TearDown(Level.Trial)
    public void teardown() {
        // Optional cleanup - H2 auto-destroys on disconnect
    }
}
