pipeline {
    agent none
    stages {
        stage('Build & Deploy') {
            agent {
                docker {
                    image 'cimg/android:2023.08'
                }
            }
            steps {
                sh 'gradle bundleDebug appDistributionUploadDebug'
            }
        }
    }
}
