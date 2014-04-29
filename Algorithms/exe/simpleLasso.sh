#!/bin/csh -f
echo "starting R ..."
R --slave --args $1 $2 $3 $4 $5 $6 $7 <../exe/simpleLasso.r
echo "finished."
