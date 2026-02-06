package com.vaporant.benchmark;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * H2 In-Memory Database Utility for JMH Benchmarks
 * 
 * Provides a reusable H2 database setup for testing DAO operations in
 * benchmarks.
 * 
 * Benefits for JMH:
 * - In-memory: Fast, no disk I/O
 * - Repeatable: Each setup starts with same state
 * - Isolated: No interference with production DB
 * - SQL Compatible: MySQL-like syntax
 * 
 * Usage in benchmarks:
 * 
 * <pre>
 * {
 *         &#64;code
 *         &#64;State(Scope.Thread)
 *         public class MyDaoBenchmark {
 *                 private DataSource dataSource;
 *                 private ProductModelDM productDao;
 * 
 *                 @Setup(Level.Trial)
 *                 public void setup() throws SQLException {
 *                         dataSource = H2TestDatabaseUtil.createDataSource();
 *                         H2TestDatabaseUtil.initializeSchema(dataSource);
 *                         H2TestDatabaseUtil.populateTestData(dataSource);
 *                         productDao = new ProductModelDM(dataSource);
 *                 }
 *         }
 * }
 * </pre>
 */
public class H2TestDatabaseUtil {

        /**
         * Creates H2 in-memory DataSource using Spring's DriverManagerDataSource
         * 
         * Connection string details:
         * - mem:testdb - In-memory database named "testdb"
         * - DB_CLOSE_DELAY=-1 - Keep DB alive until JVM exits
         * - MODE=MySQL - MySQL compatibility mode
         * 
         * @return Configured H2 DataSource
         */
        public static DataSource createDataSource() {
                DriverManagerDataSource dataSource = new DriverManagerDataSource();
                dataSource.setDriverClassName("org.h2.Driver");
                // Use MySQL mode for better compatibility with production schema
                dataSource.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL");
                dataSource.setUsername("sa");
                dataSource.setPassword("");
                return dataSource;
        }

        /**
         * Initializes database schema (CREATE TABLE statements)
         * 
         * Creates all 5 tables from init.sql:
         * - utente
         * - indirizzo
         * - prodotto
         * - ordine
         * - contenuto
         * 
         * @param dataSource H2 DataSource
         * @throws SQLException if schema initialization fails
         */
        public static void initializeSchema(DataSource dataSource) throws SQLException {
                try (Connection conn = dataSource.getConnection();
                                Statement stmt = conn.createStatement()) {

                        // Table: utente
                        stmt.execute("CREATE TABLE IF NOT EXISTS utente(" +
                                        "ID INT PRIMARY KEY AUTO_INCREMENT, " +
                                        "nome VARCHAR(20) NOT NULL, " +
                                        "cognome VARCHAR(20) NOT NULL, " +
                                        "dataNascita DATE NOT NULL, " +
                                        "CF CHAR(16) UNIQUE NOT NULL, " +
                                        "numTelefono VARCHAR(14), " +
                                        "email VARCHAR(40) UNIQUE NOT NULL, " +
                                        "psw VARCHAR(30) NOT NULL, " +
                                        "tipo VARCHAR(5) DEFAULT 'user' NOT NULL CHECK(tipo = 'user' OR tipo = 'admin')"
                                        +
                                        ")");

                        // Table: indirizzo
                        stmt.execute("CREATE TABLE IF NOT EXISTS indirizzo(" +
                                        "ID INT PRIMARY KEY AUTO_INCREMENT, " +
                                        "ID_Utente INT NOT NULL, " +
                                        "stato VARCHAR(20) NOT NULL, " +
                                        "citta VARCHAR(20) NOT NULL, " +
                                        "via VARCHAR(30) NOT NULL, " +
                                        "numCivico CHAR(4) NOT NULL, " +
                                        "cap CHAR(5) NOT NULL, " +
                                        "provincia CHAR(2) NOT NULL, " +
                                        "FOREIGN KEY(ID_Utente) REFERENCES utente(ID)" +
                                        ")");

                        // Table: prodotto
                        stmt.execute("CREATE TABLE IF NOT EXISTS prodotto(" +
                                        "ID INT PRIMARY KEY AUTO_INCREMENT, " +
                                        "nome VARCHAR(40) NOT NULL, " +
                                        "descrizione VARCHAR(255), " +
                                        "quantita INT NOT NULL CHECK(quantita>=0), " +
                                        "prezzoAttuale FLOAT NOT NULL CHECK(prezzoAttuale >= 0.1), " +
                                        "tipo VARCHAR(20) NOT NULL, " +
                                        "colore VARCHAR(20) NOT NULL" +
                                        ")");

                        // Table: ordine
                        stmt.execute("CREATE TABLE IF NOT EXISTS ordine(" +
                                        "ID_Ordine INT PRIMARY KEY AUTO_INCREMENT, " +
                                        "ID_Utente INT NOT NULL, " +
                                        "ID_Indirizzo INT NOT NULL, " +
                                        "prezzoTot FLOAT NOT NULL CHECK(prezzoTot >= 0.1), " +
                                        "dataAcquisto DATE NOT NULL, " +
                                        "metodoPagamento VARCHAR(50) CHECK(metodoPagamento = 'PayPal' OR metodoPagamento = 'Carta di credito/debito'), "
                                        +
                                        "FOREIGN KEY(ID_Indirizzo) REFERENCES indirizzo(ID), " +
                                        "FOREIGN KEY(ID_Utente) REFERENCES utente(ID)" +
                                        ")");

                        // Table: contenuto
                        stmt.execute("CREATE TABLE IF NOT EXISTS contenuto(" +
                                        "ID_Ordine INT NOT NULL, " +
                                        "ID_Prodotto INT NOT NULL, " +
                                        "quantita INT NOT NULL CHECK(quantita >= 1), " +
                                        "prezzoAcquisto FLOAT NOT NULL CHECK(prezzoAcquisto >= 0.1), " +
                                        "ivaAcquisto INT NOT NULL DEFAULT 22 CHECK(ivaAcquisto >= 0), " + // FIXED:
                                                                                                          // DEFAULT
                                                                                                          // before
                                                                                                          // CHECK
                                        "PRIMARY KEY(ID_Ordine, ID_Prodotto), " +
                                        "FOREIGN KEY(ID_Prodotto) REFERENCES prodotto(ID), " +
                                        "FOREIGN KEY(ID_Ordine) REFERENCES ordine(ID_Ordine)" +
                                        ")");
                }
        }

