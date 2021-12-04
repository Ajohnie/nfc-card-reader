  ## NFC CARD READER CONTROLLER
- This a javafx program that reads the ACR122U-A9 NFC card reader and updates firebase firestore
- It is meant to be a utility application to help web apps access the NFC card reader from a Windows PC
- USB access is blocked from the web browser and NFC is not supported either except on android 
- I build this app from the [JAVAFX app template](https://github.com/Ajohnie/demofx)

Installing
----------
- We are using Liberica JDK because it comes bundled with JAVAFX
- [Download Liberica 17 JDK](https://bell-sw.com/pages/liberica-native-image-kit/) 
  - Add environmental variables for the JDK, (there are many resources online for how to do this)
  - set JAVA_HOME=C:\Program Files\BellSoft\LibericaNIK-Full-21-OpenJDK-17 or the directory where you installed the Liberica JDK
  - set JAVA_PATH=%JAVA_HOME%\bin
  - Add %JAVA_HOME% to the global %PATH% variable so that it is accessible globally from the command line
- [Download The Latest Version of Maven](https://maven.apache.org/download.cgi)
  - set MAVEN_HOME=C:\Program Files\maven or wherever you installed your maven binaries
  - Add %MAVEN_PATH% to the global %PATH% variable so that it is accessible globally from the command line
  - Restart Your Computer so that the above changes take effect
  - git clone http://github.com/Ajohnie/nfc-card-reader
  - type cd nfc-card-reader
  - run mvn install
  
Building Executable App
----------
- Rub pack.bat to create jar file for your app
- Run link.bat(look at this file for more details) with OPTION 2 to create a custom runtime image
- Run image.bat(look at this file for more details) to create an executable file for your app

TODO
=============
- Replace the actions performed by the batch scripts with maven equivalents
- clean up used classes and reduce bundle size