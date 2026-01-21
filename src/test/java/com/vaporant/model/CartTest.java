package com.vaporant.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Cart - Logica Business Carrello")
class CartTest {

    private Cart cart;
    private ProductBean testProduct;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        testProduct = createProduct(1, "Test Product", 10.0f, 100);
    }

    // ========== GROUP 1: addProduct() Tests ==========

    @Test
    @DisplayName("addProduct - Prodotto nuovo aggiunto correttamente")
    void addProduct_newProduct_addsToCart() {
        // Act
        cart.addProduct(testProduct);

        // Assert
        assertEquals(1, cart.getProducts().size(), "Cart dovrebbe contenere 1 prodotto");
        assertEquals(10.0, cart.getPrezzoTotale(), 0.01, "Prezzo totale dovrebbe essere 10.0");
    }

    @Test
    @DisplayName("addProduct - Prodotto esistente incrementa quantità")
    void addProduct_existingProduct_incrementsQuantity() {
        // Arrange
        cart.addProduct(testProduct);

        // Act
        cart.addProduct(testProduct);

        // Assert
        assertEquals(1, cart.getProducts().size(), "Cart dovrebbe ancora contenere 1 prodotto");
        assertEquals(2, cart.getProducts().get(0).getQuantity(), "Quantità dovrebbe essere 2");
        assertEquals(20.0, cart.getPrezzoTotale(), 0.01, "Prezzo totale dovrebbe essere 20.0");
    }

    @Test
    @DisplayName("addProduct - Quantità = stock (boundary) NON incrementa")
    void addProduct_quantityEqualsStock_doesNotIncrement() {
        // Arrange - product with limited stock
        ProductBean limitedProduct = createProduct(2, "Limited", 5.0f, 1);
        cart.addProduct(limitedProduct);

        // Act - attempt to add again (qty=1, stock=1, therefore qty == stock)
        cart.addProduct(limitedProduct);

        // Assert - quantity should not increase when qty == stock
        assertEquals(1, cart.getProducts().get(0).getQuantity(),
                "Quantità non dovrebbe incrementare quando qty == stock");
        assertEquals(5.0, cart.getPrezzoTotale(), 0.01,
                "Prezzo non dovrebbe cambiare");
    }

    @Test
    @DisplayName("addProduct - Quantità > stock NON incrementa")
    void addProduct_quantityExceedsStock_doesNotIncrement() {
        // Arrange - force quantity > stock
        ProductBean product = createProduct(3, "Overstocked", 8.0f, 2);
        cart.addProduct(product);
        cart.getProducts().get(0).setQuantity(3); // Force qty > stock (simulation edge case)

        double priceBefore = cart.getPrezzoTotale();

        // Act
        cart.addProduct(product);

        // Assert
        assertEquals(3, cart.getProducts().get(0).getQuantity(),
                "Quantità non dovrebbe incrementare quando qty > stock");
        assertEquals(priceBefore, cart.getPrezzoTotale(), 0.01);
    }

    // ========== GROUP 2: deleteProduct() Tests ==========

    @Test
    @DisplayName("deleteProduct - Rimuove prodotto e aggiorna prezzo")
    void deleteProduct_existingProduct_removesAndUpdatesPrice() {
        // Arrange
        cart.addProduct(testProduct);
        cart.addProduct(testProduct); // qty = 2, prezzo = 20.0

        // Act
        cart.deleteProduct(testProduct);

        // Assert
        assertEquals(0, cart.getProducts().size(), "Cart dovrebbe essere vuoto");
        assertEquals(0.0, cart.getPrezzoTotale(), 0.01, "Prezzo totale dovrebbe essere 0");
    }

    @Test
    @DisplayName("deleteProduct - Prodotto non esistente, nessun errore")
    void deleteProduct_nonExistingProduct_noError() {
        // Arrange
        ProductBean otherProduct = createProduct(99, "Other", 1.0f, 1);

        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> cart.deleteProduct(otherProduct));
        assertEquals(0, cart.getProducts().size());
    }

    // ========== GROUP 3: aggiorna() Tests ==========

    @Test
    @DisplayName("aggiorna - Incrementa quantità e ricalcola prezzo")
    void aggiorna_increasesQuantity_recalculatesPrice() {
        // Arrange
        cart.addProduct(testProduct); // qty=1, price=10

        // Act
        cart.aggiorna(testProduct, 5);

        // Assert
        assertEquals(5, cart.getProducts().get(0).getQuantity(), "Quantità dovrebbe essere 5");
        assertEquals(50.0, cart.getPrezzoTotale(), 0.01, "Prezzo dovrebbe essere 50.0 (10*5)");
    }

    @Test
    @DisplayName("aggiorna - Decrementa quantità e ricalcola prezzo")
    void aggiorna_decreasesQuantity_recalculatesPrice() {
        // Arrange
        cart.addProduct(testProduct);
        cart.aggiorna(testProduct, 5); // First sets qty=5

        // Act
        cart.aggiorna(testProduct, 2);

        // Assert
        assertEquals(2, cart.getProducts().get(0).getQuantity(), "Quantità dovrebbe essere 2");
        assertEquals(20.0, cart.getPrezzoTotale(), 0.01, "Prezzo dovrebbe essere 20.0 (10*2)");
    }

    @Test
    @DisplayName("aggiorna - Quantità a zero azzera prezzo")
    void aggiorna_setsQuantityToZero_recalculatesPrice() {
        // Arrange
        cart.addProduct(testProduct);
        cart.aggiorna(testProduct, 3); // qty=3, prezzo=30

        // Act
        cart.aggiorna(testProduct, 0);

        // Assert
        assertEquals(0, cart.getProducts().get(0).getQuantity(), "Quantità dovrebbe essere 0");
        assertEquals(0.0, cart.getPrezzoTotale(), 0.01, "Prezzo dovrebbe essere 0.0");
    }

    // ========== GROUP 4: containsProduct() Tests ==========

    @Test
    @DisplayName("containsProduct - Trova prodotto esistente")
    void containsProduct_productExists_returnsProduct() {
        // Arrange
        cart.addProduct(testProduct);

        // Act
        ProductBean found = cart.containsProduct(testProduct);

        // Assert
        assertNotNull(found, "Dovrebbe trovare il prodotto");
        assertEquals(testProduct.getCode(), found.getCode(), "Code dovrebbe corrispondere");
    }

    @Test
    @DisplayName("containsProduct - Ritorna null se non trovato")
    void containsProduct_productNotExists_returnsNull() {
        // Arrange - cart is empty
        ProductBean otherProduct = createProduct(99, "Other", 1.0f, 1);

        // Act
        ProductBean found = cart.containsProduct(otherProduct);

        // Assert
        assertNull(found, "Dovrebbe ritornare null per prodotto non esistente");
    }

    // ========== GROUP 5: getPrezzoTotale() Tests ==========

    @Test
    @DisplayName("getPrezzoTotale - Arrotondamento a 2 decimali")
    void getPrezzoTotale_withDecimals_roundsToTwoDecimals() {
        // Arrange - product with decimal price
        ProductBean decimalProduct = createProduct(4, "Decimal", 10.555f, 10);
        cart.addProduct(decimalProduct);

        // Act
        double total = cart.getPrezzoTotale();

        // Assert - rounding to 2 decimals: 10.555 → 10.56
        assertEquals(10.56, total, 0.01,
                "Prezzo dovrebbe essere arrotondato a 10.56 (da 10.555)");
    }

    // ========== Helper Method ==========

    /**
     * Creates a ProductBean for testing with specified parameters.
     * 
     * @param code  Product code
     * @param name  Product name
     * @param price Product price
     * @param stock Product stock quantity
     * @return ProductBean configurato
     */
    private ProductBean createProduct(int code, String name, float price, int stock) {
        ProductBean p = new ProductBean();
        p.setCode(code);
        p.setName(name);
        p.setDescription(name + " description");
        p.setPrice(price);
        p.setQuantityStorage(stock);
        p.setQuantity(1); // Default quantity initial
        return p;
    }
}
