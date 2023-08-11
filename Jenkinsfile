pipeline {
    agent none
	environment {        
        FIREBASE_CREDENTIALS_FILE = credentials('ET_LITS_FIREBASE_CREDENTIALS')
    }
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
