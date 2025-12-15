@echo off
REM Compile and run SPOT JavaFX Application

echo Compiling SPOTApplication.java...
javac SPOTApplication.java

if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo.
echo Compilation successful!
echo Starting SPOT Parking Management System...
echo.

java SPOTApplication

pause
