(Based on https://github.com/mohamedYoussfi/bank-account-ms-app)

- Installer un cluster k3d (nommé istio) : https://github.com/srozange/create-k3d-cluster/blob/main/README-
- Installer istio : https://istio.io/latest/docs/setup/getting-started/ => istioctl install
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