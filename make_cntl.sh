#!/bin/bash
javac -cp CircusComm.jar CircusTestController.java
java -cp './:./CircusComm.jar' CircusTestController
