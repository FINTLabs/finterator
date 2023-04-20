# FINT Operator

## Generate key pair
```shell
openssl genrsa 2048 > private.pem
```



.yaml example file:
```yaml
apiVersion: fintlabs.no/v1alpha1
kind: FintClient
metadata:
  name: frodes-test-client
  namespace: fintlabs-no
  labels:
    app.kubernetes.io/name: test-adapter
    app.kubernetes.io/instance: test-adapter_rogfk_no
    app.kubernetes.io/version: latest
    app.kubernetes.io/component: adapter
    app.kubernetes.io/part-of: arkiv
    fintlabs.no/team: flais
    fintlabs.no/org-id: fintlabs.no

spec:
  orgId: fintlabs.no
  note: Dette er en test. Jævlar, nu kjør vi!!
  components:
    - administrasjon_personal
    - utdanning_elev
```
