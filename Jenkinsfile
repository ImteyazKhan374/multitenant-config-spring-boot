pipeline {
    agent any

    environment {
        IMAGE_NAME = 'unison-service'
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

		stage('Build Docker Image') {
		    steps {
		        bat "docker build -t ${IMAGE_NAME} ."
		        //bat "docker push ${IMAGE_NAME}"
		    }
		}


        stage('Deploy to Kubernetes') {
            steps {
                script {
                    bat 'kubectl apply -f k8s/configmap.yaml'
                    bat 'kubectl apply -f k8s/service.yaml'
                    bat 'kubectl apply -f k8s/deployment.yaml'
                }
            }
        }
    }
}