@echo off
REM ============================================================
REM  Full revert: stop proxy + remove hosts + delete proxy cert
REM  from JDK trust stores. Use with care: rebuilding afterwards
REM  requires running start-proxy.bat and re-importing the cert.
REM  Usage: revert-proxy.bat
REM ============================================================
setlocal
cd /d "%~dp0"

REM 1) Stop proxy
for /f "tokens=5" %%p in ('netstat -ano ^| findstr ":443 " ^| findstr "LISTENING"') do (
  taskkill /F /PID %%p >nul 2>&1
)
echo [1/3] Proxy stopped

REM 2) Remove hosts
powershell -NoProfile -Command "$h='C:\Windows\System32\drivers\etc\hosts'; (Get-Content $h) | Where-Object { $_ -notmatch 'piston-meta\.mojang\.com' -and $_ -notmatch 'piston-data\.mojang\.com' } | Set-Content $h -Encoding ascii"
ipconfig /flushdns >nul 2>&1
echo [2/3] Hosts mappings removed

REM 3) Delete proxy cert from JDK trust stores
set "KT17=C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot\bin\keytool.exe"
set "KT21=C:\Program Files\Microsoft\jdk-21.0.12.101-hotspot\bin\keytool.exe"
if exist "%KT17%" ( "%KT17%" -delete -alias mcproxy -keystore "C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot\lib\security\cacerts" -storepass changeit >nul 2>&1 && echo [3/3] JDK17 cert removed || echo [3/3] No JDK17 cert )
if exist "%KT21%" ( "%KT21%" -delete -alias mcproxy -keystore "C:\Program Files\Microsoft\jdk-21.0.12.101-hotspot\lib\security\cacerts" -storepass changeit >nul 2>&1 && echo [3/3] JDK21 cert removed || echo [3/3] No JDK21 cert )

echo.
echo Fully reverted. To build again, run start-proxy.bat and re-import the cert.
endlocal
