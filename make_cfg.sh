#!/bin/bash
echo cleaning
rm ./lib/CircusCfg.jar 2>/dev/null 1>/dev/null
rm ./CircusCfg/*.class 2>/dev/null 1>/dev/null
rm ./CircusCfg/*.jar 2>/dev/null 1>/dev/null

echo compiling
javac ./CircusCfg/*.java

echo archiving
jar -cvf ./lib/CircusCfg.jar ./CircusCfg/*.class
#mv CircusConfig.jar ../lib/
