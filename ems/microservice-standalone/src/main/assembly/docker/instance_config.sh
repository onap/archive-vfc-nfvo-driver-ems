#!/bin/bash

MSB_IP=`echo $MSB_ADDR | cut -d: -f 1`
MSB_PORT=`echo $MSB_ADDR | cut -d: -f 2`

sed -i "s|msbAddress.*|msbAddress = $MSB_IP:$MSB_PORT|" emsdriver/conf/emsdriver.yml
sed -i "s|\"ip\": \".*\"|\"ip\": \"$SERVICE_IP\"|" emsdriver/conf/emsdriver.yml


cat emsdriver/conf/emsdriver.yml

