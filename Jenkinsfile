@Library('my-shared-library') _

pipeline {
  ageny any
  
  stage {
    stage('init') {
      steps {
        echo "This is init"
        echo "Hoping that the getStageLog function will capture only this stage output"
      }
    }
    stage('test loadProperties') {
      steps {
        echo "Testing loadProperties function"
        echo ""
        echo "Env vars before test:"
        sh 'env'
        echo ""
        sh 'echo NAME=mr-anderson > env.properties'
        sh 'echo MY_VAR=my-val >> env.properties'
        sh 'echo SOME_VERSION=1.2.3 >> env.properties'
        sh 'echo MY_NUM=10 >> env.properties'
        loadProperties("${WORKSPACE}/env.properties")
        echo "Testing loaded vars..."
        echo "NAME = ${NAME}"
        sh "echo MY_VAR = ${MY_VAR}"
        sh "echo SOME_VERSION = ${env.SOME_VERSION}"
        sh 'echo MY_NUM = ${MY_NUM}'
        echo "Testing loadProperties done."
        echo ""
        echo "Env vars after test:"
        sh 'env'
      }
    }
    stage('test getStageLog') {
      steps {
        echo "Testing getStageLog function,"
        echo "showing output only from 'init' stage"...
        echo getStageLog('init')
        echo "Testing getStageLog done."
      }
    }
  }
}
