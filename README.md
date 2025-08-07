# 🛠️ Unison Microservice - Kubernetes Setup

This document outlines how to run your Spring Boot microservice with Kubernetes on a **local Windows system**, along with integrations for **Redis**, **Vault**, and **MySQL**.

---

## 📁 Folder Structure

```
unison/
├── k8s/
│   ├── deployment.yaml
│   ├── service.yaml
│   ├── configmap.yaml
├── Dockerfile
├── README.md (this file)
```

---

## 🚀 Prerequisites

* [Docker Desktop](https://www.docker.com/products/docker-desktop/) with Kubernetes enabled
* [kubectl CLI](https://kubernetes.io/docs/tasks/tools/)
* Java 17+ and Maven
* MySQL installed locally on Windows
* Redis and Vault (run via Docker, see below)

---

## 🐳 Run Redis and Vault via Docker

### 🔁 Redis

```bash
docker run --name redis -p 6379:6379 -d redis
```

### 🔐 Vault

```bash
docker run --cap-add=IPC_LOCK -e 'VAULT_DEV_ROOT_TOKEN_ID=root' -e 'VAULT_DEV_LISTEN_ADDRESS=0.0.0.0:8200' -p 8200:8200 --name vault -d hashicorp/vault
```

Access Vault at: [http://localhost:8200](http://localhost:8200)
Token: `root`

#### ➕ Add Vault Secret

Make a POST request to add credentials depending on how you're running the service:

### ▶️ For **Kubernetes/Docker**:

**URL:** `http://localhost:8200/v1/secret/data/unison`
**Payload:**

```json
{
    "data": {
        "dburl": "jdbc:mysql://host.docker.internal:3306/",
        "dbusername": "root",
        "dbpassword": "mysql"
    }
}
```

### 💻 For **Local Run**:

**Payload:**

```json
{
    "data": {
        "dburl": "jdbc:mysql://localhost:3306/",
        "dbusername": "root",
        "dbpassword": "mysql"
    }
}
```

You can use Postman or curl:

```bash
curl --header "X-Vault-Token: root" --request POST --data '{
  "data": {
    "dburl": "jdbc:mysql://localhost:3306/",
    "dbusername": "root",
    "dbpassword": "mysql"
  }
}' http://localhost:8200/v1/secret/data/unison
```

---

## 📂 MySQL (Native Installation)

Install MySQL on your Windows system via installer from [https://dev.mysql.com/downloads/installer/](https://dev.mysql.com/downloads/installer/).

### ✅ Configuration:

* DB Name: `unison_db`
* Username: `unison_user`
* Password: `unison_pass`
* Port: `3306`

---

## 🔧 `application.yml`

```yaml
spring:
  cache:
    type: redis
    redis:
      time-to-live: 600000  # 10 minutes in milliseconds
      cache-null-values: false
      key-prefix: true

  redis:
    host: localhost
    port: 6379
    timeout: 60000

  cloud:
    vault:
      #uri: http://host.docker.internal:8200
      uri: http://localhost:8200
      token: root     # Replace with actual token in production
      authentication: TOKEN
      kv:
        enabled: true
        backend: secret
        default-context: application

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
    fetch-registry: false
    register-with-eureka: false
  instance:
    hostname: localhost
#    prefer-ip-address: true
#    ip-address: 127.0.0.1
#    instance-id: ${spring.application.name}:${server.port}
```

---

## 📦 Build Docker Image

```bash
docker build -t unison-service:latest .
```

---

## ☘️ Deploy to Kubernetes

Apply the following YAML files from your `k8s` folder:


```bash
use 'kubectl create secret generic unison-vault-secrets --from-literal=SPRING_CLOUD_VAULT_TOKEN=root' to create vault token encrypted format

kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
```

---

## 🔌 Access the Service

Since you're running locally, expose the service using port forwarding:

```bash
kubectl port-forward svc/unison-service 9090:9090
```

Now open:

```
http://localhost:9090/user/create
```

---

## 📍 Stop the Port Forwarding

Press `Ctrl + C` in the terminal where port-forward was started.

---

## 📄 Useful Commands

```bash
# Check pods
kubectl get pods

# View logs
kubectl logs <pod-name>

# Delete all
kubectl delete -f k8s/
```

---

## ✅ Summary

* ✅ Spring Boot runs in Kubernetes
* ✅ Vault secrets injected using Spring Cloud Vault
* ✅ Redis used for caching
* ✅ MySQL installed and connected
* ✅ Vault secrets work both in local and K8s via different payload values

Let me know if you'd like to integrate MongoDB, RabbitMQ, Jenkins, or Prometheus/Grafana next.
