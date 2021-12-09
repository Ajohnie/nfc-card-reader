@echo off

SET MODULES=ALL-MODULE-PATH
SET MODULE_PATH=out\jre\lib-deps
rem SET JAR_PATH=target\PsaveServer.jar
SET JAR_PATH=target\nfc-card-reader-1.0-SNAPSHOT-jar-with-dependencies.jar
rem SET RUN_JAR=outjre\bin\java --module-path=%MODULE_PATH% --add-modules=%MODULES% -jar %JAR_PATH%
SET RUN_JAR=out\jre\bin\java -jar %JAR_PATH%

%RUN_JAR%