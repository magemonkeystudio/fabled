set /p Input=Enter the Spigot Version (Example: 1.19): 
call mvn clean install exec:java -Dspigot-version=%Input% -Dexec.args=%Input%
pause