        /**
         * Populates database with test data
         * 
         * Inserts:
         * - 4 users (3 admin, 1 user)
         * - 5 addresses
         * - Variable number of products (based on numProducts parameter)
         * - 3 sample orders
         * - 3 order contents
         * 
         * @param dataSource  H2 DataSource
         * @param numProducts Number of products to insert (useful for @Param testing)
         * @throws SQLException if data population fails
         */
        public static void populateTestData(DataSource dataSource, int numProducts) throws SQLException {
                try (Connection conn = dataSource.getConnection();
                                Statement stmt = conn.createStatement()) {

                        // Insert users
                        stmt.execute(
                                        "INSERT INTO utente VALUES(1,'Gianfranco','Barba', '2002-02-15', 'BRBGFR02B15A508B', '3290026234', 'g.barba14@studenti.unisa.it', 'ABC123.','admin')");
                        stmt.execute(
                                        "INSERT INTO utente VALUES(2,'Luigi','Guida', '2002-11-09', 'GDDLGG10G11A908B', '3336543123', 'l.guida6@studenti.unisa.it', 'CBA321.','admin')");
                        stmt.execute(
                                        "INSERT INTO utente VALUES(3,'Francesco','Corcione', '2002-07-07', 'CRC07FRC07A567C', '3389076543', 'f.corcione5@studenti.unisa.it', '123ABC.', 'admin')");
                        stmt.execute(
                                        "INSERT INTO utente VALUES(4,'Tullio','Mansi', '2002-02-20', 'MNS02TLL20A678D', '3409876321', 't.mansi@studenti.unisa.it', '321CBA.', 'user')");

                        // Insert addresses
                        stmt.execute(
                                        "INSERT INTO indirizzo VALUES(1, 1, 'Italia', 'Avella', 'Via F. Vittoria', '12', '83021', 'AV')");
                        stmt.execute(
                                        "INSERT INTO indirizzo VALUES(2, 2, 'Italia', 'Boscotrecase', 'Via Trecase', '1', '80042', 'NA')");
                        stmt.execute(
                                        "INSERT INTO indirizzo VALUES(3, 3, 'Italia', 'Ottaviano', 'Via Terre Sperdute', '3', '84010', 'NA')");
                        stmt.execute(
                                        "INSERT INTO indirizzo VALUES(4, 4, 'Italia', 'Ravello', 'Via Sulla Montagna', '4', '84011', 'SA')");
                        stmt.execute(
                                        "INSERT INTO indirizzo VALUES(5, 4, 'Italia', 'Ravello', 'Via Sul Mare', '4B', '84011', 'SA')");

                        // Insert products (parameterized count for benchmark testing)
                        String[] productNames = { "noisy creek 2", "kiwi", "voopoo drag 2", "smok nord" };
                        String[] descriptions = { "box semimeccanica", "pod entry level", "box completa", "pod" };
                        float[] prices = { 29.99f, 79.99f, 49.99f, 69.99f };
                        // High initial quantities to prevent stock depletion during high-throughput
                        // benchmarks. Increased to 100,000 to support ~25 iterations at 4000 ops/sec
                        int[] quantities = { 100000, 100000, 100000, 100000 };
                        String[] colors = { "Argento", "Rosa", "Nero", "Nero" };

                        for (int i = 0; i < numProducts; i++) {
                                int templateIndex = i % 4; // Cycle through 4 templates
                                stmt.execute(String.format(
                                                "INSERT INTO prodotto VALUES (%d,'%s %d', '%s', %d, %.2f, 'Svapo', '%s')",
                                                i + 1,
                                                productNames[templateIndex],
                                                i + 1,
                                                descriptions[templateIndex],
                                                quantities[templateIndex],
                                                prices[templateIndex] + (i * 0.5f), // Vary price slightly
                                                colors[templateIndex]));
                        }

                        // Insert sample orders
                        stmt.execute("INSERT INTO ordine VALUES (1,4,4,36.58,'2023-05-08', 'PayPal')");
                        stmt.execute("INSERT INTO ordine VALUES (2,4,5,7.58,'2023-05-08', 'PayPal')");
                        stmt.execute("INSERT INTO ordine VALUES (3,4,4,73.17,'2023-05-08', 'PayPal')");

                        // Insert order contents (only if we have products)
                        if (numProducts >= 1) {
                                stmt.execute("INSERT INTO contenuto VALUES(1,1,1,29.99,22)");
                        }
                        if (numProducts >= 2) {
                                stmt.execute("INSERT INTO contenuto VALUES(2,2,1,79.99,22)");
                        }
                        if (numProducts >= 1) {
                                stmt.execute("INSERT INTO contenuto VALUES(3,1,2,29.99,22)");
                        }
                }
        }

