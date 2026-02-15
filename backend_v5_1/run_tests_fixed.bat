@echo off
chcp 65001 > nul
echo ========================================
echo 编译并运行新创建的测试
echo ========================================
echo.

cd /d "%~dp0"

echo 设置编译环境...
set JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8

echo.
echo 清理之前的构建...
call mvn clean -Dfile.encoding=UTF-8

echo.
echo 编译测试源文件...
call mvn test-compile -Dfile.encoding=UTF-8 -Dproject.build.sourceEncoding=UTF-8

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ❌ 编译失败！
    echo.
    goto :end
)

echo.
echo 运行RoleServiceTest...
call mvn test -Dtest=RoleServiceTest -Dfile.encoding=UTF-8

echo.
echo 运行FilterServiceTest...
call mvn test -Dtest=FilterServiceTest -Dfile.encoding=UTF-8

echo.
echo 运行所有Controller测试...
call mvn test -Dtest=*ControllerTest -Dfile.encoding=UTF-8

:end
echo.
echo ========================================
echo 测试运行完成！
echo ========================================
echo.
echo 测试报告位置: target\surefire-reports\
echo.

pause
