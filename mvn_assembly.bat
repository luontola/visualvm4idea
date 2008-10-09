@echo off
call mvn clean package assembly:assembly && goto :eof
echo.
pause
