Many thanks for the opportunity. 
I have choose Technical Assignment 1 as I am quite familiar with some of the concepts of Jenkins and docker. 
To start of this I have used my AWS account where I can create ubuntu unix server. 

Part 1.	
Build a custom Jenkins Docker image from the official base 
Install role based auth strategy plugin if not there https://plugins.jenkins.io/role-strategy
Enable the following roles:
1.	Admin
2.	Deployer
3.	Developer
4.	Prod-deployer
Give relevant access to the above roles
Save image back on Docker hub

I have created EC2 ubuntu instance in AWS. 
First installed java
Installation
Check If Java Is Installed
A simple way to verify that Java is not present is to write java in the terminal and hit the enter key.

Install Java
Before installing Java, we need to update the apt cache:

sudo apt-get update

Install Java:

sudo apt-get install openjdk-8-jre-headless -y


Now verify that it installed:

java -version

Install Jenkins
Before installing Jenkins, add the key and then the sources list to apt.

wget -q -O - https://pkg.jenkins.io/debian/jenkins-ci.org.key | sudo apt-key add -
sudo sh -c 'echo deb http://pkg.jenkins.io/debian-stable binary/ > /etc/apt/sources.list.d/jenkins.list'
Update again apt's cache with the new changes.

sudo apt-get update
Install Jenkins:

sudo apt-get install jenkins -y
Check that Jenkins is up and running:



sudo jenkins service status

Initial Configuration
Unlock Jenkins
By default, Jenkins runs on port 8080. My public IP address for this example is 35.162.36.83. Make a note of your pubic IP address as we'll need it shortly.

Right after we install Jenkins, we can open a browser and go to http://your_ip_address:8080 to see the following:

The unlock page

Due to the fact that there were too many insecure Jenkins installations deployed with the previous versions, Jenkins 2 is locked by default.

In order to unlock it as the administrator write in the terminal:

sudo cat /var/lib/jenkins/secrets/initialAdminPassword
You will get a randomly generated initial password like this one:

7620hf6499ad24a7b88a7a38f8b30c8c2
Copy the password and then paste it in the provided input field.

Press Continue.

Install Plugins

There are far too many plugins in the Jenkins ecosystem and a new user had to search through long lists of them with strange names until she could find the one that would match her needs.

Fortunately, the community took notice of this situation and Jenkins 2 can be set up from the beginning with the plugins that the Jenkins community recommends.

Alternatively, if you do not want the Jenkins installation to be bloated with things that you won't need, you can select the specific ones you would like to install.

I have taken Role Based Strategy plugin and Docker plugin.

Install plugins

Plugins installation progress page

Create The Admin User
When the installation is completed, you should create the admin user. Enter the required details and hit "Save and Finish".

Set up admin details page

Configuration Complete
Jenkins is ready and you can start working with it.

Created Below Roles manually,

Deployer
Developer
Prod-deployer

And tried to build an image but it revoked all manual role setup and starts jenkins with initially

I have tried this using groovy script by calling script in the dockerfile
But got and java class plugin error which unable to resolvedt as I am notthat much of expertise in Groovy script so created Roles Admin role using Login Auth Strategy.
In my previous project this actvitity done by Matrix authorisation Startegy which is handled byseperate DevTool team.

Below error : WARNING: Failed to run script file:/var/lib/jenkins/init.groovy.d/role-auth.groovy
groovy.lang.MissingPropertyException: No such property: RoleType for class: role-auth



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
