# Deployment on Kubernetes
## EC2
## Jenkins 
## SonarQube
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
## 2. Configure and Install Sonar
 - System Requirements
   - Java 17+ (Oracle JDK, OpenJDK, or AdoptOpenJDK)
   - Hardware Recommendations:
   - Minimum 2 GB RAM
   - 2 CPU cores
### 2.1 Download the server
```sh
sudo apt update && sudo apt install unzip -y
cd /opt/ # change directory to this opt folder
wget https://binaries.sonarsource.com/Distribution/sonarqube/sonarqube-10.4.1.88267.zip
unzip *
```
### 2.2 SonarQube should not run as root. Make sure you have a dedicated sonarqube user
```sh
sudo useradd -r -d sonarqube -s /bin/false sonarqube
sudo chown -R sonarqube:sonarqube /opt/sonarqube
sudo chown -R sonarqubez:sonarqubez /opt/sonarqube
```
### 2.3 Start the server
```sh 
sudo -u sonarqube ./sonar.sh start
sudo -u sonarqubez sonarqube/bin/linux-x86-64/sonar.sh start
sudo -u sonarqubez sonarqube/bin/linux-x86-64/sonar.sh stop
```
### 2.4 Debug the server
```sh
sudo rm -rf sonarqube/logs/*
sudo rm -f sonarqube/bin/linux-x86-64/SonarQube.pid
```
### 2.5 Access it on browser
```sh
http://<ip-address>:9000
```

## 3. Create a new Jenkins pipeline:
### 3.1: In Jenkins, create a new pipeline job and configure it with the Git repository URL for the Java application.
### 3.2: Add a Jenkinsfile to the Git repository to define the pipeline stages.
### 3.3: Add dependencies like AWS Credentials, Docker pipeline, Sonar Qube
### 3.4: Configure Sonar, Github, docker, AWS secret variables
 

## 4. Define the pipeline stages:
    Stage 1: Build the Java application using Maven.
    Stage 2: Run SonarQube analysis to check the code quality.
    Stage 3: Package the application into a docker file.
    Stage 4: Upload the docker file in docker hub.
    Stage 5: Update Kuberrnetes Deployment File with image tag
    Stage 6: Promote the application to a production environment using Argo CD.

## 5. Argo CD Locally
### 5.1 Install docker 
###  5.1.1 Install Minikube
###  5.1.2 Install Kubectl
###  5.2 ```sh Install the Controllers using ArgoCD operator``` https://operatorhub.io/operator/argocd-operator to manage the lifecycle of K8s controllers and comes with default configs
###  5.3 How to use the operator https://argocd-operator.readthedocs.io/en/latest/usage/basics/
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
   kubectl edit secret savings-app-argocd-cluster
   echo bas64enxryptedtextdjjfjkfkrjffjfjf= | base64 -d
   kubectl get deploy
   minikube service savings-app-argocd-server
   ```
   Getting pod endpoints
   ```sh
   kubectl get endpoints savings-app-service
   ```
   Getting the nodeIPs
   ```sh
   minikube service savings-app-service
   ```
   Getting replica set
   ```sh
   kubectl get rs
   ```
   Adding creds on K8s
   ```sh
   kubectl create secret generic aws-credentials \
  --from-literal=AWS_ACCESS_KEY_ID=your-access-key \
  --from-literal=AWS_SECRET_ACCESS_KEY=your-secret-key \
  --from-literal=AWS_REGION=us-east-1
  ```


