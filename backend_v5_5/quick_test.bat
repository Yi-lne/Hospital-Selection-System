@echo off
REM ========================================
REM 快速测试脚本 - 验证所有修复
REM ========================================

chcp 65001 > nul
echo ========================================
echo 开始运行后端测试
echo ========================================
echo.

cd /d "%~dp0"

REM 设置UTF-8编码
set JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8
set MAVEN_OPTS=-Dfile.encoding=UTF-8

echo [1/4] 清理之前的构建...
call mvn clean -Dfile.encoding=UTF-8 -q
if %ERRORLEVEL% NEQ 0 goto :error

echo [2/4] 编译项目...
call mvn compile test-compile -Dfile.encoding=UTF-8 -DskipTests -q
if %ERRORLEVEL% NEQ 0 goto :error

echo [3/4] 运行Service测试...
call mvn test -Dtest=*ServiceTest -Dfile.encoding=UTF-8
if %ERRORLEVEL% NEQ 0 (
    echo Service测试有失败，继续运行Controller测试...
)

echo [4/4] 运行Controller测试...
call mvn test -Dtest=*ControllerTest -Dfile.encoding=UTF-8

echo.
echo ========================================
echo 测试完成！
echo ========================================
echo.
echo 详细报告请查看: target\surefire-reports\
echo.

goto :end

:error
echo.
echo ❌ 构建或测试过程中出现错误！
echo.

:end
pause
