@echo off
cd test

if not exist classes mkdir classes

:: Compiler
javac -cp "..\framework\java" -d classes controller\*.java

:: Lancer
java -cp "classes;..\framework\java\classes" controller.Main