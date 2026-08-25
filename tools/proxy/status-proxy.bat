@echo off
REM Show proxy status
echo === Port 443 listeners ===
netstat -ano | findstr ":443 " | findstr "LISTENING"
echo.
echo === hosts mappings ===
findstr /i "piston-meta piston-data" C:\Windows\System32\drivers\etc\hosts
echo.
echo === Connectivity test ===
curl.exe -k -s -o NUL -w "piston-meta -> HTTP %%{http_code}\n" --max-time 10 https://piston-meta.mojang.com/mc/game/version_manifest_v2.json 2>nul
if errorlevel 1 echo Proxy not running or error
