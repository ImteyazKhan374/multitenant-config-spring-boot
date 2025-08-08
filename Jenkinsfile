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
    }
}
