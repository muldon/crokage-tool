#!/bin/bash    
echo "executing tool jar"
toolPath="/home/rodrigo/projects/crokage-tool/target"
export PATH=$PATH:/opt/node/bin
export PATH=$PATH:/usr/local/bin
java -jar $toolPath/crokageTool.jar
