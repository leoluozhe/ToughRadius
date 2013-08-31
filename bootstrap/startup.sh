#!/bin/sh
# -----------------------------------------------------------------------------
# Start RADIUS Server
#
# $Id: startup.sh $
# -----------------------------------------------------------------------------
ulimit -n 65535

if [ -d "../jdk1.6.0_37" ]; then
  PROJ_HOME=../jdk1.6.0_37
else
  PROJ_HOME=$JAVA_HOME
fi

echo "PROJ_HOME=$PROJ_HOME"

# Check $PROJ_HOME/bin/java is Exist
if [ ! -f "$PROJ_HOME/bin/java" ]; then
  return
fi

# Check $PROJ_HOME/bin/radiusd is Exist
if [ ! -f "$PROJ_HOME/bin/radiusd" ]; then
  cp $PROJ_HOME/bin/java $PROJ_HOME/bin/radiusd
fi

# define CLASSPATH
CLASS_PATH=$PROJ_HOME/lib/dt.jar:$PROJ_HOME/lib/tools.jar:./bootstrap.jar

# debug
#DEBUG_CONFIG="-Xdebug -Xrunjdwp:transport=dt_socket,address=11815,server=y,suspend=n"

exec nohup $PROJ_HOME/bin/radiusd -server -Xms64m -Xmx1024m $DEBUG_CONFIG  -classpath $CLASS_PATH org.toughradius.Bootstrap org.toughradius.Project &