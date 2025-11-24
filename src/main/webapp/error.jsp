<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <!DOCTYPE html>
    <html lang="it">

    <head>
        <meta charset="UTF-8">
        <title>Errore - Vaporant</title>
        <style>
            body {
                font-family: sans-serif;
                text-align: center;
                padding: 50px;
                background-color: #f4f4f4;
            }

            .error-container {
                background: white;
                padding: 40px;
                border-radius: 10px;
                box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
                display: inline-block;
            }

            h1 {
                color: #d9534f;
                font-size: 48px;
                margin-bottom: 10px;
            }

            p {
                font-size: 18px;
                color: #666;
            }

            .btn {
                display: inline-block;
                margin-top: 20px;
                padding: 10px 20px;
                background-color: #007bff;
                color: white;
                text-decoration: none;
                border-radius: 5px;
            }

            .btn:hover {
                background-color: #0056b3;
            }
        </style>
    </head>

    <body>
        <div class="error-container">
            <h1>Oops!</h1>
            <h2>Errore <%= request.getAttribute("errorCode") !=null ? request.getAttribute("errorCode") : "Sconosciuto"
                    %>
            </h2>
            <p>
                <%= request.getAttribute("errorMessage") !=null ? request.getAttribute("errorMessage")
                    : "Qualcosa è andato storto." %>
            </p>
            <a href="Home.jsp" class="btn">Torna alla Home</a>
        </div>
    </body>

    </html>