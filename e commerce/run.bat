@echo off
echo Compiling E-Commerce application...
call mvn clean compile

if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo.
echo Starting server on http://localhost:8080
echo.
call mvn exec:java -Dexec.mainClass="com.ecommerce.api.ECommerceServer"

pause

