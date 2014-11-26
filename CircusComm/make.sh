#!/bin/bash
echo cleaning
rm *.class 2>/dev/null 1>/dev/null
rm *.jar 2>/dev/null 1>/dev/null

echo compiling
javac *.java

echo archiving
jar -cvf CircusComm.jar ./*.class
mv CircusComm.jar ../lib/
