<!DOCTYPE html>
<html lang="it">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Signup Page</title>
    <link rel="preconnect" href="https://fonts.gstatic.com">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="css/loginFormStyle.css">
</head>

<body>
    <div class="login-container">
        <div class="background-animation"></div>
        <form action="SignControl" method="POST" class="form">
            <span class="BorderTopBottom"></span>
            <span class="BorderLeftRight"></span>

            <h3><a href="product"><img src="media/logo.png" class="logosite">Registrati</a></h3>

            <!-- Sezione Informazioni Personali -->
            <div class="form-section">
                <label for="nome">Nome</label><br>
                <input type="text" id="nome" name="nome" autocomplete="off" required><br>

                <label for="cognome">Cognome</label><br>
                <input type="text" id="cognome" name="cognome" autocomplete="off" required><br>

                <label for="data_nascita">Data di Nascita</label><br>
                <input type="date" id="data_nascita" name="data_nascita" required><br>

                <label for="codice_fiscale">Codice Fiscale</label><br>
                <input type="text" id="codice_fiscale" name="codice_fiscale" autocomplete="off" required><br>

                <label for="telefono">Numero di Telefono</label><br>
                <input type="text" id="telefono" name="telefono" autocomplete="off" required><br>
            </div>

            <!-- Sezione Credenziali di Accesso -->
            <div class="form-section">
                <label for="email">Email</label><br>
                <input type="email" id="email" name="email" autocomplete="off" required><br>

                <label for="password">Password</label><br>
                <input type="password" id="password" name="password" placeholder="********" autocomplete="off"
                    required><br>

                <input type="checkbox" id="checkbox" onclick="hidePassword();">
                <label for="checkbox">Mostra Password</label><br>
            </div>

            <!-- Pulsante di invio -->
            <input type="submit" id="submit" value="Registrati">

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
        </form>
        <p>Sei gia' registrato? <a href="loginForm.jsp">Accedi qui</a>.</p>
    </div>
</body>

</html>