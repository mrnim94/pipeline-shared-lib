def call(Map parameters = [:]) {
    node('jenkins-vm') {
        stage('build'){
        echo "this is scripted pipeline" 
        }
    }
}