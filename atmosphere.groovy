node {
    notify('Started')
    try {
        stage 'checkout' {
            git changelog: false, poll: false, url: 'https://github.com/lev-tmp/jenkins2-course-spring-boot.git'
        }
    
        stage 'compiling, test, packaging'
        def project_path='spring-boot-samples/spring-boot-sample-atmosphere'
        
        dir(project_path) {
            sh 'mvn clean package'
        
            stage 'archiving artifacts'
            archiveArtifacts "target/*jar"
            
            step([$class: 'JUnitResultArchiver', testResults: 'target/surefire-reports/*xml'])
    
        }
    notify('Success')
    } catch (err) {
        notify("Error ${err}")
        currentBuild.result = 'FAILURE'
    }
}

def notify(status){
    emailext (
      to: "lev@gmail.com",
      subject: "${status}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
      body: """<p>${status}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
        <p>Check console output at <a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a></p>""",
    )
}
