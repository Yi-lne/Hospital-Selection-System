@echo off
set MAVEN_HOME=d:\maven\apache-maven-3.9.9
set PATH=%MAVEN_HOME%\bin;%PATH%
call mvn clean test > full_test_output.txt 2>&1
echo Done! Exit code: %ERRORLEVEL%
