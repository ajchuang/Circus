#!/bin/bash
echo cleaning
rm *.class

echo compiling
javac -cp './:./lib/CircusComm.jar:./lib/CircusConfig.jar' *.java

