(Based on https://github.com/mohamedYoussfi/bank-account-ms-app)

# Modop

- Installer un cluster k3d (nommé istio) : https://github.com/srozange/create-k3d-cluster/

- Installer istio : https://istio.io/latest/docs/setup/getting-started/ 
```bash
istioctl install --set profile=demo
```

- Création des images docker (need to use java 17) :
```bash
./build-and-push-images.sh 1.0
./build-and-push-images.sh 1.0-experimental
```

- Déploiments  :
```bash
kubectl apply -f kubernetes/0-namespace.yml
kubectl apply -f kubernetes/1-deployments-and-services.yml
kubectl apply -f kubernetes/2-istio-gateways.yml
kubectl apply -f kubernetes/3-apply-istio-tools.yml
kubectl apply -f kubernetes/4-expose-istio-tools.yml
```
Note : Le fichier ```kubernetes/3-apply-istio-tools.yml``` provient de la distrib istio : istio-1.20.3/samples/addons

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

- Mettre à jour le host :
```
127.0.0.1 kiali.istio
127.0.0.1 grafana.istio
127.0.0.1 prometheus.istio
127.0.0.1 tracing.istio
127.0.0.1 bank-account-service.istio
```

- Si sous WLS (pour acceder depuis windows) :
Installer socal : https://sourceforge.net/projects/unix-utils/files/socat/1.7.3.2/socat-1.7.3.2-1-i686.zip/download    
Lancer le script sur le host : ./redirect-wls.ps1      
Les Urls :      
  - http://bank-account-service.istio:8080/api/accounts
  - http://kiali.istio:8080
  - http://tracing.istio:8080

## Optionnel : OPA

- Déploiments  :
```bash
kubectl apply -f kubernetes/5-opa-core.yml
kubectl apply -f kubernetes/6-opa-config.yml
```

```bash
kubectl edit configmap istio -n istio-system
```

Ajout de la propriété
```yaml
data:
  mesh: |-
    extensionProviders:
    - name: opa-ext-authz-grpc
      envoyExtAuthzGrpc:
        service: opa-ext-authz-grpc.local
        port: 9191
```

Restart des deployments 
```bash
kubectl rollout restart deploy
```

Explication: Avec le user srozange, la récupération des customers n'est pas authorisée :

```bash
curl http://bank-account-service.istio/api/accounts/6d210d2d-f96c-4a89-8eb4-1739ad841fde
{"id":"6d210d2d-f96c-4a89-8eb4-1739ad841fde","balance":41134.012624550705,"createdAt":"2025-12-08","type":"SAVING_ACCOUNT","currency":"MAD","customerId":1,"customer":{"id":1,"firstName":"Mohammadi","lastName":"Imane"}}

curl --user "srozange:toto" http://bank-account-service.istio/api/accounts/6d210d2d-f96c-4a89-8eb4-1739ad841fde
{"id":"6d210d2d-f96c-4a89-8eb4-1739ad841fde","balance":41134.012624550705,"createdAt":"2025-12-08","type":"SAVING_ACCOUNT","currency":"MAD","customerId":1,"customer":{"id":1,"firstName":"Source not available","lastName":"Source Not Available"}}                                                                                              
```

Suite à une mise à jour de la rule opa : ```k rollout restart deployment```        
Suite à une mise à jour de la conf itio/opa : ```kubectl rollout restart deployment/admission-controller -n opa-istio```
