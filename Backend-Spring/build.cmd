@echo off
setlocal

set "JAVA_HOME=C:\Program Files\Java\jdk-17"
set "PATH=%JAVA_HOME%\bin;%PATH%"

set "MAVEN_VERSION=3.9.9"
set "MAVEN_DIR=%~dp0.maven"
set "MAVEN_ZIP=%MAVEN_DIR%\maven.zip"
set "MAVEN_HOME=%MAVEN_DIR%\apache-maven-%MAVEN_VERSION%"

if exist "%MAVEN_HOME%\bin\mvn.cmd" goto :run

echo Downloading Apache Maven %MAVEN_VERSION%...
if not exist "%MAVEN_DIR%" mkdir "%MAVEN_DIR%"
powershell -Command "Invoke-WebRequest -Uri 'https://dlcdn.apache.org/maven/maven-3/%MAVEN_VERSION%/binaries/apache-maven-%MAVEN_VERSION%-bin.zip' -OutFile '%MAVEN_ZIP%'"
if errorlevel 1 (
    echo Trying archive URL...
    powershell -Command "Invoke-WebRequest -Uri 'https://archive.apache.org/dist/maven/maven-3/%MAVEN_VERSION%/binaries/apache-maven-%MAVEN_VERSION%-bin.zip' -OutFile '%MAVEN_ZIP%'"
)

echo Extracting...
powershell -Command "Expand-Archive -Path '%MAVEN_ZIP%' -DestinationPath '%MAVEN_DIR%' -Force"
del "%MAVEN_ZIP%" 2>nul

:run
"%MAVEN_HOME%\bin\mvn.cmd" %*
