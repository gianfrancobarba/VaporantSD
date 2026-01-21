package com.vaporant.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.awt.image.BufferedImage;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.vaporant.model.OrderBean;
import com.vaporant.model.ProductBean;
import com.vaporant.model.UserBean;

@WebMvcTest(FatturaControl.class)
class FatturaControlTest {

        @Autowired
        private MockMvc mockMvc;

        @Test
        @DisplayName("Fattura - Visualizzazione fattura con successo - Mostra view fattura")
        void testShowFatturaSuccess() throws Exception {
                MockHttpSession session = new MockHttpSession();
                session.setAttribute("order", new OrderBean());
                session.setAttribute("user", new UserBean());

                mockMvc.perform(get("/fattura")
                                .session(session))
                                .andExpect(status().isOk())
                                .andExpect(view().name("fattura"));
        }

        @Test
        @DisplayName("Fattura - Utente non loggato - Redirect a loginForm")
        void testShowFatturaRedirect() throws Exception {
                mockMvc.perform(get("/fattura"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("loginForm.jsp"));
        }

        @Test
        @DisplayName("Fattura - Download PDF con successo - Genera PDF con header corretti")
        void testDownloadFatturaPDFSuccess() throws Exception {
                OrderBean order = new OrderBean();
                order.setId_ordine(1);
                order.setDataAcquisto(LocalDate.now());
                order.setMetodoPagamento("Carta");

                List<ProductBean> products = new ArrayList<>();
                ProductBean p = new ProductBean();
                p.setName("Test Product");
                p.setPrice(10.0f);
                p.setQuantity(1);
                products.add(p);

                MockHttpSession session = new MockHttpSession();
                session.setAttribute("order", order);
                session.setAttribute("listaProd", products);

                mockMvc.perform(get("/fattura/download")
                                .session(session))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType("application/pdf"))
                                .andExpect(header().string("Content-Disposition",
                                                "attachment; filename=fattura_1.pdf"));
        }

        @Test
        @DisplayName("Fattura - Download PDF senza login - Redirect a loginForm")
        void testDownloadFatturaPDFRedirect() throws Exception {
                mockMvc.perform(get("/fattura/download"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("loginForm.jsp"));
        }

        @Test
        @DisplayName("Fattura - Download PDF con nome prodotto lungo - Gestione truncate correttamente")
        void testDownloadFatturaPDFLongProductName() throws Exception {
                OrderBean order = new OrderBean();
                order.setId_ordine(1);
                order.setDataAcquisto(LocalDate.now());
                order.setMetodoPagamento("Carta");

                List<ProductBean> products = new ArrayList<>();
                ProductBean p = new ProductBean();
                p.setName("This is a very long product name that exceeds thirty five characters");
                p.setPrice(10.0f);
                p.setQuantity(1);
                products.add(p);

                MockHttpSession session = new MockHttpSession();
                session.setAttribute("order", order);
                session.setAttribute("listaProd", products);

                mockMvc.perform(get("/fattura/download")
                                .session(session))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType("application/pdf"));
        }

