pipeline {
    agent any

    tools {
        maven 'M3'  // This must match exactly with the name you set in Jenkins UI
    }

    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/ImteyazKhan374/multitenant-config-spring-boot.git', branch: 'main'
            }
        }

        stage('Build') {
            steps {
                // Use the Maven tool explicitly
                sh 'mvn -version'  // just to verify Maven is correctly loaded
                sh 'mvn clean package -DskipTests'
            }
        }
    }
}
