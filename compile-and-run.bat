@echo off
REM Saraye - Compile and Run Script
REM Compiles and runs the application in one step

echo ========================================
echo  Saraye - Property Rental Platform
echo ========================================
echo.

REM Compile
call compile.bat

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Cannot run - compilation failed!
    exit /b 1
)

echo.
echo ========================================
echo  Starting Application...
echo ========================================
echo.

REM Set JavaFX SDK path - Using YOUR JavaFX location
set JAVAFX_PATH=C:\Java\javafx-sdk-21.0.9\lib

echo Using JavaFX SDK from: %JAVAFX_PATH%
echo.

REM Run
java --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.fxml -cp "bin;lib/*" Main
