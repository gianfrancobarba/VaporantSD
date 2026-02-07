-- Tabella utente: gestione utenti e amministratori
CREATE TABLE utente(
    ID INT PRIMARY KEY AUTO_INCREMENT,
    nome  VARCHAR(20) NOT NULL,
    cognome  VARCHAR(20) NOT NULL,
    dataNascita DATE NOT NULL,
    CF CHAR(16) UNIQUE NOT NULL,
    numTelefono VARCHAR(14),
    email VARCHAR(40) UNIQUE NOT NULL,
    psw VARCHAR(30) NOT NULL, 
    tipo VARCHAR(5) DEFAULT 'user' NOT NULL CHECK(tipo = 'user' OR tipo = 'admin') 
);

-- Tabella indirizzo: gestione indirizzi di spedizione
CREATE TABLE indirizzo(
    ID INT PRIMARY KEY AUTO_INCREMENT,
    ID_Utente INT NOT NULL,
    stato VARCHAR(20) NOT NULL,
    citta VARCHAR(20) NOT NULL,
    via VARCHAR(30) NOT NULL,
    numCivico CHAR(4) NOT NULL,
    cap CHAR(5) NOT NULL,
    provincia CHAR(2) NOT NULL,
    FOREIGN KEY(ID_Utente) REFERENCES utente(ID)
);

-- Tabella prodotto: catalogo prodotti per lo svapo
CREATE TABLE prodotto(
    ID INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(40) NOT NULL,
    descrizione VARCHAR(255),
    quantita INT NOT NULL CHECK(quantita>=0),
    prezzoAttuale FLOAT NOT NULL CHECK(prezzoAttuale >= 0.1),
    tipo VARCHAR(20) NOT NULL,
    colore VARCHAR(20) NOT NULL
);

-- Tabella ordine: gestione ordini effettuati
CREATE TABLE ordine(
    ID_Ordine INT PRIMARY KEY AUTO_INCREMENT,
    ID_Utente INT NOT NULL, 
    ID_Indirizzo INT NOT NULL,
    prezzoTot FLOAT NOT NULL CHECK(prezzoTot >= 0.1),
    dataAcquisto DATE NOT NULL,
    metodoPagamento VARCHAR(50) CHECK(metodoPagamento = 'PayPal' OR metodoPagamento = 'Carta di credito/debito'),
    FOREIGN KEY(ID_Indirizzo) REFERENCES indirizzo(ID),
    FOREIGN KEY(ID_Utente) REFERENCES utente(ID)
);

-- Tabella contenuto: dettagli prodotti in ogni ordine
CREATE TABLE contenuto(
    ID_Ordine INT NOT NULL,
    ID_Prodotto INT NOT NULL,
    quantita INT NOT NULL CHECK(quantita >= 1),
    prezzoAcquisto FLOAT NOT NULL CHECK(prezzoAcquisto >= 0.1),
    ivaAcquisto INT NOT NULL DEFAULT 22 CHECK(ivaAcquisto >= 0),
    PRIMARY KEY(ID_Ordine, ID_Prodotto),
    FOREIGN KEY(ID_Prodotto) REFERENCES prodotto(ID),
    FOREIGN KEY(ID_Ordine) REFERENCES ordine(ID_Ordine)
);
