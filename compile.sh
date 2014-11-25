#!/bin/bash
echo cleaning
rm *.class

echo compiling
javac -cp 'CircusComm.jar' *.java

