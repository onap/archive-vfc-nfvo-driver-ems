#!/bin/bash

MSB_IP=`echo $MSB_ADDR | cut -d: -f 1`
MSB_PORT=`echo $MSB_ADDR | cut -d: -f 2`
VES_IP=`echo $VES_ADDR | cut -d: -f 1`
VES_PORT=`echo $VES_ADDR | cut -d: -f 2`
USER_NAME=`echo $VES_AUTHINFO | cut -d: -f 1`
PASSWORD=`echo $VES_AUTHINFO | cut -d: -f 2`


sed -i "s|msbAddress.*|msbAddress: $MSB_IP:$MSB_PORT|" emsdriver/conf/emsdriver.yml
sed -i "s|\"ip\": \".*\"|\"ip\": \"$SERVICE_IP\"|" emsdriver/conf/emsdriver.yml
sed -i "s|msbAddress=.*|msbAddress=$MSB_IP:$MSB_PORT|" emsdriver/conf/config.properties
sed -i "s|event_api_url=.*|event_api_url=$VES_IP|" emsdriver/conf/config.properties
sed -i "s|port=.*|port=$VES_PORT|" emsdriver/conf/config.properties
sed -i "s|username=.*|username=$USER_NAME|" emsdriver/conf/config.properties
sed -i "s|password=.*|password=$PASSWORD|" emsdriver/conf/config.properties


cat emsdriver/conf/emsdriver.yml
cat emsdriver/conf/config.properties

