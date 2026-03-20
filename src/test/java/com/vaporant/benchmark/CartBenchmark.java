package com.vaporant.benchmark;

import com.vaporant.model.Cart;
import com.vaporant.model.ProductBean;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * JMH Benchmark for Cart operations (session management)
 * 
 * Benchmarks memory-intensive operations:
 * - Adding products to cart
 * - Deleting products from cart
 * - Retrieving cart size
 * 
 * IMPROVEMENTS (following JMH best practices):
 * - @Param for testing different cart sizes (5, 20, 50)
 * - State class to prevent constant folding
 * - Return values to prevent dead code elimination
 * 
 * Pattern from guide: guida_testing_parte3_jmh.md
 */
@BenchmarkMode({ Mode.AverageTime, Mode.Throughput })
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class CartBenchmark {

    /**
     * IMPROVEMENT: @Param to test with different cart sizes
     * This allows us to see how performance scales with cart size
     */
    @Param({ "5", "20", "50" })
    public int cartSize;

    private Cart cart;
    private ProductBean[] preloadedProducts;

    /**
     * Setup executed once per benchmark iteration
     * Creates cart and test products based on cartSize parameter
     */
    @Setup(Level.Trial)
    public void setup() {
        cart = new Cart();

        // Pre-populate cart with cartSize products for realistic scenario
        // IMPROVEMENT: Using @Param value instead of hardcoded 20
        preloadedProducts = new ProductBean[cartSize];
        for (int i = 0; i < cartSize; i++) {
            ProductBean p = createTestProduct(i, "Product " + i, 10.0f + i);
            p.setQuantity(1);
            preloadedProducts[i] = p;
            cart.addProduct(p);
        }
    }

    /**
     * Benchmark: Adding a single product to cart
     * Tests: addProduct() method performance
     */
    @Benchmark
    public void benchmarkAddProduct() {
        Cart localCart = new Cart();
        ProductBean product = createTestProduct(1, "Benchmark Product", 19.99f);
        product.setQuantity(1);
        localCart.addProduct(product);
    }

    /**
     * Benchmark: Adding product to pre-populated cart
     * Tests: addProduct() with containsProduct() check overhead
     * NOTE: cart is pre-populated with cartSize products (5, 20, or 50)
     */
    @Benchmark
    public void benchmarkAddProductToPopulatedCart() {
        // Use unique ID to avoid collision with preloaded products
        ProductBean newProduct = createTestProduct(9999, "New Product", 15.99f);
        newProduct.setQuantity(1);
        cart.addProduct(newProduct);
        // Cleanup to maintain cart state for next iteration
        cart.deleteProduct(newProduct);
        // NOTE: void method - JVM could potentially eliminate this
        // In practice, cart state modification prevents elimination
    }

    /**
     * Benchmark: Deleting product from cart
     * Tests: deleteProduct() method performance with iteration
     */
    @Benchmark
    public void benchmarkDeleteProduct() {
        // Add and delete to measure delete performance
        ProductBean product = createTestProduct(100, "Delete Me", 5.99f);
        product.setQuantity(1);
        cart.addProduct(product);
        cart.deleteProduct(product);
    }

    /**
     * Benchmark: Getting cart products list
     * Tests: getProducts() method performance
     * GOOD PRACTICE: Returns int to prevent dead code elimination
     */
    @Benchmark
    public int benchmarkGetCartSize() {
        // ANTI-PATTERN would be: cart.getProducts().size(); (no return)
        // JVM would optimize this away as "dead code"
        return cart.getProducts().size();
    }

    /**
     * Benchmark: Update product quantity in cart
     * Tests: aggiorna() method with price recalculation
     */
    @Benchmark
    public void benchmarkUpdateQuantity() {
        if (!cart.getProducts().isEmpty()) {
            ProductBean product = cart.getProducts().get(0);
            cart.aggiorna(product, 3);
        }
    }

    /**
     * Benchmark: Complete cart operation cycle
     * Tests: add -> update -> delete cycle
     * Uses cartSize parameter to test with different cart sizes
     */
    @Benchmark
    public double benchmarkCartCycle() {
        Cart localCart = new Cart();

        // FIXED: Add cartSize products (not hardcoded 5)
        // This allows testing cycle performance with different cart sizes
        for (int i = 0; i < cartSize; i++) {
            ProductBean p = createTestProduct(200 + i, "Cycle Product " + i, 12.99f);
            p.setQuantity(1);
            localCart.addProduct(p);
        }

        // Update quantity of first product
        if (!localCart.getProducts().isEmpty()) {
            localCart.aggiorna(localCart.getProducts().get(0), 2);
        }

        // Delete first product
        if (!localCart.getProducts().isEmpty()) {
            localCart.deleteProduct(localCart.getProducts().get(0));
        }

        // CRITICAL: Return total to prevent dead code elimination
        // Without this return, JVM could optimize away the entire benchmark!
        return localCart.getPrezzoTotale();
    }

    /**
     * Helper method to create test ProductBean
     */
    private ProductBean createTestProduct(int code, String name, float price) {
        ProductBean product = new ProductBean();
        product.setCode(code);
        product.setName(name);
        product.setDescription("Test description for " + name);
        product.setPrice(price);
        product.setQuantityStorage(100);
        product.setQuantity(1);
        return product;
    }
}
