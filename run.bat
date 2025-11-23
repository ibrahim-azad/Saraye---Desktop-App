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

REM Set JavaFX SDK path - Using installed JavaFX SDK
set JAVAFX_PATH=C:\Java\javafx-sdk-21.0.9\lib

echo Using JavaFX SDK from: %JAVAFX_PATH%
echo.

REM Run the application
java -Dprism.order=sw --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.fxml -cp "bin;lib/*" Main

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Application failed to start!
    pause
    exit /b 1
)
