@echo off
setlocal
REM Writes local.properties with the Windows SDK path
set "REPO=%~dp0.."
for %%I in ("%REPO%") do set "REPO=%%~fI"
> "%REPO%\local.properties" echo sdk.dir=C:\Users\jcron\AppData\Local\Android\Sdk
echo Wrote %REPO%\local.properties with Windows SDK path
exit /b 0

