#!/bin/sh
version=$1

cd customer-service
docker build . -t bank-account-ms-app-kube-bank-customer-service:$version

cd ../account-service
docker build . -t bank-account-ms-app-kube-bank-account-service:$version

docker tag bank-account-ms-app-kube-bank-customer-service:$version localhost:5001/bank-account-ms-app-kube-bank-customer-service:$version
docker push localhost:5001/bank-account-ms-app-kube-bank-customer-service:$version

docker tag bank-account-ms-app-kube-bank-account-service:$version localhost:5001/bank-account-ms-app-kube-bank-account-service:$version
docker push localhost:5001/bank-account-ms-app-kube-bank-account-service:$version
