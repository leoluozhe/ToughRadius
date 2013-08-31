#! /bin/bash
#monitor radiusd, restart it when the progress is down.

while true
do
PROSS=`ps aux | awk '{if($11=="../jdk1.6.0_37/bin/radiusd") print "ok"}'`
if [[ $PROSS != "ok" ]]; then
        echo "The programm has shutdown";
        echo "Now, restart ";
        service radiusd restart
sleep 20
fi
continue
done