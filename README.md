# jenkins-shared-lib

## Description

This repo contains simple methods which could be use in every Jenkins pipeline.  
(Created by mr-anderson86, started @02/2020)

## Table of Contents

1. [Main stuff](#main-stuff)
2. [Notes](#notes)
3. [Prerequisites](#prerequisites)
4. [Usage and examples](#usage-and-examples)

### Main stuff:
* As mentioned in the description, this repo contains all sorts of simple methods which could be use in every Jenkins pipeline. Such as below:
* [getStageLog](vars/getStageLog.groovy) - captures the log from a spesific stage within the current build.
* [loadProperties](vars/loadProperties.groovy) - loads all variables from file both into shell and into pipeline's env map.
* [Jenkinsfile](Jenkinsfile) - a pipeline which tests the functions in this repo.
* Hopefully to have more useful methods in the future :-)

### Notes:
* I'm trying my best to make those methods work for every Jenkins, but if you found any bugs, you are more than welcome to fix it :-)
* In addition, you are all welcome to add your own methods here, just please make sure it works for every jenkins, and if it needs any plguins installed or script admin approval or etc, please mention it in a comment within the source code. Thanks.

### Prerequisites
* Jenkins ;-)  
(You might want to upgrade your Jenkins version to a more advaned one, that's for you own considuration.)
* If you want to use the methods from this repo, you'll need to add it as a [Global Pipeline Libraries](https://jenkins.io/doc/book/pipeline/shared-libraries/) in your Jenkins
* Any plugins which will add support for coding in your Jenkinsfiles   
(example: Artifactory plugin version >= 3.0.0, without it you can't use Artifactory upload methods).
* Some of the code here might need to be [approved by Jenkins admin](https://jenkins.io/doc/book/managing/script-approval/), I'm trying to avoid it as much as I can, but sadly not always it's possible.

### Usage and examples
Assuming you added this repo in your Jenkins "Global Pipeline Libraries" under the name "my-shared-library".  
Examples are below:

#### Declarative pipeline
```groovy
@Library('my-shared-library') _

pipeline {
  agent { node { label 'my_node' } }
  stage ('stage_1') {
    steps {
      //can see the code under vars/loadProperties.groovy
      loadProperties('path/to/file')

      // do some commands
    }
    post {
      always {
        //can see the code under vars/getStageLog.groovy
        script { env.STAGE_LOG = getStageLog('stage_1') }
        
        writeFile file: "stage.log", text: "${env.STAGE_LOG}"
        emailext (
          subject: "[${JOB_NAME}] #${BUILD_NUMBER}] - Stage_1 status",
          body: 'See log in attachment.',
          to: "some.mail@some.company.com",
          attachmentsPattern: "stage.log"
        )
        script { env.STAGE_LOG = "" }
      }
    }
  }
  stage ('stage_2') {
    steps {
      // do some other commands commands
      script { env.STAGE_LOG = getStageLog('stage_2') }
      emailext (
        mimeType: 'text/html',
        subject: "[${JOB_NAME}] #${BUILD_NUMBER}] - Stage_2 status",
        body: """<html><body>Log is below: </br>${STAGE_LOG} </body></html>""",
        to: "some.mail@some.company.com"
      )
      script { env.STAGE_LOG = "" }
    }
  }
}
```

#### Scripted pipeline
```groovy
@Library('my-shared-library') _

properties([
   pipelineTriggers([
    [$class: "GitHubPushTrigger"]
   ]),
   disableConcurrentBuilds()
])

node('my_node') {
  stage('stage_1') {
    checkout scm
    //can see the code under vars/loadProperties.groovy
    loadProperties('path/to/file')

    // do some commands

    //can see the code under vars/getStageLog.groovy
    env.STAGE_LOG = getStageLog('stage_1')
    writeFile file: "stage.log", text: "${env.STAGE_LOG}"
    
    emailext subject: "[${JOB_NAME}] #${BUILD_NUMBER}] - Stage_1 status",
      body: 'See log in attachment.',
      to: "some.mail@some.company.com",
      attachmentsPattern: "stage.log"
    
    env.STAGE_LOG = ""
  }
  
  stage('stage_2') {
    // do some commands
    env.STAGE_LOG = getStageLog('stage_2')
    
    emailext mimeType: 'text/html',
      subject: "[${JOB_NAME}] #${BUILD_NUMBER}] - Stage_2 status",
      body: """<html><body>Log is below: </br>${STAGE_LOG} </body></html>""",
      to: "some.mail@some.company.com"
    
    env.STAGE_LOG = ""
  }
}
```

### The end. Enjoy :-)
