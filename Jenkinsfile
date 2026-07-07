pipeline {
    agent any

    options {
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '20'))
        skipDefaultCheckout(true)
    }

    parameters {
        choice(name: 'ENV', choices: ['qa', 'staging', 'prod'], description: 'Environment config to run')
    }

    environment {
        MAVEN_OPTS = '-Dfile.encoding=UTF-8'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Verify Toolchain') {
            steps {
                sh 'java -version'
                sh 'mvn -version'
            }
        }

        stage('Run Selenium Tests') {
            steps {
                sh "mvn clean test -Denv=${params.ENV}"
            }
        }
    }

    post {
        always {
            junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true

            archiveArtifacts artifacts: 'target/surefire-reports/**', allowEmptyArchive: true

            script {
                if (fileExists('reports')) {
                    archiveArtifacts artifacts: 'reports/**', allowEmptyArchive: true
                } else {
                    echo 'No reports directory found to archive.'
                }

                if (fileExists('screenshots')) {
                    archiveArtifacts artifacts: 'screenshots/**', allowEmptyArchive: true
                } else {
                    echo 'No screenshots directory found to archive.'
                }
            }
        }
    }
}
