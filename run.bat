@echo off
setlocal EnableDelayedExpansion

set "SRC_DIR=src\main\java"
set "OUT_DIR=build\classes"

if not exist "%OUT_DIR%" mkdir "%OUT_DIR%"

echo Compiling Java sources...
javac -d "%OUT_DIR%" -sourcepath "%SRC_DIR%" "%SRC_DIR%\notaumkm\Main.java"
if errorlevel 1 goto compile_error

echo Running notaumkm.Main...
set "CLASSPATH=%OUT_DIR%"
if exist lib\* set "CLASSPATH=%CLASSPATH%;lib\*"
start "NotaUMKM" javaw -cp "%CLASSPATH%" notaumkm.Main
goto end

:compile_error
echo.
echo Kompilasi gagal. Periksa pesan error di atas.
exit /b 1

:end
endlocal
