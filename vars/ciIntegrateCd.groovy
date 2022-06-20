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
        stage('Build and Push images') {

            docker.withRegistry('https://docker.nimtechnology.com', 'private-docker-hub') {

                def customImage = docker.build("docker.nimtechnology.com/nim/${this.project}:${env.BUILD_ID}")

                /* Push the container to the custom Registry */
                customImage.push()
            }
        }
        stage('Deploy the workload') {
            sh(returnStdout: true,
                script: """
                    spin -h\
                    date
                    """,
                label: "integrate_spinnaker")
        }
    }
}