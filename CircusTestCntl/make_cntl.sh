#!/bin/bash
javac -cp './:../lib/CircusCommunication.jar' CircusTestController.java
java -cp './:../lib/CircusCommunication.jar' CircusTestController 8888 8889
