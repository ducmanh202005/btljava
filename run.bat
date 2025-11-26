@echo off
echo Compiling Space Invaders...

REM Compile
javac -encoding UTF-8 -d out/production/BTLJAVA -sourcepath src/main/java src/main/java/spaceinvaders/*.java

if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo Copying resources...
REM Copy resources
xcopy /E /I /Y src\main\resources out\production\BTLJAVA

echo Running game...
REM Run
cd out\production\BTLJAVA
java spaceinvaders.Main

cd ..\..\..
pause