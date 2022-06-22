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
            sh "spin application save --application-name ${this.project} --owner-email mr.nim94@gmail.com --cloud-providers 'kubernetes' --config /var/lib/jenkins/.spin/config-nimtechnology"
            sh '''cat << EOF > /var/lib/jenkins/.spin/pipe/file1.json
{
    "name": "pipeline-${this.project}",
    "application": "${this.project}",
    "exclude": [],
    "keepWaitingPipelines": false,
    "lastModifiedBy": "anonymous",
    "limitConcurrent": true,
    "notifications": [],
    "parameterConfig": [],
    "schema": "v2",
    "stages": [],
    "template": {
        "artifactAccount": "front50ArtifactCredentials",
        "reference": "spinnaker://nimtSpinnakerTemplateV1",
        "type": "front50/pipelineTemplate"
    },
    "triggers": [],
    "type": "templatedPipeline",
    "updateTs": "1652627797000",
    "variables": {
        "kind": "deployment",
        "workloadName": "default-nimtechnology"
    }
}
            '''
        }
    }
}