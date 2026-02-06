package com.vaporant.benchmark;

import com.vaporant.model.ProductBean;
import com.vaporant.repository.ProductModelDM;
import org.openjdk.jmh.annotations.*;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * JMH Benchmark for ProductModelDM (Repository Layer)
 * 
 * Measures performance of synchronized DAO methods:
 * - doRetrieveAll() with different dataset sizes and ORDER BY clauses
 * - doRetrieveByKey() for single-row lookup
 * - updateQuantityStorage() for UPDATE operations
 * 
 * Key Focus: Understanding synchronized method overhead and query complexity
 * impact
 * 
 * Pattern: Uses H2 in-memory database for repeatable, fast tests
 */
@BenchmarkMode({ Mode.Throughput, Mode.AverageTime })
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class ProductModelDMBenchmark {

    /**
     * Dataset size: number of products in database
     * Tests how performance scales with data volume
     */
    @Param({ "10", "50", "100" })
    public int datasetSize;

    /**
     * ORDER BY clause for doRetrieveAll
     * Tests impact of sorting on query performance
     * "null" = no ORDER BY, "nome" = sort by name, "prezzoAttuale" = sort by price
     */
    @Param({ "null", "nome", "prezzoAttuale" })
    public String orderBy;

    private DataSource dataSource;
    private ProductModelDM productDao;
    private ProductBean testProduct;

    /**
     * Setup: Initialize H2 database and populate with test data
     * Level.Trial is safe here because we're doing mostly read operations
     * (no stock depletion issues like OrderProcessingBenchmark)
     */
    @Setup(Level.Trial)
    public void setup() throws SQLException {
        // Create H2 in-memory database
        dataSource = H2TestDatabaseUtil.createDataSource();

        // Drop existing schema if any (prevents duplicate key errors on subsequent
        // setups)
        try {
            H2TestDatabaseUtil.dropSchema(dataSource);
        } catch (SQLException e) {
            // Ignore if schema doesn't exist yet
        }

        H2TestDatabaseUtil.initializeSchema(dataSource);
        H2TestDatabaseUtil.populateTestData(dataSource, datasetSize);

        // Initialize DAO
        productDao = new ProductModelDM(dataSource);

        // Get a test product for benchmarks that need one
        Collection<ProductBean> products = productDao.doRetrieveAll(null);
        if (!products.isEmpty()) {
            testProduct = products.iterator().next();
        }
    }

    /**
     * Benchmark: Retrieve all products with optional ORDER BY
     * 
     * This benchmark tests:
     * - Synchronized method overhead
     * - ResultSet processing and collection building
     * - ORDER BY clause impact (when orderBy != "null")
     * - Scaling with dataset size
     * 
     * @return Number of products retrieved (prevents dead code elimination)
     */
    @Benchmark
    public int benchmarkRetrieveAll() throws SQLException {
        String order = "null".equals(orderBy) ? null : orderBy;
        Collection<ProductBean> products = productDao.doRetrieveAll(order);
        return products.size();
    }

    /**
     * Benchmark: Retrieve single product by primary key
     * 
     * This benchmark tests:
     * - Synchronized method overhead
     * - Indexed PK lookup (should be fast regardless of dataset size)
     * - Single-row ResultSet processing
     * 
     * @return Product code (prevents dead code elimination)
     */
    @Benchmark
    public int benchmarkRetrieveByKey() throws SQLException {
        // Use middle product ID to avoid edge cases
        int productId = datasetSize / 2;
        ProductBean product = productDao.doRetrieveByKey(productId);
        return product.getCode();
    }

    /**
     * Benchmark: Update product quantity
     * 
     * This benchmark tests:
     * - Synchronized method overhead
     * - UPDATE query performance
     * - Write operation cost
     * 
     * Note: We're updating the same product repeatedly, so no stock issues
     */
    @Benchmark
    public void benchmarkUpdateQuantity() throws SQLException {
        if (testProduct != null) {
            // Update with a safe quantity that doesn't violate constraints
            productDao.updateQuantityStorage(testProduct, 50000);
        }
    }

    /**
     * Teardown: Clean up resources
     * H2 in-memory DB auto-cleans on connection close
     */
    @TearDown(Level.Trial)
    public void teardown() {
        // Optional cleanup - H2 auto-destroys on disconnect
    }
}
