#!/bin/bash
echo -n "Enter the Spigot Version (Example: 1.19): "
read Input
echo -n "Enter any additional arguments: "
read Args
mvn clean install exec:java -Dspigot-version=$Input -Dexec.args="$Input $Args"