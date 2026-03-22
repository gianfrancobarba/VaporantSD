#!/bin/bash

cd "$(dirname "$0")"

# Classpath: classi compilate + tutte le dipendenze Maven
echo "Risoluzione classpath Maven..."
DEPS=$(mvn -q dependency:build-classpath -DincludeScope=compile -Dmdep.outputFile=/tmp/jml_cp.txt && cat /tmp/jml_cp.txt)
CP="target/classes:${DEPS}"

# Definizione dei file da verificare
FILES=(
    "src/main/java/com/vaporant/model/ProductBean.java"
    "src/main/java/com/vaporant/model/Cart.java"
    "src/main/java/com/vaporant/model/OrderBean.java"
    "src/main/java/com/vaporant/model/UserBean.java"
    "src/main/java/com/vaporant/model/AddressBean.java"
    "src/main/java/com/vaporant/model/AddressList.java"
)

# Modalità di verifica: -esc (formale, lento) o -check (sintassi, veloce)
MODE="-esc"

# Esecuzione OpenJML per ogni file
for FILE in "${FILES[@]}"; do
    echo "--------------------------------------------------"
    echo "Verifica [$MODE] di $FILE..."
    openjml $MODE -progress -cp "$CP" "$FILE"
done