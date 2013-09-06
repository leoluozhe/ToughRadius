#!/bin/sh


if [ -d "../jdk1.6.0_37" ]; then
  PROJ_HOME=../jdk1.6.0_37
else
  PROJ_HOME=$JAVA_HOME
fi

# Check $PROJ_HOME/bin/java is Exist
if [ ! -f "$PROJ_HOME/bin/java" ]; then
  return
fi

# define CLASSPATH
CLASS_PATH=./lib/radiusd.jar:./lib/hsqldb.jar

$PROJ_HOME/bin/java -server -classpath $CLASS_PATH org.toughradius.SetupDB
