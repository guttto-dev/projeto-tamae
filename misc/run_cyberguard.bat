@echo off

echo Ativando o ambiente virtual...
call .venv\Scripts\activate

echo Iniciando...
javac misc\CyberGuard.java
java -cp misc CyberGuard
