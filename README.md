  ## NFC CARD READER CONTROLLER
- This a javafx app that reads the ACR122U-A9 NFC card reader and updates firebase firestore
- It is meant to be a utility application to help web apps access the NFC card reader from a Windows PC
- USB access is blocked from the web browser and NFC is not supported either except on android 
- It is based on the [JAVAFX app template](https://github.com/Ajohnie/demofx)

Installing
----------
- We are using Liberica JDK because it comes bundled with JAVAFX
- [Download and Install Liberica 17 JDK](https://bell-sw.com/pages/liberica-native-image-kit/) 
  - Add environmental variables for the JDK, (there are many resources online for how to do this)
  - set JAVA_HOME=C:\Program Files\BellSoft\LibericaNIK-Full-21-OpenJDK-17 or the directory where you installed the Liberica JDK
  - set JAVA_PATH=%JAVA_HOME%\bin
  - Add %JAVA_HOME% to the global %PATH% variable so that it is accessible globally from the command line
- [Download And Install The Latest Version of Maven](https://maven.apache.org/download.cgi)
  - set MAVEN_HOME=C:\Program Files\maven or wherever you installed your maven binaries
  - Add %MAVEN_PATH% to the global %PATH% variable so that it is accessible globally from the command line
  - Restart Your Computer so that the above changes take effect
  - git clone http://github.com/Ajohnie/nfc-card-reader
  - type cd nfc-card-reader
  - run mvn install
  - You will need a service account key for the app to connect to firebase
  - get it from here https://console.firebase.google.com/project/YOUR_PROJECT/settings/serviceaccounts/adminsdk
  - then copy its contents and paste them in a file named  serviceAccountKey.json under src/main/resources directory
  - Depending on the firebase authentication scheme you use, you might need to [register the application in firebase](https://console.cloud.google.com/apis/credentials)
- [Download And Install The Latest Version of Inno Setup](https://jrsoftware.org/isdl.php)
  - Add the installation directory(e.g "C:\Program Files (x86)\Inno Setup 6") to the global %PATH% variable so that it is accessible globally from the command line
- [Download And Install The Latest Version of Wix](https://wixtoolset.org/releases/)
  - Add the binaries' directory(e.g "C:\Program Files (x86)\WiX Toolset v3.11\bin") to the global %PATH% variable so that it is accessible globally from the command line

Building An Executable App
----------
- Run mvn package

TODO
----------
- reduce bundle size
