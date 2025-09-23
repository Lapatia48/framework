@echo off
setlocal enabledelayedexpansion

:: CONFIGURATION
set JAVA_HOME=C:\Program Files\Java\jdk-22
set TOMCAT_HOME=C:\Program Files\Apache Software Foundation\Tomcat 11.0

:: Nom du jar (change si besoin)
set app_name=framework

:: CHEMINS
set SRC_DIR=framework\java
set BUILD_DIR=framework\build
set JAR_DIR=dist
set JAR_FILE=%JAR_DIR%\%app_name%.jar

echo Nettoyage du dossier build et dist...
if exist "%BUILD_DIR%" rmdir /s /q "%BUILD_DIR%"
if exist "%JAR_DIR%" rmdir /s /q "%JAR_DIR%"
mkdir "%BUILD_DIR%"
mkdir "%JAR_DIR%"

echo Recherche et compilation de toutes les classes Java...
set FILES=
for /R "%SRC_DIR%" %%f in (*.java) do (
    set FILES=!FILES! "%%f"
)

javac -cp "%TOMCAT_HOME%\lib\servlet-api.jar" -d "%BUILD_DIR%" %FILES%

if %ERRORLEVEL% NEQ 0 (
    echo ERREUR: La compilation a echoue.
    exit /b 1
)

echo Creation du JAR %JAR_FILE%...
jar cf "%JAR_FILE%" -C "%BUILD_DIR%" .

echo Copie du JAR dans test\WEB-INF\lib...
if not exist "test\WEB-INF\lib" mkdir "test\WEB-INF\lib"
copy "%JAR_FILE%" "test\WEB-INF\lib\" /Y

echo Copie du projet test vers Tomcat\webapps...
rmdir /s /q "%TOMCAT_HOME%\webapps\test"
xcopy "test" "%TOMCAT_HOME%\webapps\test" /E /I /Y

echo Nettoyage des artefacts temporaires...
rmdir /s /q "%BUILD_DIR%"
rmdir /s /q "%JAR_DIR%"

echo Deploiement termine avec succes !
pause
