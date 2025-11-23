@echo off
REM Saraye - Run Script
REM Runs the compiled Saraye application with JavaFX SDK

echo ========================================
echo  Running Saraye Application...
echo ========================================
echo.

REM Check if compiled
if not exist bin\Main.class (
    echo [ERROR] Application not compiled! Run compile.bat first.
    pause
    exit /b 1
)

REM Set JavaFX SDK path - Using YOUR JavaFX location
set JAVAFX_PATH=D:\Java\javafx-sdk-21.0.9\lib

echo Using JavaFX SDK from: %JAVAFX_PATH%
echo.

REM Run the application
java --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.fxml -cp "bin;lib/*;D:/Java/lib/mssql-jdbc-13.2.1.jre8.jar" Main

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Application failed to start!
    pause
    exit /b 1
)