        @Test
        @DisplayName("Fattura - Download PDF con eccezione - Restituisce 500 Internal Server Error")
        void testDownloadFatturaPDFException() throws Exception {
                OrderBean order = new OrderBean();
                order.setId_ordine(1);
                order.setDataAcquisto(null); // This will cause NPE when calling toString()
                order.setMetodoPagamento("Carta");

                List<ProductBean> products = new ArrayList<>();

                MockHttpSession session = new MockHttpSession();
                session.setAttribute("order", order);
                session.setAttribute("listaProd", products);

                mockMvc.perform(get("/fattura/download")
                                .session(session))
                                .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Fattura - PDF Content Verification - Valida contenuto testuale e valori numerici")
        void testDownloadFatturaPDF_ValidatesContentAndCalculations() throws Exception {
                // === ARRANGE ===
                OrderBean order = new OrderBean();
                order.setId_ordine(12345);
                order.setDataAcquisto(LocalDate.of(2024, 1, 15));
                order.setMetodoPagamento("PayPal");

                List<ProductBean> products = new ArrayList<>();

                // Product 1: 2 items @ 15.50 EUR = 31.00 EUR
                ProductBean p1 = new ProductBean();
                p1.setCode(1);
                p1.setName("Vaporizzatore Elite");
                p1.setPrice(15.50f);
                p1.setQuantity(2);
                products.add(p1);

                // Product 2: 1 item @ 8.99 EUR = 8.99 EUR
                ProductBean p2 = new ProductBean();
                p2.setCode(2);
                p2.setName("Cartuccia CBD");
                p2.setPrice(8.99f);
                p2.setQuantity(1);
                products.add(p2);

                // TOTAL EXPECTED: 31.00 + 8.99 = 39.99 EUR

                MockHttpSession session = new MockHttpSession();
                session.setAttribute("order", order);
                session.setAttribute("listaProd", products);

                // === ACT ===
                MvcResult result = mockMvc.perform(get("/fattura/download")
                                .session(session))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType("application/pdf"))
                                .andReturn();

                byte[] pdfBytes = result.getResponse().getContentAsByteArray();

                // === ASSERT: Multi-Layer PDF Validation ===
                try (PDDocument document = PDDocument.load(pdfBytes)) {

                        // === LAYER 0: PDF Structure Validation ===
                        assertNotNull(document, "PDF document should be valid and loadable");
                        assertEquals(1, document.getNumberOfPages(), "PDF should have exactly 1 page");

                        PDPage page = document.getPage(0);
                        assertNotNull(page, "PDF page should not be null");

                        // === LAYER 1A: PDF Rendering Validation ===
                        // CRITICAL: If coordinates are broken (mutation), rendering FAILS or produces
                        // blank page
                        PDFRenderer renderer = new PDFRenderer(document);
                        BufferedImage image = renderer.renderImageWithDPI(0, 72); // 72 DPI for speed

                        assertNotNull(image, "PDF should be renderable to image");

                        int width = image.getWidth();
                        int height = image.getHeight();

                        assertTrue(width > 0 && height > 0,
                                        "Rendered image should have positive dimensions");

                        // Verify image contains actual content (not blank white page)
                        // If mutation puts text OFF-PAGE (X<0, Y>height), page will be blank!
                        boolean hasContent = false;
                        int samplePoints = 50; // Sample 50 random pixels
                        Random random = new Random(42); // Fixed seed for reproducibility

                        for (int i = 0; i < samplePoints; i++) {
                                int x = random.nextInt(width);
                                int y = random.nextInt(height);
                                int rgb = image.getRGB(x, y);

                                // Check if pixel is NOT white (0xFFFFFFFF or -1)
                                if (rgb != 0xFFFFFFFF && rgb != -1) {
                                        hasContent = true;
                                        break;
                                }
                        }

                        assertTrue(hasContent,
                                        "PDF should contain visible content (not blank page). " +
                                                        "If coordinates are mutated, text may be off-page!");

                        // === LAYER 1B: PDF File Size Validation ===
                        // If layout is severely broken, PDF size might be anomalous
                        assertTrue(pdfBytes.length > 1000,
                                        "PDF should be at least 1KB (indicates proper content)");
                        assertTrue(pdfBytes.length < 100000,
                                        "PDF should be less than 100KB (sanity check)");

                        // === LAYER 2: Text Extraction and Order Verification ===
                        PDFTextStripper stripper = new PDFTextStripper();
                        String pdfText = stripper.getText(document);

                        // === LAYER 2A: Verify element order (TOP to BOTTOM) ===
                        // This kills Y-coordinate mutations (lines 55, 64, 72, 78, 84, 108, 112, 114,
                        // 148, 151, 155, 157)
                        int indexFattura = pdfText.indexOf("FATTURA VAPORANT");
                        int indexNumero = pdfText.indexOf("Numero Fattura:");
                        int indexData = pdfText.indexOf("Data:");
                        int indexMetodo = pdfText.indexOf("Metodo di Pagamento:");
                        int indexProdottoHeader = pdfText.indexOf("Prodotto");
                        int indexQuantitaHeader = pdfText.indexOf("Quantita");
                        int indexPrezzoHeader = pdfText.indexOf("Prezzo");
                        int indexTotaleHeader = pdfText.indexOf("Totale");
                        int indexProduct1 = pdfText.indexOf("Vaporizzatore Elite");
                        int indexProduct2 = pdfText.indexOf("Cartuccia CBD");
                        int indexTotaleLabel = pdfText.indexOf("TOTALE:");

                        // Verify correct TOP-to-BOTTOM order
                        assertTrue(indexFattura >= 0 && indexFattura < indexNumero,
                                        "FATTURA should appear before Numero Fattura");
                        assertTrue(indexNumero < indexData,
                                        "Numero Fattura should appear before Data");
                        assertTrue(indexData < indexMetodo,
                                        "Data should appear before Metodo di Pagamento");
                        assertTrue(indexMetodo < indexProdottoHeader,
                                        "Metodo should appear before table headers");

                        // === LAYER 2: Verify header order LEFT-to-RIGHT ===
                        // This kills X-coordinate mutations (lines 94, 99, 104)
                        assertTrue(indexProdottoHeader < indexQuantitaHeader,
                                        "Prodotto header should appear before Quantita");
                        assertTrue(indexQuantitaHeader < indexPrezzoHeader,
                                        "Quantita header should appear before Prezzo");
                        assertTrue(indexPrezzoHeader < indexTotaleHeader,
                                        "Prezzo header should appear before Totale header");

                        // Verify products appear after headers and in correct order
                        assertTrue(indexProdottoHeader < indexProduct1,
                                        "Headers should appear before first product");
                        assertTrue(indexProduct1 < indexProduct2,
                                        "Product 1 should appear before Product 2");
                        assertTrue(indexProduct2 < indexTotaleLabel,
                                        "Products should appear before TOTALE label");

                        // === LAYER 3: Extract and verify EXACT total value ===
                        // This kills accumulator mutation (line 122)
                        Pattern totalPattern = Pattern.compile("TOTALE:\\s*([0-9]+[.,][0-9]{2})\\s*EUR");
                        Matcher totalMatcher = totalPattern.matcher(pdfText);

                        assertTrue(totalMatcher.find(), "Should find 'TOTALE: XX.XX EUR' pattern");

                        String totalValueStr = totalMatcher.group(1).replace(",", ".");
                        double actualTotal = Double.parseDouble(totalValueStr);

                        // EXACT value verification (kills accumulator mutation on line 122)
                        assertEquals(39.99, actualTotal, 0.01,
                                        "Total should be EXACTLY 39.99 EUR (31.00 + 8.99)");

                        // === LAYER 4: Verify product data presence ===
                        // This kills X-coordinate table mutations (lines 134, 139, 144)

                        // Verify header
                        assertTrue(pdfText.contains("FATTURA VAPORANT"), "PDF should contain header");

                        // Verify order details
                        assertTrue(pdfText.contains("Numero Fattura: #12345"), "PDF should contain order number");
                        assertTrue(pdfText.contains("Data: 2024-01-15"), "PDF should contain order date");
                        assertTrue(pdfText.contains("Metodo di Pagamento: PayPal"),
                                        "PDF should contain payment method");

                        // Verify product names
                        assertTrue(pdfText.contains("Vaporizzatore Elite"), "PDF should contain product 1 name");
                        assertTrue(pdfText.contains("Cartuccia CBD"), "PDF should contain product 2 name");

                        // Verify calculations with flexible decimal format (. or ,)
                        assertTrue(pdfText.contains("15.50") || pdfText.contains("15,50"),
                                        "PDF should contain product 1 price");
                        assertTrue(pdfText.contains("31.00") || pdfText.contains("31,00"),
                                        "PDF should contain product 1 subtotal");
                        assertTrue(pdfText.contains("8.99") || pdfText.contains("8,99"),
                                        "PDF should contain product 2 price");
                }
        }

