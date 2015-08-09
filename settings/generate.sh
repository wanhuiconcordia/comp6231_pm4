#!/bin/bash
cp -r ../libs ./
cp -r ../settings ./
mv ./settings/*.sh ./
javac -d ./ -cp ".:./libs/dom4j-2.0.0-ALPHA-2.jar:./libs/swingx-1.6.jar:./libs/org-apache-commons-codec.jar:" ../src/tools/*
javac -d ./ ../src/loggerserver/*
javac -d ./ -cp ".:./libs/dom4j-2.0.0-ALPHA-2.jar:./libs/swingx-1.6.jar:./libs/org-apache-commons-codec.jar:" ../src/manufacturer/*
javac -d ./ -cp ".:./libs/dom4j-2.0.0-ALPHA-2.jar:./libs/swingx-1.6.jar:" ../src/warehouse/*
javac -d ./ -cp ".:./libs/dom4j-2.0.0-ALPHA-2.jar:./libs/swingx-1.6.jar:" ../src/retailer/*
javac -d ./ -cp ".:./libs/dom4j-2.0.0-ALPHA-2.jar:./libs/swingx-1.6.jar:" ../src/client/*





