@REM
@REM Copyright (C) 2015 CMCC, Inc. and others. All rights reserved. (CMCC)
@REM
@REM Licensed under the Apache License, Version 2.0 (the "License");
@REM you may not use this file except in compliance with the License.
@REM You may obtain a copy of the License at
@REM
@REM         http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing, software
@REM distributed under the License is distributed on an "AS IS" BASIS,
@REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@REM See the License for the specific language governing permissions and
@REM limitations under the License.
@REM
@echo off
title dac-service

set RUNHOME=%~dp0
echo ### RUNHOME: %RUNHOME%

echo ### Starting emsdriver-service
cd /d %RUNHOME%

rem set JAVA_HOME=D:\JDK1.8\jdk\jdk\windows
set JAVA="%JAVA_HOME%\bin\java.exe"
set jvm_opts=-Xms50m -Xmx128m

if exist %JAVA_HOME%\jre\lib\i386 set jvm_opts=%jvm_opts% -Djava.library.path=%RUNHOME%conf\system\native\windows
if not exist %JAVA_HOME%\jre\lib\i386 set jvm_opts=%jvm_opts% -Djava.library.path=%RUNHOME%conf\system\native\windows-x86-64

rem set remote debug
rem set port=8306
rem set jvm_opts=%jvm_opts% -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=%port%,server=y,suspend=n

for /f "delims= " %%a in ('date /t') do (set date_string=%%a)
set date_time_string=%date_string%-%TIME%
set date_time_string=%date_time_string::=-%
set date_time_string=%date_time_string: =-%
set date_time_string=%date_time_string:/=-%
set date_time_string=%date_time_string:\=-%
set date_time_string=%date_time_string:<=-%
set date_time_string=%date_time_string:?=-%
set date_time_string=%date_time_string:"=-%
set date_time_string=%date_time_string:>=-%
set date_time_string=%date_time_string:|=-%

set jvm_opts=%jvm_opts% -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=%RUNHOME%logs\dump-dac-%date_time_string%.hprof

rem set class_path=%RUNHOME%;%RUNHOME%boco-driver.jar;%RUNHOME%/3rdParty/evel_javalib2-0.0.1.jar
set class_path=%RUNHOME%;%RUNHOME%boco-driver.jar
echo ### jvm_opts: %jvm_opts%
echo ### class_path: %class_path%

%JAVA% -classpath %class_path% %jvm_opts% org.onap.vfc.nfvo.emsdriver.EmsDriverApplication  server %RUNHOME%conf/emsdriver.yml

IF ERRORLEVEL 1 goto showerror
exit
:showerror
echo WARNING: Error occurred during startup or Server abnormally stopped by way of killing the process,Please check!
echo After checking, press any key to close
rem pause
exit
