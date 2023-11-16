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
				FIREBASE_APP_ID = credentials('ET_LITS_APP_ID')
				BUILD_TAG_PRETTY = "$BUILD_NUMBER $BRANCH_NAME"
            }
			steps {
				sh './gradlew assembleDebug appDistributionUploadDebug'
            }
        }
    }
}
