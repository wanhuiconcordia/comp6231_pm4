#!/bin/bash
java -cp ".:./libs/dom4j-2.0.0-ALPHA-2.jar:./libs/swingx-1.6.jar:" retailer.RetailerReplica $1 $2
#(exec java retailer.RetailerReplica "$@" > /tmp/retailerReplica.log 2>&1 )&
