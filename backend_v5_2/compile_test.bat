@echo off
cd /d D:\Projects\Software_Project\Hospital-Selection-System\backend
echo Running Maven test compile...
call mvn clean compile test-compile
echo.
echo Exit code: %ERRORLEVEL%
echo.
echo Checking for CollectionControllerTest class...
dir target\test-classes\com\chen\HospitalSelection\controller\CollectionControllerTest.class 2>nul
if %ERRORLEVEL% EQU 0 (
    echo SUCCESS: CollectionControllerTest compiled successfully!
) else (
    echo FAILED: CollectionControllerTest not found
)
pause
