#!/bin/bash
echo cleaning
rm ./CircusCommunication/*.class 2>/dev/null 1>/dev/null
rm ./lib/CircusCommunication.jar 2>/dev/null 1>/dev/null

echo compiling
javac ./CircusCommunication/*.java

echo archiving
jar -cvf ./lib/CircusCommunication.jar ./CircusCommunication/*.class
#mv CircusComm.jar ../lib/
