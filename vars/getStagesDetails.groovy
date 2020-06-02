import groovy.json.JsonSlurper
import static groovy.json.JsonOutput.*
//PLEASE NOTE: Need Blue ocean plugin installed on your Jenkins

/**
 * Returns map containing all stages statuses and durations in miils
 *
 * @param creds, String which contains Jenkins credentails (Must if no read permissions to anonymous)
 *               format: 'username:password'
 * @param toJson, if true, then it'll return the data in json format String, else it'll return it in map.
 *
 * @return data, as map or json string
 *
 * @usage examples: def myMap = getStagesDetails()
 *                  def myMap = getStagesDetails(creds: 'username:password')
 *                  def myJson = getStagesDetails(toJson: true)
 *                  def myJson = getStagesDetails(creds: 'username:password', toJson: true)
 *                  def myJson = getStagesDetails(toJson: true, creds: 'username:password')
 */

def call(String creds = null, Boolean json = false) {  
  def url_path = "${JENKINS_URL}blue/rest/organizations/jenkins/pipelines/${JOB_NAME}/runs/${BUILD_NUMBER}/nodes/"
  URL url = new URL(url_path)
  def map
  
  if (creds?.trim()) {
    def authString = creds.getBytes().encodeBase64().toString()
    map = new JsonSlurper().parseText(url.getText(requestProperties: ["Authorization": "Basic ${authString}"]))
  } else {
    map = new JsonSlurper().parseText(url.text)
  }
  
  def data = [:]
  
  map.each {
    //Debugging
    /*println "Stage name = ${it.displayName}"
    println "Stage status = ${it.result}"
    println "Stage duration in mills = ${it.durationInMillis}"
    println ""*/
    
    key = it.displayName.replace(" ","_")
    data."${key}_status" = it.result
    data."${key}_duration" = it.durationInMillis
  }
  if (json) return toJson(data)
  else return data
}

def call(Boolean json, String creds = null) {
  return call (creds,json)
}

def call(Map config) {
  if (!config.containsKey('creds')) { config.arguments = '' }
  if (!config.containsKey('toJson')) { config.tagLatest = false }
  return call(config.creds, config.toJson)
}
