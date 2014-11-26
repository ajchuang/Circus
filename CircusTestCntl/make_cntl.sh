#!/bin/bash
javac -cp './:../lib/CircusComm.jar' CircusTestController.java
java -cp './:../lib/CircusComm.jar' CircusTestController 8888 8889
