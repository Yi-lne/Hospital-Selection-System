@echo off
chcp 65001 > nul
echo ========================================
echo 运行所有后端测试
echo ========================================
echo.

cd /d "%~dp0"

echo 清理之前的构建...
call mvn clean

echo.
echo 编译并运行所有测试...
call mvn test -Dfile.encoding=UTF-8 -Dproject.build.sourceEncoding=UTF-8

echo.
echo ========================================
echo 测试运行完成！
echo ========================================
echo.
echo 请查看 target\surefire-reports 目录中的测试报告
echo.

pause
