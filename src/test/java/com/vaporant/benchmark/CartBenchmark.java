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
 * Pattern from guide: guida_testing_parte3_jmh.md
 */
@BenchmarkMode({ Mode.AverageTime, Mode.Throughput })
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class CartBenchmark {

    private Cart cart;
    private ProductBean[] preloadedProducts;

    /**
     * Setup executed once per benchmark iteration
     * Creates cart and test products
     */
    @Setup(Level.Trial)
    public void setup() {
        cart = new Cart();

        // Pre-populate cart with 20 products for realistic scenario
        preloadedProducts = new ProductBean[20];
        for (int i = 0; i < 20; i++) {
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
     */
    @Benchmark
    public void benchmarkAddProductToPopulatedCart() {
        ProductBean newProduct = createTestProduct(50, "New Product", 15.99f);
        newProduct.setQuantity(1);
        cart.addProduct(newProduct);
        // Cleanup
        cart.deleteProduct(newProduct);
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
     */
    @Benchmark
    public int benchmarkGetCartSize() {
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
     */
    @Benchmark
    public double benchmarkCartCycle() {
        Cart localCart = new Cart();

        // Add 5 products
        for (int i = 0; i < 5; i++) {
            ProductBean p = createTestProduct(200 + i, "Cycle Product " + i, 12.99f);
            p.setQuantity(1);
            localCart.addProduct(p);
        }

        // Update quantity
        if (!localCart.getProducts().isEmpty()) {
            localCart.aggiorna(localCart.getProducts().get(0), 2);
        }

        // Delete one
        if (!localCart.getProducts().isEmpty()) {
            localCart.deleteProduct(localCart.getProducts().get(0));
        }

        // Return total to prevent dead code elimination
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
