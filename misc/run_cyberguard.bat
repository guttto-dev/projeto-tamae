@echo off

echo Ativando o ambiente virtual...
call .venv\Scripts\activate

javac misc\CyberGuard.java
java -cp misc CyberGuard
