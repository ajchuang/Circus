#!/bin/bash
echo cleaning
rm *.class

echo compiling
javac *.java

echo starting
java Circus config.txt
