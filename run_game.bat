@echo off
echo ========================================
echo LibGDX Game Launcher
echo ========================================
echo.

REM Change to your project directory
REM Edit the path below to match your project location
cd /d "C:\Users\Stephanie\Downloads\OOP_P2_Final\OOP_P2_Final\screen_entity_managers\oop_proj"

echo Cleaning previous build...
call gradlew.bat clean
echo.

echo Building project...
call gradlew.bat build
echo.

echo Running game...
call gradlew.bat lwjgl3:run
echo.

echo ========================================
echo Game closed
echo ========================================
pause
