package com.vaporant.benchmark;

import com.vaporant.model.OrderBean;
import com.vaporant.model.ProductBean;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.openjdk.jmh.annotations.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * JMH Benchmark for PDF Generation (Invoice generation)
 * 
 * Benchmarks CPU-intensive operations:
 * - Simple invoice generation (5 products)
 * - Complex invoice generation (50 products)
 * - PDF document creation overhead
 * 
 * Pattern from guide: guida_testing_parte3_jmh.md
 * Tests Apache PDFBox performance for invoice generation
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class PdfGenerationBenchmark {

    private OrderBean simpleOrder;
    private List<ProductBean> simpleProducts;

    private OrderBean complexOrder;
    private List<ProductBean> complexProducts;

    /**
     * Setup: Create test orders with different complexity levels
     */
    @Setup(Level.Trial)
    public void setup() {
        // Simple order: 5 products
        simpleOrder = createTestOrder(1, "Simple Order");
        simpleProducts = createTestProducts(5);

        // Complex order: 50 products
        complexOrder = createTestOrder(2, "Complex Order");
        complexProducts = createTestProducts(50);
    }

    /**
     * Benchmark: Generate PDF for simple invoice (5 products)
     * Tests: Basic PDF generation performance
     */
    @Benchmark
    public byte[] benchmarkSimpleInvoice() throws IOException {
        return generateInvoicePDF(simpleOrder, simpleProducts);
    }

    /**
     * Benchmark: Generate PDF for complex invoice (50 products)
     * Tests: PDF generation with large dataset
     */
    @Benchmark
    public byte[] benchmarkComplexInvoice() throws IOException {
        return generateInvoicePDF(complexOrder, complexProducts);
    }

    /**
     * Benchmark: PDF document creation overhead (empty document)
     * Tests: PDFBox document infrastructure cost
     */
    @Benchmark
    public byte[] benchmarkEmptyDocument() throws IOException {
        try (PDDocument document = new PDDocument();
                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            document.save(baos);

            return baos.toByteArray();
        }
    }

    /**
     * Benchmark: PDF with title only (minimal content)
     * Tests: Text rendering overhead
     */
    @Benchmark
    public byte[] benchmarkTitleOnly() throws IOException {
        try (PDDocument document = new PDDocument();
                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, page.getMediaBox().getHeight() - 50);
                contentStream.showText("FATTURA VAPORANT");
                contentStream.endText();
            }

            document.save(baos);
            return baos.toByteArray();
        }
    }

    /**
     * Core PDF generation logic (mirror of FatturaControl logic)
     */
    private byte[] generateInvoicePDF(OrderBean order, List<ProductBean> products) throws IOException {
        try (PDDocument document = new PDDocument();
                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                float margin = 50;
                float yPosition = page.getMediaBox().getHeight() - margin;
                float leading = 20f;

                // Title
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("FATTURA VAPORANT");
                contentStream.endText();
                yPosition -= leading * 2;

                // Invoice details
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Numero Fattura: #" + order.getId_ordine());
                contentStream.endText();
                yPosition -= leading;

                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Data: " + order.getDataAcquisto().toString());
                contentStream.endText();
                yPosition -= leading;

                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Metodo: " + order.getMetodoPagamento());
                contentStream.endText();
                yPosition -= leading * 2;

                // Table header
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Prodotto");
                contentStream.endText();

                contentStream.beginText();
                contentStream.newLineAtOffset(margin + 250, yPosition);
                contentStream.showText("Quantita");
                contentStream.endText();

                contentStream.beginText();
                contentStream.newLineAtOffset(margin + 350, yPosition);
                contentStream.showText("Prezzo");
                contentStream.endText();

                contentStream.beginText();
                contentStream.newLineAtOffset(margin + 450, yPosition);
                contentStream.showText("Totale");
                contentStream.endText();

                yPosition -= leading;

                // Draw line
                contentStream.moveTo(margin, yPosition);
                contentStream.lineTo(page.getMediaBox().getWidth() - margin, yPosition);
                contentStream.stroke();
                yPosition -= leading;

                // Table content
                contentStream.setFont(PDType1Font.HELVETICA, 11);
                double totalAmount = 0;

                for (ProductBean product : products) {
                    double subtotal = product.getPrice() * product.getQuantity();
                    totalAmount += subtotal;

                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin, yPosition);
                    String productName = product.getName();
                    if (productName.length() > 35) {
                        productName = productName.substring(0, 32) + "...";
                    }
                    contentStream.showText(productName);
                    contentStream.endText();

                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin + 250, yPosition);
                    contentStream.showText(String.valueOf(product.getQuantity()));
                    contentStream.endText();

                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin + 350, yPosition);
                    contentStream.showText(String.format("%.2f EUR", product.getPrice()));
                    contentStream.endText();

                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin + 450, yPosition);
                    contentStream.showText(String.format("%.2f EUR", subtotal));
                    contentStream.endText();

                    yPosition -= leading;
                }

                yPosition -= leading;

                // Draw line before total
                contentStream.moveTo(margin, yPosition);
                contentStream.lineTo(page.getMediaBox().getWidth() - margin, yPosition);
                contentStream.stroke();
                yPosition -= leading;

                // Total
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin + 350, yPosition);
                contentStream.showText("TOTALE:");
                contentStream.endText();

                contentStream.beginText();
                contentStream.newLineAtOffset(margin + 450, yPosition);
                contentStream.showText(String.format("%.2f EUR", totalAmount));
                contentStream.endText();
            }

            document.save(baos);
            return baos.toByteArray();
        }
    }

    /**
     * Helper: Create test order
     */
    private OrderBean createTestOrder(int id, String metodoPagamento) {
        OrderBean order = new OrderBean();
        order.setId_ordine(id);
        order.setMetodoPagamento(metodoPagamento);
        order.setDataAcquisto(LocalDate.now());
        return order;
    }

    /**
     * Helper: Create test products list
     */
    private List<ProductBean> createTestProducts(int count) {
        List<ProductBean> products = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            ProductBean product = new ProductBean();
            product.setCode(i);
            product.setName("Product " + i + " - Test Product Name");
            product.setDescription("Description for product " + i);
            product.setPrice(10.0f + (i % 50));
            product.setQuantity(1 + (i % 5));
            product.setQuantityStorage(100);
            products.add(product);
        }

        return products;
    }
}
