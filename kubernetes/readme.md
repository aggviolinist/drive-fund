# Deployment on Kubernetes
## EC2
## Jenkins 
## Docker
## ArgoCD $ Kubernetes
Architecture
![Architecture Design](/kubernetes/images/architecture.png)
## 1. EC2
### 1.1 Install Java to the server
   ```sh
   sudo apt update
   sudo apt install openjdk-17-jre
   java -version
   ```
### 1.2 Installing Jenkins
   ```sh
   curl -fsSL https://pkg.jenkins.io/debian/jenkins.io-2023.key | sudo tee \
   /usr/share/keyrings/jenkins-keyring.asc > /dev/null
   echo deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc] \
   https://pkg.jenkins.io/debian binary/ | sudo tee \
   /etc/apt/sources.list.d/jenkins.list > /dev/null
   sudo apt-get update
   sudo apt-get install jenkins
   ```
   Working with Jenkins
   ```sh
   sudo cat /var/lib/jenkins/secrets/initialAdminPassword
   ```
   ```sh
   http://<ec2-instance-public-ip>:8080
   ```
### 1.3 Install Docker
   ```sh
   sudo apt update
   sudo apt install docker.io
   ```
   Docker Slave Configuration
   ```sh
   sudo su - 
   usermod -aG docker jenkins
   usermod -aG docker ubuntu
   systemctl restart docker
   ```
### 1.4 Install Git
   ```sh
   sudo apt install git
   ```
### 1.5 Install Sonar Qube
   - System Requirements
   - Java 17+ (Oracle JDK, OpenJDK, or AdoptOpenJDK)
   - Hardware Recommendations:
   - Minimum 2 GB RAM
   - 2 CPU cores
   ```sh
   sudo apt update && sudo apt install unzip -y
   adduser sonarqube
   wget https://binaries.sonarsource.com/Distribution/sonarqube/sonarqube-10.4.1.88267.zip
   unzip *
   chown -R sonarqube:sonarqube /opt/sonarqube
   chmod -R 775 /opt/sonarqube
   cd /opt/sonarqube/bin/linux-x86-64
   ./sonar.sh start
   ```
## 2. Create a new Jenkins pipeline:
### 2.1: In Jenkins, create a new pipeline job and configure it with the Git repository URL for the Java application.
### 2.2: Add a Jenkinsfile to the Git repository to define the pipeline stages.
### 2.3: Add dependencies like AWS Credentials, Docker pipeline, Sonar Qube
### 2.4: Configure Sonar, Github, docker, AWS secret variables
 
## 3. Define the pipeline stages:
    Stage 1: Build the Java application using Maven.
    Stage 2: Run SonarQube analysis to check the code quality.
    Stage 3: Package the application into a docker file.
    Stage 4: Upload the docker file in docker hub.
    Stage 5: Update Kuberrnetes Deployment File with image tag
    Stage 6: Promote the application to a production environment using Argo CD.
## 4. Argo CD
### 4.1 Install docker 
###  4.1.1 Install MInikube
###  4.1.2 Install Kubectl
###  4.2 ```sh Install the Controllers using ArgoCD operator``` https://operatorhub.io/operator/argocd-operator to manage the lifecycle of K8s controllers and comes with default configs
###  4.3 How to use the operator https://argocd-operator.readthedocs.io/en/latest/usage/basics/
   ```sh
   kubectl get pods -n operators
   kubectl apply -f kubernetes/argoCD/argocd-basic.yml
   kubectl get pods
   kubectl get pods -w
   kubectl get svc
   kubectl edit svc argo-cd-server
   minikube service argo-cd-server
   minikube service list
   kubectl get secret
   kubectl edit secret argo-cd-server
   echo bas64enxryptedtextdjjfjkfkrjffjfjf= | base64 -d
   kubectl get deploy
   ```


