#!/bin/bash
echo cleaning
rm *.class 2>/dev/null 1>/dev/null
rm *.jar 2>/dev/null 1>/dev/null

echo compiling
javac *.java

echo archiving
jar -cvf CircusConfig.jar ./*.class
mv CircusConfig.jar ../lib/
