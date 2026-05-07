@echo off
echo Building FastUI2...
call mvn clean install -DskipTests

echo Launching Visual Demo...
call mvn exec:java -Dexec.mainClass="fastui.VisualDemo"
pause