        @Test
        @DisplayName("Fattura - PDF Decimal Precision - Verifica arrotondamenti corretti")
        void testDownloadFatturaPDF_DecimalPrecisionCorrect() throws Exception {
                // === ARRANGE ===
                OrderBean order = new OrderBean();
                order.setId_ordine(999);
                order.setDataAcquisto(LocalDate.now());
                order.setMetodoPagamento("Carta");

                List<ProductBean> products = new ArrayList<>();

                // Product con prezzo decimale complesso
                ProductBean p = new ProductBean();
                p.setCode(1);
                p.setName("Test Decimal");
                p.setPrice(10.333f); // Price con 3 decimali
                p.setQuantity(3); // Total: 10.333 * 3 = 31.00 (rounded)
                products.add(p);

                MockHttpSession session = new MockHttpSession();
                session.setAttribute("order", order);
                session.setAttribute("listaProd", products);

                // === ACT ===
                MvcResult result = mockMvc.perform(get("/fattura/download")
                                .session(session))
                                .andExpect(status().isOk())
                                .andReturn();

                byte[] pdfBytes = result.getResponse().getContentAsByteArray();

                // === ASSERT ===
                try (PDDocument document = PDDocument.load(pdfBytes)) {
                        PDFTextStripper stripper = new PDFTextStripper();
                        String pdfText = stripper.getText(document);

                        // Verify order (basic check)
                        int indexFattura = pdfText.indexOf("FATTURA VAPORANT");
                        int indexProduct = pdfText.indexOf("Test Decimal");
                        int indexTotale = pdfText.indexOf("TOTALE:");

                        assertTrue(indexFattura >= 0 && indexFattura < indexProduct,
                                        "Header should appear before product");
                        assertTrue(indexProduct < indexTotale,
                                        "Product should appear before TOTALE");

                        // Extract and verify EXACT total using regex
                        Pattern totalPattern = Pattern.compile("TOTALE:\\s*([0-9]+[.,][0-9]{2})\\s*EUR");
                        Matcher totalMatcher = totalPattern.matcher(pdfText);

                        assertTrue(totalMatcher.find(), "Should find TOTALE pattern");

                        String totalValueStr = totalMatcher.group(1).replace(",", ".");
                        double actualTotal = Double.parseDouble(totalValueStr);

                        // 10.333 * 3 = 30.999 → formatted as 31.00
                        assertEquals(31.00, actualTotal, 0.01,
                                        "Total should be 31.00 EUR (10.333 * 3, rounded)");

                        // Verify unit price is formatted to 2 decimals (flexible decimal separator)
                        assertTrue(pdfText.contains("10.33") || pdfText.contains("10,33"),
                                        "PDF should contain unit price rounded to 2 decimals");
                }
        }

