#!/bin/sh

awk '{for (i=1; i<=NF; i++) a[i]=a[i](NR!=1?FS:"")$i} END {for (i=1; i in a; i++) print a[i]}' $1 >>$2
