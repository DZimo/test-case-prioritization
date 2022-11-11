pipeline {
    agent any
    stages {
        stage('Test') {
            steps {
                sh 'mvn -B clean test'
            }
        }
    }
    post {
        always {
            gamekins jacocoCSVPath: '**/target/site/jacoco/jacoco.csv', jacocoResultsPath: '**/target/site/jacoco'
            jacoco execPattern: '**/target/**.exec', classPattern: '**/target/classes/de/uni_passau/fim/se2/test_prioritization'
        }
    }
}
