pipeline {
    agent none	
    stages {
        stage('Build & Deploy') {
            agent {
                docker {
                    image 'mobiledevops/android-sdk-image'
                }
            }
            environment {
                FIREBASE_CREDENTIALS_FILE = credentials('ET_LITS_FIREBASE_CREDENTIALS')
            }
			steps {
				sh './gradlew assembleDebug appDistributionUploadDebug'
            }
        }
    }
}
