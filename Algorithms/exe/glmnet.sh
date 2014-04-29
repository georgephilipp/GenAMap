#!/bin/csh -f
echo "starting R ..."
R --slave --args $1 $2 $6 $3 $4 $5 $6 $7 $8 $9 <../exe/Glmnet.r
echo "finished."
