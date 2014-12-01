#!/bin/bash
echo cleaning
rm ./CircusPPacket/*.class 2>/dev/null 1>/dev/null
rm ./lib/CircusPPacket.jar 2>/dev/null 1>/dev/null

echo compiling
javac ./CircusPPacket/*.java

echo archiving
jar -cvf ./lib/CircusPPacket.jar ./CircusPPacket/*.class
