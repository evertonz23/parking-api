
stage 'Dev'
node('docker-cloud') {
    checkout scm
    mvn 'clean package'
    // mvn '-o clean package'
    // archive 'target/x.war'
}