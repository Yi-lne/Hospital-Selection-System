@echo off
cd /d D:\Projects\Software_Project\Hospital-Selection-System\backend
echo ========================================
echo Running Backend Tests
echo ========================================
echo.

call mvn clean test -Dtest=UserControllerTest,DoctorControllerTest,MessageControllerTest,CollectionControllerTest

echo.
echo ========================================
echo Test Results
echo ========================================
echo.

dir /b target\surefire-reports\TEST-*.xml 2>nul
echo.

for %%f in (target\surefire-reports\TEST-*.xml) do (
    echo %%f
    findstr /C:"tests run" "%%f"
    echo.
)

echo ========================================
echo Done
echo ========================================
pause
