#!/bin/sh
# -----------------------------------------------------------------------------
# Shut radiusd
#
# $Id: shutdown.sh $
# -----------------------------------------------------------------------------

if [ -d "../jdk1.6.0_37" ]; then
  PROJ_HOME=../jdk1.6.0_37
else
  PROJ_HOME=$JAVA_HOME
fi

# Check $PROJ_HOME/bin/java is Exist
if [ ! -f "$PROJ_HOME/bin/java" ]; then
  return
fi


$PROJ_HOME/bin/java -server -classpath "./lib/radiusd.jar" org.toughradius.Shutdown