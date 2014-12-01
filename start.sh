#!/bin/bash
echo cleaning
rm *.class

echo compiling
javac -cp './:./lib/CircusComm.jar:./lib/CircusCfg.jar:./lib/CircusPPacket.jar' CPSwitch.java Circus.java DebugInterface.java CPTable.java CPacket.java CSwitch.java DataPlaneHandler.java

echo starting
java -cp './:./lib/CircusComm.jar:./lib/CircusCfg.jar:./lib/CircusPPacket.jar' Circus config.txt
