#!/bin/bash
echo cleaning
rm *.class

echo compiling
javac -cp './:CircusComm.jar' *.java

echo starting
java -cp './:CircusComm.jar' Circus config.txt
