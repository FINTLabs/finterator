# FINT Operator

## Generate key pair
```shell
openssl genrsa 2048 > private.pem
```
for LibreSSL 3.3.6:
```
openssl genpkey -algorithm rsa
```

### Client
.yaml example file:
```yaml
apiVersion: fintlabs.no/v1alpha1
kind: FintClient
metadata:
  name: flais-test-client
  namespace: fintlabs-no
  labels:
    app.kubernetes.io/name: test-client
    app.kubernetes.io/instance: test-service-backend-client_county_no
    app.kubernetes.io/version: latest
    app.kubernetes.io/component: backend
    app.kubernetes.io/part-of: arkiv
    fintlabs.no/team: flais
    fintlabs.no/org-id: fintlabs.no

spec:
  orgId: fintlabs.no
  note: Dette er en test. Nu kjør vi!!
  components:
    - administrasjon_personal
    - utdanning_elev
```

### Adapter
.yaml example file:
```yaml
apiVersion: fintlabs.no/v1alpha1
kind: FintAdapter
metadata:
  name: flais-test-adapter
  namespace: fintlabs-no
  labels:
    app.kubernetes.io/name: test-adapter
    app.kubernetes.io/instance: test-adapter_county_no
    app.kubernetes.io/version: latest
    app.kubernetes.io/component: adapter
    app.kubernetes.io/component: arkiv-adapter
    app.kubernetes.io/part-of: arkiv
    fintlabs.no/team: flais
    fintlabs.no/org-id: fintlabs.no
    
spec:
  orgId: fintlabs.no
  note: Dette er en test. Nu kjør vi!!
  components:
    - administrasjon_personal
    - utdanning_elev
  assetIds:
    - test.fylke.no
    - test.annet_fylke.no 
```
