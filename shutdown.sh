#!/bin/sh
 
NAME=$1
echo $NAME
ID=`ps -ef | grep tomcat |grep -v grep| awk '{print $2}'`
echo $ID
echo "---------------"
for id in $ID
do
kill -9 $id
echo `ps aux | grep $id`
echo
echo "killed $id"
done

echo "---------------"