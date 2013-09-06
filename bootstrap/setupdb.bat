@echo off
if "%OS%" == "Windows_NT" setlocal
title Shut DHCP Server

rem ---------------------------------------------------------------------------
rem Shut DHCP Server
rem
rem $Id: shutdown.bat $
rem ---------------------------------------------------------------------------

if exist "..\jdk1.6.0_37" (
    SET PROJ_HOME=..\jdk1.6.0_37
) else (
    SET PROJ_HOME=%JAVA_HOME%
)

if exist "%PROJ_HOME%\bin\java.exe" goto exec

if not exist "%PROJ_HOME%\bin\java.exe" goto end


:exec
echo ===========================
echo start init database
echo ===========================

SET CLASS_PATH="./lib/hsqldb.jar";"./lib/radiusd.jar"

"%PROJ_HOME%\bin\java" -server -classpath %CLASS_PATH% org.toughradius.SetupDB

echo ===========================
echo done
echo ===========================

:end
