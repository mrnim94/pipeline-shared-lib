def call(Map parameters = [:]) {
    properties(
        [
            disableConcurrentBuilds()
        ]
    )
    node('jenkins-vm') {
        stage('build'){
            checkout([
                $class: 'GitSCM', 
                branches: [[name: "origin/${BRANCH_NAME}"]], 
                userRemoteConfigs: [[
                    url: scm.userRemoteConfigs[0].url
                ]]
            ])
            // def  myCustomUbuntuImage = docker.build("my-ubuntu:my-latest")
            
            // myCustomUbuntuImage.inside{
            //     sh 'cat /etc/lsb-release'
            // }
            
        }
    }
}