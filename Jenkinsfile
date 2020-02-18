@Library('my-shared-library') _
//This pipeline is to test the functions in this repo

pipeline {
  agent any
  
  stages {
    stage('init') {
      steps {
        echo "This is init"
        echo "Hoping that the getStageLog function will capture only this stage output"
        script {
          env.stages = []
          env.stages << env.STAGE_NAME
        }
      }
    }
    stage('test loadProperties') {
      steps {
        env.stages << env.STAGE_NAME
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
        script {
          assert NAME == 'mr-anderson'
          assert MY_VAR == 'my-val'
          assert SOME_VERSION == '1.2.3'
          assert MY_NUM == '10'
        }
        echo "Testing loadProperties done."
        echo ""
        echo "Env vars after test:"
        sh 'env'
      }
    }
    stage('test getStageLog') {
      steps {
        script { env.stages.push(env.STAGE_NAME) }
        echo "Testing getStageLog function,"
        echo "showing output only from 'init' stage..."
        echo getStageLog('init')
        echo "Testing getStageLog done."
      }
    }
    stage('test parseJson') {
      steps {
        echo "Testing parseJson function..."
        script {
          env.stages.push(env.STAGE_NAME)
          
          def listTest = '"myList": [4, 8, 15, 16, 23, 42]'
          def intText = '"number": 123'
          def stringText = '"name": "John Doe"'
          def jsonText = '{ ' + listTest + ', ' + intText + ', ' + stringText + ' }'
          echo "jsonText = ${jsonText}" 
          
          def myMap = parseJson(jsonText)
          echo "After parsing to json:"
          assert myMap instanceof Map
          
          echo "myMap.name = ${myMap.name}"
          echo "myMap.number = ${myMap.number}"
          echo "myMap.myList = ${myMap.myList}"
          
          assert myMap.myList instanceof List
          assert myMap.number == 123
          assert myMap.name == 'John Doe'
          
          echo "Pretty map printing:"
          parseJson.prettyPrint(myMap)
          
          def myJson = parseJson.mapToJson(myMap)
          echo "myJson = ${myJson}"
          
          echo "Pretty json printing:"
          parseJson.prettyPrint(myJson)
          echo "Pretty json printing testing done."
          
          echo "Testing parseJson.fromUrl..."
          echo "Grabbing json from currn build url: ${BUILD_URL}api/json"
          myMap = parseJson.fromUrl("${BUILD_URL}api/json")
          
          assert myMap instanceof Map
          echo "myMap.number = ${myMap.number}"
          echo "myMap.timestamp = ${myMap.timestamp}"
          echo "myMap.fullDisplayName = ${myMap.fullDisplayName}"
          echo "myMap.url = ${myMap.url}"
          echo "myMap.result = ${myMap.result}"
          
          assert myMap.number == env.BUILD_NUMBER as Integer
          assert myMap.fullDisplayName == "${JOB_NAME} #${BUILD_NUMBER}"
          assert myMap.url == env.BUILD_URL
          echo "Testing parseJson.fromUrl done."
          echo "Testing parseJson done."
        }
      }
    }
    stage('test getStagesDetails') {
      steps {
        echo "Testing getStagesDetails..."
        script {
          env.stages.push(env.STAGE_NAME)
          
          def myMap = getStagesDetails()
          assert  myMap instanceof Map
          echo "Printing in map:"
          parseJson.prettyPrint(myMap)
          
          env.stages.each {
            key = it.replace(" ","_")
            assert myMap."${key}_status" instanceof String
            assert myMap."${key}_dur".class == Integer    
          }
        }
        
        echo "Printing in json:"
        echo getStagesDetails('',true)
        echo "Testing getStagesDetails done."
      }
    }
  }
}
