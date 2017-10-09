#!/bin/bash
cd /service/emsdriver
./run.sh

while [ ! -f logs/emsdriver.log ]; do
    sleep 1
done
tail -F logs/emsdirver.log
