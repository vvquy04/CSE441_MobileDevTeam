@REM Licensed to the Apache Software Foundation (ASF)
@REM Maven Wrapper script for Windows
@echo off

set MAVEN_WRAPPER_JAR="%~dp0\.mvn\wrapper\maven-wrapper.jar"
set WRAPPER_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"
set WRAPPER_PROPERTIES="%~dp0\.mvn\wrapper\maven-wrapper.properties"

if not exist "%~dp0\.mvn\wrapper" mkdir "%~dp0\.mvn\wrapper"

if not exist %MAVEN_WRAPPER_JAR% (
    echo Downloading Maven Wrapper...
    powershell -Command "Invoke-WebRequest -Uri %WRAPPER_URL% -OutFile %MAVEN_WRAPPER_JAR%"
)

set JAVA_EXE=java
if defined JAVA_HOME set JAVA_EXE="%JAVA_HOME%\bin\java"

%JAVA_EXE% -jar %MAVEN_WRAPPER_JAR% %*
