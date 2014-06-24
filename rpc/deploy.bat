@echo off
echo ------------------------------------
echo Deploying : "mvn clean install source:jar deploy -Dmaven.test.skip=true" .
echo current path is : %~dp0
echo ------------------------------------
set MVN=mvn
set MAVEN_OPTS=%MAVEN_OPTS% -XX:MaxPermSize=128m

pushd %~dp0

call %MVN%  clean install source:jar deploy -Dmaven.test.skip=true
if errorlevel 1 goto error

echo ------------------------------------
echo Deploy Success !
echo ------------------------------------

goto end


:error
echo Error Happen!!!
:end
pause