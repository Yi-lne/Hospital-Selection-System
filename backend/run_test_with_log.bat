@echo off
cd /d D:\Projects\Software_Project\Hospital-Selection-System\backend
echo ========================================
echo Running CollectionControllerTest
echo ========================================
call mvn clean test -Dtest=CollectionControllerTest#testCheckCollected_True -X
echo.
echo ========================================
echo Exit Code: %ERRORLEVEL%
echo ========================================
pause
