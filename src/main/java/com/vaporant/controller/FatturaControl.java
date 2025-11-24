package com.vaporant.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.vaporant.model.OrderBean;
import com.vaporant.model.ProductBean;

@Controller
public class FatturaControl {

    @RequestMapping(value = "/fattura", method = {RequestMethod.GET, RequestMethod.POST})
    public String showFattura(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession();
        if (session.getAttribute("order") == null || session.getAttribute("user") == null) {
            return "redirect:loginForm.jsp";
        }
        return "fattura"; // Resolves to fattura.jsp
    }

    @RequestMapping(value = "/fattura/download", method = RequestMethod.GET)
    public void downloadFatturaPDF(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession();
        
        OrderBean order = (OrderBean) session.getAttribute("order");
        @SuppressWarnings("unchecked")
        List<ProductBean> products = (List<ProductBean>) session.getAttribute("listaProd");
        
        if (order == null || products == null) {
            res.sendRedirect("loginForm.jsp");
            return;
        }

        try (PDDocument document = new PDDocument()) {
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
                contentStream.showText("Metodo di Pagamento: " + order.getMetodoPagamento());
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

            // Write PDF to response
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            
            res.setContentType("application/pdf");
            res.setHeader("Content-Disposition", "attachment; filename=fattura_" + order.getId_ordine() + ".pdf");
            res.setContentLength(baos.size());
            res.getOutputStream().write(baos.toByteArray());
            res.getOutputStream().flush();
            
        } catch (Exception e) {
            e.printStackTrace();
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore nella generazione del PDF");
        }
    }
}
