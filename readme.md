Below are the Jenkins file 


pipeline {
  environment {
    registry = "rameshkerurkar/pipeline_assignment"
    registryCredential = 'dockerhub'
   }
  
  agent any
  
  stages {
      stage('Cloning Git') {
          steps {
              git credentialsId: 'github', url: 'https://github.com/rameshkerurkar/pipeline_job.git'
          }
        }
        stage('Building imag') {
            steps {
                script {
                    def dockerImage = docker.build registry + ":$BUILD_NUMBER"
                }
            }
        }    
        stage('run docker container from image') {
            steps {
                sh 'docker run -d -p 8080:8080 -p 50000:50000 -v /var/jenkinsnew_home rameshkerurkar/pipeline_assignment":$BUILD_NUMBER"'
            }
        }
        stage('Test the jenkins is running and user authentication') {
            steps {
                sh 'sh ./test.sh'
            }
        }
        stage('publish docker image to dockerhub') {
            steps {
                script {
                    docker.withRegistry( '', registryCredential )  {
                        sh 'docker push rameshkerurkar/pipeline_assignment":$BUILD_NUMBER"'
                    }
                }  
            }
        }
    }
}
