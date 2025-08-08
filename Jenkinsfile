pipeline {
    agent any

    environment {
        // You can keep environment variables if needed later
        IMAGE_NAME = 'unison-service'
    }

    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/ImteyazKhan374/multitenant-config-spring-boot.git', branch: 'main'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }
    }
}
