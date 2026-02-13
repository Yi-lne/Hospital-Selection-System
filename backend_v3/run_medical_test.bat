@echo off
cd /d D:\Projects\Software_Project\Hospital-Selection-System\backend
echo Running MedicalHistoryControllerTest...
mvn test -Dtest=MedicalHistoryControllerTest
echo Test completed with exit code: %ERRORLEVEL%
