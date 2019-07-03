#!/bin/bash    
echo "executing tool jar"
toolPath="/home/rodrigo/projects/crokage-tool/target"
export PATH=$PATH:/opt/node/bin
export PATH=$PATH:/usr/local/bin
java -Xms1024M -Xmx35g -jar $toolPath/crokageTool.jar
