@echo off
cd C:PlServer\api\JAVA_PROJECTS\demofx
SET JAR_FILE=target\demofx-1.0-SNAPSHOT-jar-with-dependencies.jar
rem use javafx version in your pom and runtime config
SET ALL_MODULE_PATH="C:\Program Files\BellSoft\LibericaNIK-Full-21-OpenJDK-17\jmods"
SET MODULES=ALL-MODULE-PATH
rem print-deps shows concise list
SET OPERATION=list-deps
rem SET OPERATION=print-module-deps
SET DEP_LIST=target\deps-list.txt
SET ACTION_DEP=jdeps --module-path=%ALL_MODULE_PATH%  --add-modules=%MODULES% --%OPERATION% %JAR_FILE%

rem "C:PlServer\api\JAVA_PROJECTS\demofx\target\jre"
SET OUTPUT_PATH=--output target\jre
SET LINK_OPTIONS= --no-header-files --no-man-pages --compress=2 --strip-debug
rem get link modules after running %ACTION_DEP%
SET LINK_MODULES= --add-modules  java.base,javafx.controls,javafx.fxml,javafx.graphics
SET ACTION_LINK=jlink %LINK_OPTIONS% %LINK_MODULES% %OUTPUT_PATH%

rem this file is relative to the paths specified
SET MODULE_PATH=target\jre\lib-deps
SET COPY_MODULES=Xcopy /E /I %ALL_MODULE_PATH% %MODULE_PATH%

rem OPTION 1 -- generate a list of module dependencies for use by the --add-modules option
rem the modules will be stored in the file %DEP_LIST%
rem %ACTION_DEP%>>%DEP_LIST%

rem OPTION 2 -- use jlink to create a custom runtime image
rmdir /S /Q %OUTPUT_PATH%
rem earlier while trying this out with a non modular version, I had to copy modules to the module path %MODULE_PATH%
rem %ACTION_LINK% && %COPY_MODULES%
%ACTION_LINK%