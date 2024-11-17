(Based on https://github.com/mohamedYoussfi/bank-account-ms-app)

- Installer un cluster k3d (nommé istio) : https://github.com/srozange/create-k3d-cluster/

- Installer istio : https://istio.io/latest/docs/setup/getting-started/ 
```bash
istioctl install --set profile=demo
```

- Création des images docker :
```bash
./build-and-push-images.sh 1.0
./build-and-push-images.sh 1.0-experimental
```

- Créer un namespace :
```bash
kubectl create ns bank-account
kubectl label ns bank-account istio-injection=enabled
```

- Déploiment des micro service account et customer :
```bash
kubectl apply -f kubernetes/1-deployments-and-services.yml
kubectl apply -f kubernetes/2-istio-gateways.yml
```

- Exposition des outils istio :
Dans le code istio, faire un ```kubectl apply``` des yaml dans istio-1.20.3/samples/addons
```bash
kubectl apply -f kubernetes/3-expose-istio-tools.yml
```

- Maj du sampling rate
```bash
kubectl edit configmap istio -n istio-system
```
Ajout de la propriété
```yaml
mesh: |-
    accessLogFile: /dev/stdout
    defaultConfig:
      discoveryAddress: istiod.istio-system.svc:15012
      proxyMetadata: {}
      tracing:
        sampling: 100  # add this
```
Restart des deployments 
```bash
kubectl rollout restart deploy
```
- Si ds WSL, mettre à jour le host :
```
ip-wsl kiali.istio
ip-wsl grafana.istio
ip-wsl prometheus.istio
ip-wsl tracing.istio
ip-wsl bank-account-service.istio
```
- Url : http://bank-account-service.istio/api/accounts