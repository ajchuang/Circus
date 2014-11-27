#!/bin/bash
echo cleaning
rm *.class

echo compiling
javac -cp './:./lib/CircusComm.jar:./lib/CircusConfig.jar' CPSwitch.java Circus.java DebugInterface.java CPTable.java CircusConfig.java PPacket.java CPacket.java CSwitch.java DataPlaneHandler.java

echo starting
java -cp './:./lib/CircusComm.jar:./lib/CircusConfig.jar' Circus config.txt
