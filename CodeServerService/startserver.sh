#!/bin/bash
# script started at boot time by /etc/rc.d/init.d/genamap
# setting to kill subprocesses 
trap 'kill $(jobs -p)' EXIT
/usr/bin/java -jar /home/genamap/service/service.jar geneassoc -1 "/home/genamap/log/"
