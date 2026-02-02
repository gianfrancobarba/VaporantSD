<%@page import="com.vaporant.repository.OrderDaoImpl" %>
   <%@ page import="com.vaporant.repository.UserDaoImpl" %>
      <%@ page import="com.vaporant.model.UserBean" %>
         <%@ page import="java.util.List" %>
            <%@ page import="com.vaporant.model.AddressBean" %>
               <%@ page import="com.vaporant.model.OrderBean" %>
                  <%@ page import="com.vaporant.controller.AddressControl" %>
                     <%@ page import="com.vaporant.repository.AddressDaoImpl" %>
                        <%@ page import="com.vaporant.controller.OrderControl" %>
                           <%@ page import="com.vaporant.controller.ModifyControl" %>
                              <% UserBean user=(UserBean) request.getSession().getAttribute("user");
                                 if(user.getEmail()==null){ response.sendRedirect("loginForm.jsp"); } AddressDaoImpl
                                 addressDao=new AddressDaoImpl(); List<AddressBean> indirizzi = null;
                                 if(addressDao.findByID(user.getId()) != null)
                                 indirizzi = addressDao.findByID(user.getId());

                                 OrderDaoImpl orderDao = new OrderDaoImpl();
                                 List<OrderBean> ordini = null;
                                    if(orderDao.findByIdUtente(user.getId()) != null)
                                    ordini = orderDao.findByIdUtente(user.getId());
                                    %>

                                    <!DOCTYPE html>
                                    <html lang="it">

                                    <head>
                                       <title>Dettagli Utente</title>
                                       <link rel="stylesheet" type="text/css" href="css/UtenteStyle.css">
                                       <style>
                                          .hidden {
                                             display: none;
                                          }

                                          .placeholder {
                                             color: #65657b;
                                             font-family: sans-serif;
                                             left: 20px;
                                             line-height: 14px;
                                             pointer-events: none;
                                             position: absolute;
                                             transform-origin: 0 50%;
                                             transition: transform 200ms, color 200ms;
                                             top: 20px;
                                          }
                                       </style>
                                       <script>
                                          function showEmailInput() {
                                             var emailLabel = document.getElementById("emailLabel");
                                             var emailInput = document.getElementById("emailInput");
                                             var editButton = document.getElementById("editButton");
                                             var submitButton = document.getElementById("submitButton");

                                             emailLabel.classList.add("hidden");
                                             emailInput.classList.remove("hidden");
                                             editButton.classList.add("hidden");
                                             submitButton.classList.remove("hidden");
                                          }

                                          function showPhoneInput() {
                                             var phoneLabel = document.getElementById("phoneLabel");
                                             var phoneInput = document.getElementById("phoneInput");
                                             var editButton = document.getElementById("editPhoneButton");
                                             var submitButton = document.getElementById("submitPhoneButton");

                                             phoneLabel.classList.add("hidden");
                                             phoneInput.classList.remove("hidden");
                                             editButton.classList.add("hidden");
                                             submitButton.classList.remove("hidden");
                                          }

                                          var oldPasswordInput;
                                          var newPasswordInput;

                                          function showPasswordInputs() {
                                             oldPasswordInput = document.getElementById("oldPasswordInput");
                                             newPasswordInput = document.getElementById("newPasswordInput");
                                             var editPasswordButton = document.getElementById("editPasswordButton");
                                             var submitPasswordButton = document.getElementById("submitPasswordButton");

                                             oldPasswordInput.classList.remove("hidden");
                                             newPasswordInput.classList.remove("hidden");
                                             editPasswordButton.classList.add("hidden");
                                             submitPasswordButton.classList.remove("hidden");
                                          }

                                          function submitEmail() {
                                             var emailInput = document.getElementById("emailInput").value;

                                             // Effettua la chiamata AJAX per inviare la nuova email alla servlet
                                             var xhttp = new XMLHttpRequest();
                                             xhttp.onreadystatechange = function () {
                                                if (this.readyState === 4) {
                                                   if (this.status === 200) {
                                                      var responseJson = JSON.parse(this.responseText);
                                                      var newEmail = responseJson.email;
                                                      alert("Email modificata con successo! Ti abbiamo inviato una mail di conferma.");
                                                      document.getElementById("emailLabel").textContent = newEmail;
                                                      // Reimposta il form alla situazione iniziale
                                                      document.getElementById("emailInput").classList.add("hidden");
                                                      document.getElementById("emailInput").value = '';
                                                      document.getElementById("emailLabel").classList.remove("hidden");
                                                      document.getElementById("editButton").classList.remove("hidden");
                                                      document.getElementById("submitButton").classList.add("hidden");
                                                   } else {
                                                      alert("Si è verificato un errore durante la modifica dell'email. Riprova più tardi.");
                                                      document.getElementById("emailInput").disabled = false;
                                                      document.getElementById("submitButton").disabled = false;
                                                   }
                                                }
                                             };
                                             xhttp.open("POST", "modify", true);
                                             xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
                                             xhttp.send("action=modificaEmail&nuovaEmail=" + encodeURIComponent(emailInput));
                                          }

                                          function submitPhone() {
                                             var phoneInput = document.getElementById("phoneInput").value;

                                             // Effettua la chiamata AJAX per inviare il nuovo numero di cellulare alla servlet
                                             var xhttp = new XMLHttpRequest();
                                             xhttp.onreadystatechange = function () {
                                                if (this.readyState === 4) {
                                                   if (this.status === 200) {
                                                      var responseJson = JSON.parse(this.responseText);
                                                      var newCell = responseJson.numTelefono;
                                                      alert("Numero di cellulare modificato con successo!");
                                                      document.getElementById("phoneLabel").textContent = newCell;
                                                      // Reimposta il form alla situazione iniziale
                                                      document.getElementById("phoneInput").classList.add("hidden");
                                                      document.getElementById("phoneInput").value = '';
                                                      document.getElementById("phoneLabel").classList.remove("hidden");
                                                      document.getElementById("editPhoneButton").classList.remove("hidden");
                                                      document.getElementById("submitPhoneButton").classList.add("hidden");
                                                   } else {
                                                      alert("Si è verificato un errore durante la modifica del numero di cellulare. Riprova più tardi.");
                                                      document.getElementById("phoneInput").disabled = false;
                                                      document.getElementById("submitPhoneButton").disabled = false;
                                                   }
                                                }
                                             };
                                             xhttp.open("POST", "modify", true);
                                             xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
                                             xhttp.send("action=modificaTelefono&nuovoTelefono=" + encodeURIComponent(phoneInput));
                                          }

                                          function submitPassword() {
                                             var oldPassword = oldPasswordInput.value;
                                             var newPassword = newPasswordInput.value;

                                             // Effettua la chiamata AJAX per inviare la vecchia e la nuova password alla servlet
                                             var xhttp = new XMLHttpRequest();
                                             xhttp.onreadystatechange = function () {
                                                if (this.readyState === 4) {
                                                   if (this.status === 200) {
                                                      var responseJson = JSON.parse(this.responseText);
                                                      if (responseJson.success) {
                                                         alert("Password modificata con successo!");
                                                         // Reimposta il form alla situazione iniziale
                                                         oldPasswordInput.classList.add("hidden");
                                                         oldPasswordInput.value = '';
                                                         newPasswordInput.classList.add("hidden");
                                                         newPasswordInput.value = '';
                                                         document.getElementById("editPasswordButton").classList.remove("hidden");
                                                         document.getElementById("submitPasswordButton").classList.add("hidden");
                                                      } else {
                                                         alert("Vecchia password errata. Riprova.");
                                                      }
                                                   } else {
                                                      alert("Si è verificato un errore durante la modifica della password. Riprova più tardi.");
                                                   }
                                                }
                                             };
                                             xhttp.open("POST", "modify", true);
                                             xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
                                             xhttp.send("action=modificaPassword&vecchiaPassword=" + encodeURIComponent(oldPassword) + "&nuovaPassword=" + encodeURIComponent(newPassword));
                                          }
                                       </script>
                                    </head>

                                    <body>
                                       <%@include file="Header.jsp" %>
                                          <br> <br>
                                          <h1>Benvenuto, <%= user.getNome().toUpperCase() %>!</h1>
                                          <div class="data">
                                             <p><span>Nome:</span>
                                                <%= user.getNome() %>
                                             </p>
                                             <p><span>Cognome:</span>
                                                <%= user.getCognome() %>
                                             </p>
                                             <p><span>Data di nascita:</span>
                                                <%= user.getDataNascita() %>
                                             </p>
                                             <div>
                                                <label id="emailLabel" for="emailInput"><span>Email:</span>
                                                   <%= user.getEmail() %>
                                                </label>
                                                <input type="text" id="emailInput" class="hidden" required />
                                                <button id="editButton" class="btn"
                                                   onclick="showEmailInput()">MODIFICA</button>
                                                <button id="submitButton" class="hidden btn"
                                                   onclick="submitEmail()">CONFERMA</button>
                                             </div>
                                             <br>
                                             <div>
                                                <label id="phoneLabel" for="phoneInput"><span>Numero di
                                                      cellulare:</span>
                                                   <%= user.getNumTelefono() %>
                                                </label>
                                                <input type="text" id="phoneInput" class="hidden" required />
                                                <button id="editPhoneButton" class="btn"
                                                   onclick="showPhoneInput()">MODIFICA</button>
                                                <button id="submitPhoneButton" class="hidden btn"
                                                   onclick="submitPhone()">CONFERMA</button>
                                             </div>
                                             <br>
                                             <label id="passwordLabel"><span>Password:</span></label>
                                             <button id="editPasswordButton" class="btn"
                                                onclick="showPasswordInputs()">MODIFICA</button>
                                             <input type="text" id="oldPasswordInput" class="hidden"
                                                placeholder="Vecchia password" required />
                                             <input type="text" id="newPasswordInput" class="hidden"
                                                placeholder="Nuova password" required />
                                             <button id="submitPasswordButton" class="hidden" class="btn"
                                                onclick="submitPassword()">CONFERMA</button>
                                             <br>
                                          </div>
                                          <%if(indirizzi !=null){ %>
                                             <div class="address">
                                                <h2>INDIRIZZI</h2>
                                                <table>
                                                   <thead>
                                                      <tr>
                                                         <th>Indirizzo</th>
                                                         <th>Città</th>
                                                         <th>Provincia</th>
                                                      </tr>
                                                   </thead>
                                                   <tbody>
                                                      <% for (AddressBean indirizzo : indirizzi) { %>
                                                         <tr>
                                                            <td>
                                                               <%= indirizzo.getVia() %>
                                                            </td>
                                                            <td>
                                                               <%= indirizzo.getCitta() %>
                                                            </td>
                                                            <td>
                                                               <%= indirizzo.getProvincia() %>
                                                            </td>
                                                         </tr>
                                                         <% } %>
                                                   </tbody>
                                                </table>
                                                <button onclick="location.href='AddressForm.jsp'" class="btn">AGGIUNGI
                                                   INDIRIZZO</button>
                                                <br>
                                                <%} %>
                                             </div>
                                             <%if(ordini !=null){ %>
                                                <div class="orders">
                                                   <h2>ORDINI EFFETTUATI</h2>
                                                   <table>
                                                      <thead>
                                                         <tr>
                                                            <th>ID Ordine</th>
                                                            <th>Data Ordine</th>
                                                            <th>Totale</th>
                                                         </tr>
                                                      </thead>
                                                      <tbody>
                                                         <% for (OrderBean ordine : ordini) { %>
                                                            <tr>
                                                               <td>
                                                                  <%= ordine.getId_ordine() %>
                                                               </td>
                                                               <td>
                                                                  <%= ordine.getDataAcquisto() %>
                                                               </td>
                                                               <td>
                                                                  <%= ordine.getPrezzoTot() %>
                                                               </td>
                                                            </tr>
                                                            <% } %>
                                                      </tbody>
                                                   </table>
                                                </div>
                                                <%} %>
                                                   <%@include file="Footer.jsp" %>
                                    </body>

                                    </html>