        /**
         * Convenience method: populate with 4 default products
         * 
         * @param dataSource H2 DataSource
         * @throws SQLException if data population fails
         */
        public static void populateTestData(DataSource dataSource) throws SQLException {
                populateTestData(dataSource, 4);
        }

        /**
         * Clears all data from database (keeps schema)
         * Useful for @Setup(Level.Invocation) if needed
         * 
         * @param dataSource H2 DataSource
         * @throws SQLException if cleanup fails
         */
        public static void clearData(DataSource dataSource) throws SQLException {
                try (Connection conn = dataSource.getConnection();
                                Statement stmt = conn.createStatement()) {
                        // Disable referential integrity temporarily
                        stmt.execute("SET REFERENTIAL_INTEGRITY FALSE");

                        // TRUNCATE tables (deletes all data AND resets AUTO_INCREMENT to 1)
                        stmt.execute("TRUNCATE TABLE contenuto");
                        stmt.execute("TRUNCATE TABLE ordine");
                        stmt.execute("TRUNCATE TABLE prodotto");
                        stmt.execute("TRUNCATE TABLE indirizzo");
                        stmt.execute("TRUNCATE TABLE utente");

                        // Re-enable referential integrity
                        stmt.execute("SET REFERENTIAL_INTEGRITY TRUE");
                }
        }

        /**
         * Drops all tables (for complete reset)
         * 
         * @param dataSource H2 DataSource
         * @throws SQLException if drop fails
         */
        public static void dropSchema(DataSource dataSource) throws SQLException {
                try (Connection conn = dataSource.getConnection();
                                Statement stmt = conn.createStatement()) {
                        stmt.execute("SET REFERENTIAL_INTEGRITY FALSE");
                        stmt.execute("DROP TABLE IF EXISTS contenuto");
                        stmt.execute("DROP TABLE IF EXISTS ordine");
                        stmt.execute("DROP TABLE IF EXISTS prodotto");
                        stmt.execute("DROP TABLE IF EXISTS indirizzo");
                        stmt.execute("DROP TABLE IF EXISTS utente");
                        stmt.execute("SET REFERENTIAL_INTEGRITY TRUE");
                }
        }
}
