pipeline {
    agent none
    stages {
        stage('Build & Deploy') {
            agent {
                docker {
                    image 'mobiledevops/android-sdk-image'
                }
            }
            steps {
                sh './gradlew assembleDebug appDistributionUploadDebug'
            }
        }
    }
}
