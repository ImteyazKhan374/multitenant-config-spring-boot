pipeline {
    agent {
        docker {
            image 'maven:3.9.6-eclipse-temurin-17' // or use the version matching your project
        }
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
