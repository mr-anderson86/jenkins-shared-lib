import groovy.json.JsonSlurper
import static groovy.json.JsonOutput.*
//PLEASE NOTE: Might need admin script appoval (unless using as shared library)

/**
 * Returns json string as a Map using jsonSlurper: https://groovy-lang.org/json.html ).
 * jsonSlurper also puts the data within the map as: string, number, array, boolean, null 
 * and date (based on the yyyy-MM-dd’T’HH:mm:ssZ date format) 
 *
 * @param json, String in json format
 * @return data, as map
 *
 * @usage example: def myMap = parseJson('{ "name": "John Doe" }')
 */
def call(String json) {  
  def jsonSlurper = new JsonSlurper()
  def data = jsonSlurper.parseText(json)
  assert data instanceof Map
  return data
}

/**
 * @param data, Map or String in json format
 * @output if data is json (string), then it'll print in one line
 *         if data is map, then it'll print with indentation
 *
 * @usage example: parseJson.prettyPrint(data)
 */
def prettyPrint(data) {
  echo prettyPrint(toJson(data))
}

/**
 * @param data, the data in Map
 * @return String in json format
 *
 * @usage example: def json = parseJson.mapToJson(data)
 */
def mapToJson(Map data) {
  return toJson(data)
}

/**
 * @param url, in a string
 * @return the json from the URL as a map
 *
 * @usage example: def myMap = parseJson.fromUrl("${BUILD_URL}/api/json")
 */
def fromUrl(String url) {
  return new JsonSlurper().parseText(new URL(url))
}
