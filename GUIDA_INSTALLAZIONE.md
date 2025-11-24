# Guida Completa all'Installazione di Vaporant

Benvenuto! Questa guida ti accompagnerà passo dopo passo nell'installazione e configurazione del progetto Vaporant. Anche se sei alle prime armi, segui attentamente questi passaggi e sarai operativo in pochi minuti.

## 1. Prerequisiti (Cosa ti serve)

Prima di iniziare, assicurati di avere installato i seguenti software sul tuo computer.

### A. Java Development Kit (JDK) 17
Il progetto è scritto in Java, quindi hai bisogno del JDK per compilarlo ed eseguirlo.
1.  Scarica il JDK 17 da qui: [Oracle JDK 17](https://www.oracle.com/java/technologies/downloads/#java17) (Scegli la versione per Windows x64 Installer).
2.  Esegui l'installer e segui le istruzioni.
3.  **Verifica**: Apri il terminale (cerca "cmd" o "PowerShell" nel menu Start) e scrivi:
    ```bash
    java -version
    ```
    Dovresti vedere qualcosa come `java version "17.0.x"`.

### B. Apache Maven
Maven serve per gestire le dipendenze del progetto e compilarlo.
1.  Scarica Maven da qui: [Apache Maven Download](https://maven.apache.org/download.cgi) (Scarica il file `Binary zip archive`).
2.  Estrai lo zip in una cartella comoda, ad esempio `C:\Program Files\Maven`.
3.  **Configura le Variabili d'Ambiente**:
    *   Cerca "Modifica le variabili di ambiente relative al sistema" nel menu Start.
    *   Clicca su "Variabili d'ambiente".
    *   Sotto "Variabili di sistema", trova `Path` e clicca su "Modifica".
    *   Clicca su "Nuovo" e incolla il percorso della cartella `bin` di Maven (es. `C:\Program Files\Maven\apache-maven-3.9.x\bin`).
    *   Clicca OK su tutto.
4.  **Verifica**: Apri un *nuovo* terminale e scrivi:
    ```bash
    mvn -version
    ```
    Dovresti vedere la versione di Maven installata.

### C. MySQL Server
Questo è il database dove verranno salvati i dati.
1.  Scarica l'installer da qui: [MySQL Installer](https://dev.mysql.com/downloads/installer/) (Scegli la versione web-community).
2.  Durante l'installazione, scegli "Server only" o "Developer Default".
3.  **IMPORTANTE**: Quando ti chiede di impostare la password per l'utente `root`, scegli una password sicura e **ANNOTALA**. Ti servirà dopo. (Nel progetto è impostata di default a `3946`, ma puoi cambiarla).

---

## 2. Configurazione del Database

Ora prepariamo il database per l'applicazione.

1.  Apri **MySQL Workbench** (installato con MySQL) o il terminale MySQL.
2.  Esegui questi comandi SQL per creare il database:
    ```sql
    CREATE DATABASE storage;
    USE storage;
    ```
3.  Ora dobbiamo creare le tabelle. Apri il file che si trova nella cartella `database/` del progetto (potrebbe chiamarsi `storage.sql` o simile). Copia tutto il contenuto ed eseguilo nella finestra di query di MySQL.

---

## 3. Configurazione dell'Applicazione

Dobbiamo dire all'applicazione come connettersi al tuo database.

1.  Vai nella cartella del progetto: `src/main/resources`.
2.  Apri il file `application.properties` con un editor di testo (Notepad, VS Code, ecc.).
3.  Modifica le seguenti righe con le TUE impostazioni:
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/storage?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC
    spring.datasource.username=root
    spring.datasource.password=LA_TUA_PASSWORD_MYSQL
    ```
    *Sostituisci `LA_TUA_PASSWORD_MYSQL` con la password che hai scelto durante l'installazione di MySQL.*

---

## 4. Compilazione e Avvio

Ci siamo quasi! Ora avviamo l'applicazione.

1.  Apri il terminale (PowerShell o CMD).
2.  Spostati nella cartella del progetto:
    ```bash
    cd c:\Users\gianf\Documents\Vaporant
    ```
3.  **Pulisci e Compila**:
    Esegui questo comando per scaricare le librerie e costruire il progetto:
    ```bash
    mvn clean package
    ```
    *Attendi finché non vedi la scritta "BUILD SUCCESS". La prima volta potrebbe volerci un po'.*

4.  **Avvia**:
    Esegui questo comando per far partire il server:
    ```bash
    mvn spring-boot:run
    ```
    *Quando vedi delle scritte che scorrono e alla fine il cursore lampeggia senza errori, il server è attivo!*

---

## 5. Come Usare l'Applicazione

Ora che il server è attivo, apri il tuo browser (Chrome, Edge, Firefox) e vai a questi indirizzi:

*   **Home Page**: [http://localhost:8080](http://localhost:8080)
*   **Login**: [http://localhost:8080/loginForm.jsp](http://localhost:8080/loginForm.jsp)
*   **Prodotti**: [http://localhost:8080/ProductView.jsp](http://localhost:8080/ProductView.jsp)

### Funzionalità Principali
*   **Registrazione**: Crea un nuovo utente per provare l'acquisto.
*   **Carrello**: Aggiungi prodotti e procedi al checkout.
*   **Admin**: Se hai creato un utente admin nel database, puoi gestire i prodotti.

---

## Risoluzione Problemi Comuni

*   **Errore "Port 8080 already in use"**:
    Qualcos'altro sta usando la porta 8080. Apri `application.properties` e aggiungi:
    ```properties
    server.port=8081
    ```
    Poi riavvia con `mvn spring-boot:run` e usa `http://localhost:8081`.

*   **Errore di Connessione al Database**:
    Controlla bene username e password in `application.properties`. Assicurati che il servizio MySQL sia avviato (cerca "Servizi" in Windows e controlla MySQL).

*   **Errori strani in compilazione**:
    Prova a forzare l'aggiornamento delle dipendenze:
    ```bash
    mvn clean package -U
    ```

Buon lavoro con Vaporant!
