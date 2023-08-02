@ECHO off
set /p Input=Enter the Spigot Version (Example: 1.19): 
set /p Args=Enter any additional arguments: 
call mvn clean install exec:java -Dspigot-version=%Input% -Dexec.args="%Input% %Args%"
pause
