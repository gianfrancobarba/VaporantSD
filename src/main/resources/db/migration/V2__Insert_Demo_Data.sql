SET @ruolo_admin = 'admin';
SET @ruolo_user = 'user';
SET @paese_italia = 'Italia';
SET @categoria_svapo = 'Svapo';
SET @metodo_paypal = 'PayPal';

-- Utenti di default (3 admin, 1 user)
INSERT INTO utente VALUES(1,'Gianfranco','Barba', '2002-02-15', 'BRBGFR02B15A508B', '3290026234', 'g.barba14@studenti.unisa.it', 'ABC123.', @ruolo_admin);
INSERT INTO utente VALUES(2,'Luigi','Guida', '2002-11-09', 'GDDLGG10G11A908B', '3336543123', 'l.guida6@studenti.unisa.it', 'CBA321.', @ruolo_admin);
INSERT INTO utente VALUES(3,'Francesco','Corcione', '2002-07-07', 'CRC07FRC07A567C', '3389076543', 'f.corcione5@studenti.unisa.it', '123ABC.', @ruolo_admin);
INSERT INTO utente VALUES(4,'Tullio','Mansi', '2002-02-20', 'MNS02TLL20A678D', '3409876321', 't.mansi@studenti.unisa.it', '321CBA.', @ruolo_user);

-- Indirizzi di default
INSERT INTO indirizzo VALUES(1, 1, @paese_italia, 'Avella', 'Via F. Vittoria', '12',  '83021', 'AV');
INSERT INTO indirizzo VALUES(2, 2, @paese_italia, 'Boscotrecase', 'Via Trecase', '1',  '80042', 'NA');
INSERT INTO indirizzo VALUES(3, 3, @paese_italia, 'Ottaviano', 'Via Terre Sperdute', '3',  '84010', 'NA');
INSERT INTO indirizzo VALUES(4, 4, @paese_italia, 'Ravello', 'Via Sulla Montagna', '4',  '84011', 'SA');
INSERT INTO indirizzo VALUES(5, 4, @paese_italia, 'Ravello', 'Via Sul Mare', '4B',  '84011', 'SA');

-- Prodotti di esempio
INSERT INTO prodotto VALUES (1,'noisy creek 2', 'box semimeccanica', 12, 29.99, @categoria_svapo, 'Argento');
INSERT INTO prodotto VALUES (2,'kiwi', 'pod entry level', 25, 79.99, @categoria_svapo, 'Rosa');
INSERT INTO prodotto VALUES (3,'voopoo drag 2', 'box completa', 5, 49.99, @categoria_svapo, 'Nero');
INSERT INTO prodotto VALUES (4,'smok nord', 'pod', 22, 69.99, @categoria_svapo, 'Nero');

-- Ordini di esempio
INSERT INTO ordine VALUES (1,4,4,36.58,'2023-05-08', @metodo_paypal); -- calcolato con iva 22
INSERT INTO ordine VALUES (2,4,5,7.58,'2023-05-08', @metodo_paypal); -- calcolato con iva 22
INSERT INTO ordine VALUES (3,4,4,73.17,'2023-05-08', @metodo_paypal); -- calcolato con iva 22

-- Contenuto ordini di esempio
INSERT INTO contenuto VALUES(1,1,1,29.99,22);
INSERT INTO contenuto VALUES(2,2,1,79.99,22);
INSERT INTO contenuto VALUES(3,1,2,29.99,22);
