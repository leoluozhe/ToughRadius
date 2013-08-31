@echo off
if "%OS%" == "Windows_NT" setlocal
title RADIUS Server

rem ---------------------------------------------------------------------------
rem Start radiusd
rem
rem $Id: startup.bat $
rem ---------------------------------------------------------------------------

if exist "..\jdk1.6.0_37" (
    SET PROJ_HOME=..\jdk1.6.0_37
) else (
    SET PROJ_HOME=%JAVA_HOME%
)

echo "PROJ_HOME=%PROJ_HOME%"

if exist "%PROJ_HOME%\bin\radiusd.exe" goto exec

if not exist "%PROJ_HOME%\bin\java.exe" goto end

copy "%PROJ_HOME%\bin\java.exe" "%PROJ_HOME%\bin\radiusd.exe"

:exec
rem define CLASSPATH
SET CLASS_PATH="%PROJ_HOME%/lib/dt.jar";"%PROJ_HOME%/lib/tools.jar";"./bootstrap.jar"

rem debug
SET DEBUG_CONFIG=-Xdebug -Xrunjdwp:transport=dt_socket,address=11815,server=y,suspend=n

"%PROJ_HOME%\bin\radiusd" -server -Xms64m -Xmx1024m %DEBUG_CONFIG% -classpath %CLASS_PATH% org.toughradius.Bootstrap org.toughradius.Project

echo ===========================
echo done
echo ===========================

:end

pause