def call(Map parameters = [:]) {
    properties(
        [
            disableConcurrentBuilds()
        ]
    )
    node('jenkins-vm') {
        stage('Checkout'){
            checkout([
                $class: 'GitSCM', 
                branches: [[name: "origin/${BRANCH_NAME}"]], 
                userRemoteConfigs: [[
                    url: scm.userRemoteConfigs[0].url
                ]]
            ])  
        }
        stage('Build images') {
            steps {
                // sh "docker build -t ${IMAGE_NAME}:latest ."
                // sh "docker build -t ${IMAGE_NAME}:${GIT_HASH} ."
                script {
                  app_latest = docker.build nameImage + ":latest"
                  app_version = docker.build nameImage + ":$GIT_HASH"
                }
            }
        }
    }
}