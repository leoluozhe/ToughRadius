@echo off
if "%OS%" == "Windows_NT" setlocal
title Shut RADIUS Server

rem ---------------------------------------------------------------------------
rem Shut RADIUS Server
rem
rem $Id: shutdown.bat $
rem ---------------------------------------------------------------------------

if exist "..\jdk1.6.0_37" (
    SET PROJ_HOME=..\jdk1.6.0_37
) else (
    SET PROJ_HOME=%JAVA_HOME%
)

if exist "%PROJ_HOME%\bin\radiusd.exe" goto exec

if not exist "%PROJ_HOME%\bin\java.exe" goto end


:exec
echo ===========================
echo  shutdown RADIUS Server
echo ===========================

"%PROJ_HOME%\bin\java" -server -classpath "./lib/radiusd.jar" org.toughradius.Shutdown

echo ===========================
echo done
echo ===========================

:end
