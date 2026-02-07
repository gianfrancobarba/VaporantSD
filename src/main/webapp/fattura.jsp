<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="com.vaporant.model.OrderBean" %>
        <%@ page import="com.vaporant.model.UserBean" %>
            <%@ page import="com.vaporant.model.ProductBean" %>
                <%@ page import="java.util.List" %>
                    <%@ page import="java.time.format.DateTimeFormatter" %>

                        <% UserBean user=(UserBean) session.getAttribute("user"); OrderBean order=(OrderBean)
                            session.getAttribute("order"); List<ProductBean> products = (List<ProductBean>)
                                session.getAttribute("listaProd");

                                if (user == null || order == null) {
                                response.sendRedirect("loginForm.jsp");
                                return;
                                }
                                %>

                                <!DOCTYPE html>
                                <html lang="it">

                                <head>
                                    <meta charset="UTF-8">
                                    <title>Fattura Ordine #<%= order.getId_ordine() %>
                                    </title>
                                    <style>
                                        body {
                                            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                                            color: #333;
                                            line-height: 1.6;
                                            padding: 20px;
                                            background-color: #f9f9f9;
                                        }

                                        .invoice-box {
                                            max-width: 800px;
                                            margin: auto;
                                            padding: 30px;
                                            border: 1px solid #eee;
                                            box-shadow: 0 0 10px rgba(0, 0, 0, 0.15);
                                            background-color: #fff;
                                        }

                                        .invoice-header {
                                            display: flex;
                                            justify-content: space-between;
                                            margin-bottom: 20px;
                                        }

                                        .invoice-title {
                                            font-size: 24px;
                                            font-weight: bold;
                                            color: #555;
                                        }

                                        .invoice-details {
                                            text-align: right;
                                        }

                                        .invoice-table {
                                            width: 100%;
                                            border-collapse: collapse;
                                            margin-top: 20px;
                                        }

                                        .invoice-table th,
                                        .invoice-table td {
                                            padding: 12px;
                                            text-align: left;
                                            border-bottom: 1px solid #ddd;
                                        }

                                        .invoice-table th {
                                            background-color: #f2f2f2;
                                        }

                                        .total-row {
                                            font-weight: bold;
                                            background-color: #f9f9f9;
                                        }

                                        .btn {
                                            display: inline-block;
                                            padding: 10px 20px;
                                            background-color: #007bff;
                                            color: white;
                                            text-decoration: none;
                                            border-radius: 5px;
                                            margin-top: 20px;
                                        }

                                        .btn:hover {
                                            background-color: #0056b3;
                                        }

                                        @media print {
                                            .btn {
                                                display: none;
                                            }

                                            .invoice-box {
                                                box-shadow: none;
                                                border: none;
                                            }
                                        }
                                    </style>
                                </head>

                                <body>

                                    <div class="invoice-box">
                                        <div class="invoice-header">
                                            <div>
                                                <div class="invoice-title">VAPORANT</div>
                                                <div>Via Roma 123, Salerno</div>
                                                <div>Email: info@vaporant.com</div>
                                            </div>
                                            <div class="invoice-details">
                                                <h3>Fattura #<%= order.getId_ordine() %>
                                                </h3>
                                                <div>Data: <%= order.getDataAcquisto() %>
                                                </div>
                                                <div>Cliente: <%= user.getNome() %>
                                                        <%= user.getCognome() %>
                                                </div>
                                                <div>Email: <%= user.getEmail() %>
                                                </div>
                                            </div>
                                        </div>

                                        <table class="invoice-table">
                                            <thead>
                                                <tr>
                                                    <th>Prodotto</th>
                                                    <th>Quantità</th>
                                                    <th>Prezzo Unitario</th>
                                                    <th>Totale</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <% if (products !=null) { for (ProductBean p : products) { %>
                                                    <tr>
                                                        <td>
                                                            <%= p.getName() %>
                                                        </td>
                                                        <td>
                                                            <%= p.getQuantity() %>
                                                        </td>
                                                        <td>€ <%= String.format("%.2f", p.getPrice()) %>
                                                        </td>
                                                        <td>€ <%= String.format("%.2f", p.getPrice() * p.getQuantity())
                                                                %>
                                                        </td>
                                                    </tr>
                                                    <% } } %>
                                                        <tr class="total-row">
                                                            <td colspan="3" style="text-align: right;">Totale Ordine:
                                                            </td>
                                                            <td>€ <%= String.format("%.2f", order.getPrezzoTot()) %>
                                                            </td>
                                                        </tr>
                                            </tbody>
                                        </table>

                                        <div style="margin-top: 20px;">
                                            <strong>Metodo di Pagamento:</strong>
                                            <%= order.getMetodoPagamento() %>
                                        </div>

                                        <div style="text-align: center;">

                                            <a href="javascript:window.print()" class="btn">Stampa Fattura</a>
                                            <a href="ProductView.jsp" class="btn"
                                                style="background-color: #6c757d;">Torna alla Home</a>
                                        </div>
                                    </div>

                                </body>

                                </html>