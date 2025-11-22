@echo off
REM Saraye - Compile Script
REM Compiles all Java source files

echo ========================================
echo  Compiling Saraye Application...
echo ========================================

REM Clean bin directory
if exist bin (
    rmdir /s /q bin
)
mkdir bin

REM Compile Java files
echo Compiling Java source files...
javac -d bin -cp "lib/*" src/Main.java src/models/*.java src/ui/utils/*.java src/ui/controllers/*.java

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Compilation failed!
    pause
    exit /b 1
)

REM Copy resources
echo Copying resources...
mkdir bin\ui\views
mkdir bin\css
xcopy /Y src\ui\views\*.fxml bin\ui\views\
xcopy /Y resources\css\*.css bin\css\

echo.
echo [SUCCESS] Compilation completed successfully!
echo.
pause
