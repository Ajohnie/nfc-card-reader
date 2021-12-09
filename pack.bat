@echo off
cd C:Pserver\api\JAVA_PROJECTS\nfc-card-reader
mvn clean compile assembly:single
rem use -U option to bypass cached artifact resolution failures
rem mvn clean compile assembly:single -U