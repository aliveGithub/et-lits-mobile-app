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
                script {                    
                    env.FIREBASE_CREDENTIALS_FILE = credentials('ET_LITS_FIREBASE_CREDENTIALS')
                }
				sh './gradlew assembleDebug appDistributionUploadDebug'
            }            
        }
    }
}
