@echo off
REM ============================================================
REM  Stop the Mojang mirror proxy and remove the hosts mappings.
REM  NOTE: stopping must also remove the hosts entries, otherwise
REM  piston-meta/piston-data keep pointing to 127.0.0.1 and break.
REM  Usage: stop-proxy.bat
REM ============================================================
setlocal
cd /d "%~dp0"

REM 1) Kill the process listening on 443 (the proxy)
for /f "tokens=5" %%p in ('netstat -ano ^| findstr ":443 " ^| findstr "LISTENING"') do (
  echo Killing proxy PID %%p
  taskkill /F /PID %%p >nul 2>&1
)
netstat -ano | findstr ":443 " | findstr "LISTENING" >nul
if errorlevel 1 ( echo [OK] Proxy stopped, port 443 released ) else ( echo [WARN] Something still listens on 443 )

REM 2) Remove the two hosts lines
powershell -NoProfile -Command "$h='C:\Windows\System32\drivers\etc\hosts'; (Get-Content $h) | Where-Object { $_ -notmatch 'piston-meta\.mojang\.com' -and $_ -notmatch 'piston-data\.mojang\.com' } | Set-Content $h -Encoding ascii; Write-Output 'hosts: mappings removed'"

REM 3) Flush DNS cache
ipconfig /flushdns >nul 2>&1
echo [OK] DNS flushed, Mojang back to direct
endlocal
