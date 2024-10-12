#!/usr/bin/env sh

set -e

echo "Ativando o ambiente virtual..."
. .venv/bin/activate

echo "Iniciando..."
javac misc/CyberGuard.java
java -cp misc CyberGuard
