import groovy.json.JsonSlurper
import static groovy.json.JsonOutput.*
//PLEASE NOTE: Need Blue ocean plugin installed on your Jenkins

/**
 * Returns map containing all stages statuses and durations in miils
 *
 * @param creds, String which contains Jenkins credentails (Must if no read permissions to anonymous)
 *               format: 'username:password'
 * @param json, if true, then it'll return the data in json format String, else it'll return it in map.
 * @return data, as map or json string
 *
 * @usage examples: def myMap = getStagesDetails()
 *                  def myMap = getStagesDetails('username:password')
 *                  def myJson = getStagesDetails(true)
 *                  def myJson = getStagesDetails('username:password', true)
 *                  def myJson = getStagesDetails(true, 'username:password')
 */

def call(String creds = null, Boolean json = false) {  
  def url = "${JENKINS_URL}blue/rest/organizations/jenkins/pipelines/${JOB_NAME}/runs/${BUILD_NUMBER}/nodes/"
  if (creds?.trim()) {
    url_host = env.JENKINS_URL.split('//')[1].split(':')[0]
    url_port = env.JENKINS_URL.split('//')[1].split(':')[1]
    url = "http://${creds}@${url_host}:${url_port}blue/rest/organizations/jenkins/pipelines/${JOB_NAME}/runs/${BUILD_NUMBER}/nodes/"
  }
  def data = [:]
  def map = new JsonSlurper().parseText(new URL(url).text)
  
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
