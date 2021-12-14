@echo off
cd C:Pserver\api\JAVA_PROJECTS\nfc-card-reader
SET JAR_FILE=target\nfc-card-reader-1.0-SNAPSHOT-jar-with-dependencies.jar
rem use javafx version in your pom and runtime config
SET ALL_MODULE_PATH="C:\Program Files\BellSoft\LibericaNIK-Full-21-OpenJDK-17\jmods"
SET MODULES=ALL-MODULE-PATH
rem print-deps shows concise list
SET OPERATION=list-deps
rem --ignore-missing-deps
rem SET OPERATION=print-module-deps --ignore-missing-deps
SET DEP_LIST=target\deps-list.txt
SET ACTION_DEP=jdeps --module-path=%ALL_MODULE_PATH%  --add-modules=%MODULES% --%OPERATION% %JAR_FILE%

rem this file is relative to the paths specified
SET MODULE_PATH=target\lib-deps
SET COPY_MODULES=Xcopy /E /I %ALL_MODULE_PATH% %MODULE_PATH%

rem "C:Pserver\api\JAVA_PROJECTS\nfc-card-reader\outjre"
SET OUTPUT_PATH=--output out\jre
SET LINK_OPTIONS= --no-header-files --no-man-pages --compress=2 --strip-debug
rem get link modules after running %ACTION_DEP%
rem SET LINK_MODULES= --add-modules  java.base,javafx.controls,javafx.fxml,javafx.base,javafx.graphics,java.logging,java.xml,java.smartcardio,java.net.http
SET LINK_MODULES= --add-modules javafx.fxml,javafx.graphics,javafx.controls,javafx.base,java.smartcardio,com.jfoenix,google.cloud.firestore,google.cloud.core,com.google.auth.oauth2,firebase.admin,com.google.auth
rem SET ACTION_LINK=jlink %LINK_OPTIONS% %LINK_MODULES% %OUTPUT_PATH% --module-path %MODULE_PATH%
SET ACTION_LINK=jlink %LINK_OPTIONS% %LINK_MODULES% %OUTPUT_PATH% --module-path %MODULE_PATH%
rem SET ACTION_LINK=jlink %LINK_OPTIONS% %LINK_MODULES% %OUTPUT_PATH%

echo OPTION 1 -- generate a list of module dependencies for use by the --add-modules option
echo OPTION 1.1 -- print out module dependencies
echo the modules will be stored in the file %DEP_LIST%

echo OPTION 2 -- use jlink to create a custom runtime image
echo OPTION 2.1 --copy modules to the module path %MODULE_PATH% and then do option2

echo enter option (allowed values are option1, option1.1, option2, option2.1)
SET /P ACTION=

if %ACTION%=="" (
   goto end
)

goto %ACTION%
goto end

:option1
SET OPERATION=list-deps
rem --ignore-missing-deps
%ACTION_DEP%>>%DEP_LIST%
echo list of module dependencies was stored in %DEP_LIST%
goto end

:option1.1
SET OPERATION=print-module-deps
rem --ignore-missing-deps
%ACTION_DEP%>>%DEP_LIST%
echo print out of module dependencies was stored in %DEP_LIST%
goto end

:option2
rmdir /S /Q %OUTPUT_PATH%
%ACTION_LINK%
echo Java runtime was generated and stored in %OUTPUT_PATH%
goto end

rem earlier while trying this out with a non modular version, I had to copy modules to the module path %MODULE_PATH%
:option2.1
rmdir /S /Q %OUTPUT_PATH%
%ACTION_LINK% && %COPY_MODULES%
echo Java runtime was generated and stored in %OUTPUT_PATH%
goto end

:end
pause
