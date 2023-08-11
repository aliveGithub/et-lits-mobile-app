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
					withCredentials([file(credentialsId: 'ET_LITS_FIREBASE_CREDENTIALS', variable: 'TEMP_SECRET_FILE')]) {
                    env.FIREBASE_CREDENTIALS_FILE = TEMP_SECRET_FILE
                }
                }
				sh './gradlew assembleDebug appDistributionUploadDebug'
            }            
        }
    }
}
