(Based on https://github.com/mohamedYoussfi/bank-account-ms-app)

- Installer un cluster k3d (nommé istio) : https://github.com/srozange/create-k3d-cluster/blob/main/README-
- Installer istio : https://istio.io/latest/docs/setup/getting-started/ =>  istioctl install --set profile=demo
- Création des images docker :
./build-and-push-images.sh 1.0
./build-and-push-images.sh 1.0-experimental
- Créer un namespace :
kubectl create ns bank-account
kubectl label ns bank-account istio-injection=enabled
- Déploiment des images :
kubectl apply -f kubernetes/1-deployments-and-services.yml
kubectl apply -f kubernetes/2-deployments-and-services.yml
- Exposition des outils istio
Dans le code istio, faire un apply des yaml dans istio-1.20.3/samples/addons
kubectl apply -f kubernetes/3-expose-istio-tools.yml
- Maj du sampling rate (TODO)