pipeline {
agent any
stages{
  stage('build'){
    steps{
    sh '/home/rroussel/Documents/apache-maven-3.6.3/bin/mvn -Prelease clean install'
    }
  }
}
}
