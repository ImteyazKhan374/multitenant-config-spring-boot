pipeline {
    agent any

    environment {
        IMAGE_NAME = 'unison-service'
        BUILD_TAG = "${env.BUILD_NUMBER}"
        FULL_IMAGE = "unison-service:${BUILD_TAG}"
        KUBECONFIG = 'C:\\Users\\imtey\\.kube\\config'

        // 🔹 SonarQube settings
        SONAR_HOST_URL = 'http://localhost:9000'   // Replace with your SonarQube URL
        SONAR_PROJECT_KEY = 'unison-service'       // Must match your Sonar project key
        SONAR_PROJECT_NAME = 'Unison Service'
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

        stage('SonarQube Analysis') {
            steps {
                withCredentials([string(credentialsId: 'sqa_663d3b5ea9e2cae1aa80e9d72ce675bed64d1cb5', variable: 'SONAR_TOKEN')]) {
                    bat """
                        mvn sonar:sonar ^
                            -Dsonar.projectKey=${SONAR_PROJECT_KEY} ^
                            -Dsonar.projectName="${SONAR_PROJECT_NAME}" ^
                            -Dsonar.host.url=${SONAR_HOST_URL} ^
                            -Dsonar.login=%SONAR_TOKEN%
                    """
                }
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
                // Optional: push to registry
                // bat "docker push ${FULL_IMAGE}"
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                withEnv(["KUBECONFIG=C:\\Users\\imtey\\.kube\\config"]) {
                    script {
                        def imageTag = "${BUILD_TAG}"
                        bat "powershell -Command \"(Get-Content k8s/deployment.yaml) -replace '__IMAGE_TAG__', '${imageTag}' | Set-Content k8s/deployment-tagged.yaml\""
                        bat 'kubectl apply -f k8s/configmap.yaml'
                        bat 'kubectl apply -f k8s/service.yaml'
                    }
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
