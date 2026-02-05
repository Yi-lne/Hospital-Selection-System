@echo off
cd /d "%~dp0"
echo Running UserControllerTest...
call mvn test -Dtest=UserControllerTest
echo.
echo Test completed.
pause
