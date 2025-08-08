pipeline {
    agent any

    environment {
        IMAGE_NAME = 'unison-service'
        BUILD_TAG = "${env.BUILD_NUMBER}"
        FULL_IMAGE = "unison-service:${BUILD_TAG}"
        KUBECONFIG = 'C:\\Users\\imtey\\.kube\\config'
    }

    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/ImteyazKhan374/multitenant-config-spring-boot.git', branch: 'main'
            }
        }

        stage('Build with Maven') {
            steps {
                bat 'mvn clean package -DskipTests'
            }
        }

        stage('Archive JAR') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Docker Build & Push') {
            steps {
                bat "docker build -t ${FULL_IMAGE} ."
                // Optional push
                // bat "docker push ${FULL_IMAGE}"
            }
        }

        stage('Deploy to Kubernetes') {
    steps {
        withEnv(["KUBECONFIG=C:\\Users\\imtey\\.kube\\config"]) {
            script {
                def imageTag = "${BUILD_TAG}"
                
                // Replace placeholder with actual image tag
                bat "powershell -Command \"(Get-Content k8s/deployment.yaml) -replace '__IMAGE_TAG__', '${imageTag}' | Set-Content k8s/deployment-tagged.yaml\""

                // Apply the updated YAML
                bat 'kubectl apply -f k8s/configmap.yaml'
                bat 'kubectl apply -f k8s/service.yaml'
                bat 'kubectl apply -f k8s/deployment-tagged.yaml'
            }
        }
    }
}

    post {
        success {
            echo "✅ Deployment successful: ${FULL_IMAGE}"
        }
        failure {
            echo "❌ Deployment failed for: ${FULL_IMAGE}"
        }
    }
}
