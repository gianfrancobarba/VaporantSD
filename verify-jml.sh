#!/bin/bash

cd "$(dirname "$0")"

# Classpath: classi compilate da Maven
CP="target/classes"

openjml -esc -cp "$CP" src/main/java/com/vaporant/repository/ProductModel.java