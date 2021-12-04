@echo off
cd C:PlServer\api\JAVA_PROJECTS\demofx
rem no need for input dir for now, but you can put files to be shipped with the app bundle there
rem like additional jar files etc
SET INPUT_DIR="target\inputs"
SET OUTPUT_DIR="target\outputs"
SET JRE_DIR="target\jre"
SET APP_NAME="demofx"
SET MAIN_JAR_PATH="target"
SET MAIN_JAR="demofx-1.0-SNAPSHOT-jar-with-dependencies.jar"
SET MAIN_CLASS="com.example.demofx.HelloApplication"
SET MODULE_PATH="%JAVA_HOME%/jmods"
SET APP_MODULES=java.base,javafx.controls,javafx.graphics,java.datatransfer,java.desktop,java.scripting,java.xml
rem -Xmx2048m
SET JAVA_OPTIONS=""
SET APP_DESCRIPTION="demo javafx app"
SET APP_COPYRIGHT="© 2021 ripple solutions"
SET APP_VERSION="1.0"
SET APP_TYPE="exe"
SET ABOUT_URL="https://github.com/Ajohnie/demofx"
rem get UUID form https://www.uuidgenerator.net/
SET APP_UUID="fbff31d4-0729-4bdf-aa68-576bb3bf966b"
SET APP_UPDATE_URL="https://github.com/Ajohnie/demofx"
SET APP_INSTALL_DIR=%APP_NAME%
SET APP_LICENSE_FILE="C:PlServer\license.rtf"
SET APP_RESOURCE_DIR="src/main/resources"
SET APP_MENU_GROUP="Ripple Solutions"
SET APP_ICON="%APP_RESOURCE_DIR%\icons\256x256.ico"
SET APP_VENDOR=%APP_MENU_GROUP%
SET COPY_JAR=xcopy "%MAIN_JAR_PATH%\%MAIN_JAR%" "%INPUT_DIR%\%MAIN_JAR%" /Y
SET PACKAGE_APP=jpackage --input %INPUT_DIR% --dest %OUTPUT_DIR% --name %APP_NAME% --main-jar %MAIN_JAR% --main-class %MAIN_CLASS% --module-path %MODULE_PATH% --add-modules %APP_MODULES% --java-options %JAVA_OPTIONS% --description %APP_DESCRIPTION% --copyright %APP_COPYRIGHT% --app-version %APP_VERSION% --type %APP_TYPE% --icon %APP_ICON% --about-url %ABOUT_URL% --install-dir %APP_INSTALL_DIR% --license-file %APP_LICENSE_FILE% --resource-dir %APP_RESOURCE_DIR% --win-upgrade-uuid %APP_UUID% --win-update-url %APP_UPDATE_URL% --win-menu-group %APP_MENU_GROUP% --vendor %APP_VENDOR% --win-menu --win-help-url --win-dir-chooser --win-per-user-install --win-shortcut --win-shortcut-prompt --verbose
rem option for using own runtime with out module specification
SET PACKAGE_APP_WITH_JRE=jpackage --input %INPUT_DIR% --dest %OUTPUT_DIR% --runtime-image %JRE_DIR% --name %APP_NAME% --main-jar %MAIN_JAR% --main-class %MAIN_CLASS% --java-options %JAVA_OPTIONS% --description %APP_DESCRIPTION% --copyright %APP_COPYRIGHT% --app-version %APP_VERSION% --type %APP_TYPE% --icon %APP_ICON% --about-url %ABOUT_URL% --install-dir %APP_INSTALL_DIR% --license-file %APP_LICENSE_FILE% --resource-dir %APP_RESOURCE_DIR% --win-upgrade-uuid %APP_UUID% --win-update-url %APP_UPDATE_URL% --win-menu-group %APP_MENU_GROUP% --vendor %APP_VENDOR% --win-menu --win-help-url --win-dir-chooser --win-per-user-install --win-shortcut --win-shortcut-prompt --verbose
%COPY_JAR%
rem OPTION 1
rem I tried using the first option where I let jpackage create the runtime but the resulting bin directory
rem was missing java.exe and therefore the application couldn't run
rem %PACKAGE_APP%

rem OPTION 2
rem first run pack.bat to create the jar file under target directory, then link.bat to create a runtime if you want your own runtime
%PACKAGE_APP_WITH_JRE%
