# Tester OPA sans cluster Kubernetes

## Option 1 : OPA eval

> Note : `input-deny.json` est au format REST API (`{"input": {...}}`).
> Pour `opa eval`, l'input est passé directement sans ce wrapper via `--stdin-input`.

```bash
# Cas refusé : srozange → bank-customer-service (résultat: false)
opa eval -d policy.rego --stdin-input "data.istio.authz.allow" \
  <<< '{"attributes":{"request":{"http":{"headers":{"authorization":"Basic c3JvemFuZ2U6dG90bw=="},"host":"bank-customer-service:8080"}}}}'

# Cas autorisé : autre user → bank-customer-service (résultat: true)
opa eval -d policy.rego --stdin-input "data.istio.authz.allow" \
  <<< '{"attributes":{"request":{"http":{"headers":{"authorization":"Basic b3RoZXI6cGFzcw=="},"host":"bank-customer-service:8080"}}}}'

# Debug : afficher user_name, deny, allow, host
opa eval -d policy.rego --stdin-input "data.istio.authz.debug_info" \
  <<< '{"attributes":{"request":{"http":{"headers":{"authorization":"Basic c3JvemFuZ2U6dG90bw=="},"host":"bank-customer-service:8080"}}}}'
```

## Option 2 : Serveur Docker

```bash
docker run -p 8181:8181 -v ${PWD}:/policy \
  openpolicyagent/opa:0.61.0-envoy \
  run --server /policy/policy.rego

# Tester via l'API REST OPA
curl -X POST http://localhost:8181/v1/data/istio/authz/allow \
  -H "Content-Type: application/json" \
  -d @input-deny.json
```

## Option 3 : `opa test` — tests unitaires (recommandé)

```bash
opa test policy.rego policy_test.rego -v
```

## Valeurs base64 utiles

| Credentials | Base64 |
|---|---|
| `srozange:toto` | `c3JvemFuZ2U6dG90bw==` |
| `other:pass` | `b3RoZXI6cGFzcw==` |

Générer une valeur : `echo -n "user:password" | base64`

## Recommandation

Préférer l'**option 3** (`opa test`) — tests reproductibles, rapides, sans dépendance externe.
L'**option 1** (`opa eval`) est utile pour déboguer une rule rapidement.