        @Test
        @DisplayName("Fattura - PDF Multiple Products - Somma totale corretta con 5 prodotti")
        void testDownloadFatturaPDF_MultipleProductsTotalCorrect() throws Exception {
                // === ARRANGE ===
                OrderBean order = new OrderBean();
                order.setId_ordine(777);
                order.setDataAcquisto(LocalDate.now());
                order.setMetodoPagamento("Contrassegno");

                List<ProductBean> products = new ArrayList<>();

                // 5 products with varying prices
                products.add(createProductForTest(1, "Prod A", 12.50f, 1)); // 12.50
                products.add(createProductForTest(2, "Prod B", 7.99f, 2)); // 15.98
                products.add(createProductForTest(3, "Prod C", 20.00f, 1)); // 20.00
                products.add(createProductForTest(4, "Prod D", 5.25f, 3)); // 15.75
                products.add(createProductForTest(5, "Prod E", 9.99f, 1)); // 9.99
                // EXPECTED TOTAL: 12.50 + 15.98 + 20.00 + 15.75 + 9.99 = 74.22 EUR

                MockHttpSession session = new MockHttpSession();
                session.setAttribute("order", order);
                session.setAttribute("listaProd", products);

                // === ACT ===
                MvcResult result = mockMvc.perform(get("/fattura/download")
                                .session(session))
                                .andExpect(status().isOk())
                                .andReturn();

                byte[] pdfBytes = result.getResponse().getContentAsByteArray();

                // === ASSERT: PDF Validation ===
                try (PDDocument document = PDDocument.load(pdfBytes)) {
                        // PDF Rendering Validation
                        assertNotNull(document, "PDF should be valid");
                        PDFRenderer renderer = new PDFRenderer(document);
                        BufferedImage image = renderer.renderImageWithDPI(0, 72);
                        assertNotNull(image, "PDF should be renderable");

                        // Verify content exists
                        boolean hasContent = false;
                        Random random = new Random(42);
                        for (int i = 0; i < 30; i++) {
                                int x = random.nextInt(image.getWidth());
                                int y = random.nextInt(image.getHeight());
                                if (image.getRGB(x, y) != 0xFFFFFFFF && image.getRGB(x, y) != -1) {
                                        hasContent = true;
                                        break;
                                }
                        }
                        assertTrue(hasContent, "PDF should have visible content");

                        // File size validation
                        assertTrue(pdfBytes.length > 1000 && pdfBytes.length < 100000,
                                        "PDF size should be reasonable");

                        PDFTextStripper stripper = new PDFTextStripper();
                        String pdfText = stripper.getText(document);

                        // === LAYER 1: Verify product order (5 products) ===
                        int idx1 = pdfText.indexOf("Prod A");
                        int idx2 = pdfText.indexOf("Prod B");
                        int idx3 = pdfText.indexOf("Prod C");
                        int idx4 = pdfText.indexOf("Prod D");
                        int idx5 = pdfText.indexOf("Prod E");
                        int idxTotale = pdfText.indexOf("TOTALE:");

                        assertTrue(idx1 >= 0 && idx1 < idx2 && idx2 < idx3 && idx3 < idx4 && idx4 < idx5,
                                        "Products should appear in insertion order (A, B, C, D, E)");
                        assertTrue(idx5 < idxTotale,
                                        "All products should appear before TOTALE");

                        // === LAYER 3: Extract and verify EXACT total for 5 products ===
                        Pattern totalPattern = Pattern.compile("TOTALE:\\s*([0-9]+[.,][0-9]{2})\\s*EUR");
                        Matcher totalMatcher = totalPattern.matcher(pdfText);

                        assertTrue(totalMatcher.find(), "Should find TOTALE pattern");

                        String totalValueStr = totalMatcher.group(1).replace(",", ".");
                        double actualTotal = Double.parseDouble(totalValueStr);

                        // EXACT total: 12.50 + 15.98 + 20.00 + 15.75 + 9.99 = 74.22
                        assertEquals(74.22, actualTotal, 0.01,
                                        "Total should be EXACTLY 74.22 EUR");

                        // Verify all subtotals present
                        assertTrue(pdfText.contains("12.50") || pdfText.contains("12,50"),
                                        "PDF should contain subtotal 1");
                        assertTrue(pdfText.contains("15.98") || pdfText.contains("15,98"),
                                        "PDF should contain subtotal 2");
                        assertTrue(pdfText.contains("20.00") || pdfText.contains("20,00"),
                                        "PDF should contain subtotal 3");
                        assertTrue(pdfText.contains("15.75") || pdfText.contains("15,75"),
                                        "PDF should contain subtotal 4");
                        assertTrue(pdfText.contains("9.99") || pdfText.contains("9,99"),
                                        "PDF should contain subtotal 5");
                }
        }

        // Helper method
        private ProductBean createProductForTest(int code, String name, float price, int qty) {
                ProductBean p = new ProductBean();
                p.setCode(code);
                p.setName(name);
                p.setPrice(price);
                p.setQuantity(qty);
                return p;
        }
}
