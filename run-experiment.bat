@echo off
setlocal
cd /d "%~dp0"

echo ===========================================
echo FastUI Spatial Experiment
echo ===========================================
echo.
echo Launching: Spatial Experiment
echo.

:: Run the experiment from the example folder using Maven
cd examples
echo Compiling and Launching Experiment...
call mvn compile exec:java -Dexec.mainClass="fastui.SpatialExperiment"
if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Experiment failed to launch. 
    echo Ensure you ran 'mvn clean install' at least once to install FastUI locally.
    pause
)

cd ..
