@echo off

SET MODULES=ALL-MODULE-PATH
SET MODULE_PATH=target\jre\lib-deps
rem SET JAR_PATH=target\PsaveServer.jar
SET JAR_PATH=target\demofx-1.0-SNAPSHOT-jar-with-dependencies.jar
rem SET RUN_JAR=target\jre\bin\java --module-path=%MODULE_PATH% --add-modules=%MODULES% -jar %JAR_PATH%
SET RUN_JAR=target\jre\bin\java -jar %JAR_PATH%

%RUN_JAR%