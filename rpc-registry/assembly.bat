@echo off
echo ------------------------------------
echo Building : "clean compile assembly:single" .
echo current path is : %~dp0
echo ------------------------------------
set MVN=mvn
set MAVEN_OPTS=%MAVEN_OPTS% -XX:MaxPermSize=128m

pushd %~dp0

call %MVN%  clean compile assembly:single
if errorlevel 1 goto error

echo ------------------------------------
echo Assemble Success !
echo ------------------------------------

goto end


:error
echo Error Happen!!!
:end
pause