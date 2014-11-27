#!/bin/bash
echo cleaning
rm *.class

echo compiling
javac -cp './:./lib/CircusComm.jar:./lib/CircusCfg.jar' CPSwitch.java Circus.java DebugInterface.java CPTable.java PPacket.java CPacket.java CSwitch.java DataPlaneHandler.java

echo starting
java -cp './:./lib/CircusComm.jar:./lib/CircusCfg.jar' Circus config.txt
