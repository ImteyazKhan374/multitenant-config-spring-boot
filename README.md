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
* Jenkins installed locally (or via Docker Optional)
* SonarQube running locally (or via Docker Optional)
* Ngrok running locally (optional)

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

### Jenkins

```bash
docker run -u root --name jenkins -d -p 9999:9999 -p 50000:50000 -v jenkins_home:/var/jenkins_home jenkins/jenkins:lts
or download the jenkins for windows and run on 9999
```

Install plugins:

* Pipeline
* Git
* SonarQube Scanner
* Docker Pipeline



Access Jenkins: http://localhost:999
Find the initial password under C:\ProgramData\Jenkins\.jenkins\secrets\initialAdminPassword and setup the rest


### SonarQube

```bash
docker run --name sonarqube -d -p 9000:9000 sonarqube:lts
```
Access SonarQube: http://localhost:9000
Default credentials: admin/admin

### ngrok

* download ngrok
* Log in to  ngrok to your account and find your authtoken

```bash
Run this in your terminal:
ngrok config add-authtoken YOUR_AUTHTOKEN_HERE
Example:
ngrok config add-authtoken 2FJp0xxxxx_xxxxxxxx
```

* Expose Jenkins via ngrok


```bash

ngrok http 9999
You’ll get a public URL like: https://2f34-103-78-55-101.ngrok-free.app
Whenever you try to access https://2f34-103-78-55-101.ngrok-free.app will point to your local jenkins running on 9999
```

* Enable GitHub webhook in Jenkins


```bash
In Jenkins job → Configure → Build Triggers → ✔ GitHub hook trigger for GITScm polling.
```

* Add webhook in GitHub

```bash
Go to Settings under your repo → Webhooks → Add webhook.
Payload URL:
https://2f34-103-78-55-101.ngrok-free.app/github-webhook/ (generated using ngrok http 9999)
Content type: application/json
Events: "Just the push event"
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
