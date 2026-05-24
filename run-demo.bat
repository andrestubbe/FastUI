@echo off
setlocal
cd /d "%~dp0"

echo ===========================================
echo FastUI Demo (v0.1.0)
echo ===========================================
echo.
echo Launching: Visual Demo
echo.

:: Run the demo from the example folder using Maven
cd examples
echo Compiling and Launching Demo...
call mvn compile exec:java -Dexec.mainClass="fastui.VisualDemo"
if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Demo failed to launch. 
    echo Ensure you ran 'mvn clean install' at least once to install FastUI locally.
    pause
)

cd ..
