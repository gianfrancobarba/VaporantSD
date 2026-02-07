<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

    <% String action=(String)request.getSession().getAttribute("action"); request.getSession().setAttribute("cart",
        request.getSession().getAttribute("cart")); %>
        <!DOCTYPE html>
        <html lang="it">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Login Page</title>
            <link rel="preconnect" href="https://fonts.gstatic.com">
            <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300&display=swap" rel="stylesheet">
            <link rel="stylesheet" href="css/loginFormStyle.css">
            <style>
                .error-message {
                    color: red;
                    margin-top: 5px;
                    font-size: 14px;
                }
            </style>
        </head>

        <body>
            <div class="login-container">
                <form action="login" id="login-form" method="POST" class="form">
                    <span class="BorderTopBottom"></span>
                    <span class="BorderLeftRight"></span>

                    <!-- Titolo della tabella -->
                    <h1><a href="product"><img src="media/logo.png" class="logosite" alt="?">Accedi</a></h1>

                    <label for="email">Email</label><br>
                    <input type="email" id="email" name="email" autocomplete="off"><br>
                    <p id="error-email" class="error-message"></p>

                    <label for="password">Password</label><br>
                    <input type="password" id="password" name="password" placeholder="********" autocomplete="off"><br>
                    <p id="error-password" class="error-message"></p>

                    <input type="checkbox" id="checkbox" onclick="hidePassword();">
                    <label for="checkbox">Mostra Password</label><br>

                    <input type="submit" id="submit" value="Accedi">
                </form>
                <!-- Collegamento alla pagina di registrazione (signup.html) -->
                <p>Non hai un account? <a href="SignForm.jsp">Registrati qui</a></p>
            </div>
            <script>
                function hidePassword() {
                    var x = document.getElementById('password');
                    if (x.type == "password") {
                        x.type = 'text';
                    } else {
                        x.type = 'password';
                    }
                }
            </script>
        </body>

        </html>