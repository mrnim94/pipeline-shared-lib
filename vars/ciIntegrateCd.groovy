def call(Map parameters = [:]) {
    properties(
        [
            disableConcurrentBuilds()
        ]
    )

    /*
    * Get project name from SCM URL
    * Project name must not contain '.' character
    */
    this.project = scm.getUserRemoteConfigs()[0].getUrl().tokenize('/')[3].split("\\.")[0].toLowerCase()

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
            docker.build("${this.project}:${env.BUILD_ID}")
            // sh "docker build -t ${IMAGE_NAME}:latest ."
            // sh "docker build -t ${IMAGE_NAME}:${GIT_HASH} ."
            // script {
            //     app_latest = docker.build nameImage + ":latest"
            //     app_version = docker.build nameImage + ":$GIT_HASH"
            // }
        }
    }
}