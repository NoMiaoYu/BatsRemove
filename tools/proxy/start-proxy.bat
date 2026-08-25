@echo off
REM ============================================================
REM  Start the Mojang mirror proxy (piston-meta/piston-data -> bmclapi)
REM  Usage: start-proxy.bat
REM ============================================================
setlocal enabledelayedexpansion
cd /d "%~dp0"

REM 1) Make sure hosts contains the two mappings
powershell -NoProfile -Command "$h='C:\Windows\System32\drivers\etc\hosts'; $c=Get-Content $h -Raw; $lines=@('127.0.0.1 piston-meta.mojang.com','127.0.0.1 piston-data.mojang.com'); $add=$lines | Where-Object { -not $c.Contains(($_ -split ' ')[1]) }; if($add){ Add-Content -Path $h -Value ($add -join \"`n\") -Encoding ascii; Write-Output 'hosts: added' } else { Write-Output 'hosts: already present' }"

REM 2) Check if port 443 is already in use
netstat -ano | findstr ":443 " | findstr "LISTENING" >nul
if not errorlevel 1 (
  echo [WARN] Port 443 already in use. Proxy may be running. Run stop-proxy.bat first.
  exit /b 0
)

REM 3) Start node proxy, log to proxy.log
echo Starting proxy...
start "mcproxy" /min cmd /c "node proxy.js > proxy.log 2>&1"

REM 4) Wait and verify
timeout /t 2 /nobreak >nul
netstat -ano | findstr ":443 " | findstr "LISTENING" >nul
if not errorlevel 1 (
  echo [OK] Proxy started, listening on 127.0.0.1:443
  echo      Test: curl -k https://piston-meta.mojang.com/mc/game/version_manifest_v2.json
) else (
  echo [FAIL] No listener on 443. Check proxy.log
)
endlocal
