#!/bin/sh
while true; do curl -s http://bank-account-service.istio/actuator/info | grep -o '"version":"[^"]*"'; sleep 1; done
