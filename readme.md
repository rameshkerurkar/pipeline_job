Many thanks for the opportunity. 
I have choose Technical Assignment 1 as I am quite familiar with some of the concepts of Jenkins and docker. 
To start of this I have used my AWS account where I can create ubuntu unix server. 

Overview : To perform the given assignment , I have used a master jenkins to perform the end to end process and through the master jankins pipeline job I am able to complete the below pipeline stages :

Build a custom Jenkins docker image with help of Dockerfile (Install custom plugins and create user with enabling one of security strategy)
Run the created custom jenkins image with port 8080 and 50000 and volume path
Test the running jenkins container url with one of username and password (If the URL is not accessible the build will failed)
Publish the Image on dockerhub after successful passing test cases



###Part 1  #############
Build a custom Jenkins Docker image from the official base 
Install role based auth strategy plugin if not there https://plugins.jenkins.io/role-strategy
Enable the following roles:
1.	Admin
2.	Deployer
3.	Developer
4.	Prod-deployer
Give relevant access to the above roles
Save image back on Docker hub

Below steps for my master Jenkins creation and enable role based authentication :
I have created EC2 ubuntu instance in AWS. 
First java should be installed to run the Jenkins

Before installing Java, we need to update the apt cache and install the java:
sudo apt-get update
sudo apt-get install default-jdk -y
java --version

Install master Jenkins
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
By default, Jenkins runs on port 8080. But I used 9090 port (Because due to the host limitation Ihave to run Jenkins and jenkins inside a docker container on a single instance) My public IP address for this example is 35.162.36.83. Make a note of my pubic IP address as we'll need it shortly.

To change the default port of Jenkins , In the default configuration we need to change the HTTP port: 
vi /etc/default/jenkins
HTTP Port - 9090

Right after we install Jenkins, we can open a browser and go to http://your_ip_address:9090 to see the following:

The unlock page
In order to unlock it as the administrator write in the terminal:

sudo cat /var/lib/jenkins/secrets/initialAdminPassword
You will get a randomly generated initial password like this one:

7620hf6499ad24a7b88a7a38f8b30c8c2
Copy the password and then paste it in the provided input field.

Install Plugins

Fortunately, the community took notice of this situation and Jenkins can be set up from the beginning with the plugins that the Jenkins community recommends.

Alternatively, if we do not want the Jenkins installation to be bloated with things that you won't need, you can select the specific ones you would like to install.

I have taken Role Based Strategy plugin and Docker plugin by the option selected plugins to be installed.

Install plugins

Plugins installation progress page

Create The Admin User
When the installation is completed, you should create the admin user. Enter the required details and hit "Save and Finish".

Set up admin details page

Configuration Complete
Jenkins is ready and you can start working with it.

Next to enable Rolebased Authentication under global security 
Created Below Roles manually, Aggigne roles accordingly.

Deployer
Developer
Prod-deployer

Note : Above steps I did tried to complete by Dockerfile(for Jenkins Image creation) and groovy script (to enable role based strategy and create roles and assign permissions) 
I have tried Role based Strategy using groovy script by calling script in the dockerfile attaching the same (role-based-auth.groovy)
But got java class plugin error which unable to resolved as I am not that much of expertise in Groovy script so created Roles Admin role using Login Auth Strategy.
In my previous project this actvitity done by Matrix authorisation Startegy which is handled by seperate DevTool team.

Below error : WARNING: Failed to run script file:/var/lib/jenkins/init.groovy.d/role-auth.groovy
groovy.lang.MissingPropertyException: No such property: RoleType for class: role-auth


So in the Part 1 I used manully steps(on my master Jenkins 18.221.140.131:9090) and also I used in pipeline automated process with the Authorization Strategy (with Logged in user having the full access) which works fine on the running container on (18.221.140.131:8080). Please find script security.groovy calling in Dockerfile and below are the steps of pipeline to do this.


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
      }  

#####Part 2######################
Create a pipeline to automate and publish the above image after testing the relevant parts.


Below stages I have included in pipeline to complete the part2 of assignment:



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


For test please find test cases where I have taken the Login And Password page as test cases, If I am able to access URL means output is 200 then my test case is passed with out any issue. It will thro the excetion if any issues.

Please find the pipeline screenshot for more details and run jenkins job with below details
URL : 18.221.140.131:9090/
User : signmentSAdmin
Password: AssignmentS

######Part 3##########
Run the above image on a hosted Docker service.
Please check below URL where Jenkins running in a docker container
URL : 18.221.140.131:8080/

I have included the all parts of assignment in a single pipeline which is created on my master jenkins (18.221.140.131:9090)

So below stages I have included in pipeline as part3 of assignment :


        stage('run docker container from image') {
            steps {
                sh 'docker run -d -p 8080:8080 -p 50000:50000 -v /var/jenkinsnew_home rameshkerurkar/pipeline_assignment":$BUILD_NUMBER"'
            }
                stage('publish docker image to dockerhub') {
            steps {
                script {
                    docker.withRegistry( '', registryCredential )  {
                        sh 'docker push rameshkerurkar/pipeline_assignment":$BUILD_NUMBER"'
                    }
                }  
            } 
            
            
            
            
