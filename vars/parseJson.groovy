import groovy.json.JsonSlurperClassic
import static groovy.json.JsonOutput.*
//PLEASE NOTE: Might need admin script appoval (unless using as shared library)

/**
 * Returns json string as a Map (using jsonSlurper: https://groovy-lang.org/json.html ).
 * jsonSlurper also puts the data within the map as: string, number, array, boolean, null 
 * and date (based on the yyyy-MM-dd’T’HH:mm:ssZ date format) 
 *
 * @param json, String in json format
 * @return data, as map
 *
 * @usage example: def myMap = parseJson('{ "name": "John Doe" }')
 */
@NonCPS
def call(String json) {  
  def jsonSlurper = new JsonSlurperClassic()
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
 * @param auth, String in format 'username:password'
 * @return the json from the URL as a map
 *
 * @usage examples: def myMap = parseJson.fromUrl("${BUILD_URL}/api/json")
 *                  def myMap = parseJson.fromUrl("${BUILD_URL}/api/json", 'myUser:myPassword')
 */
@NonCPS
def fromUrl(String url_path, String auth='') {
  URL url = new URL(url_path)
  if (auth?.trim()) {
    def authString = auth.getBytes().encodeBase64().toString()
    return new JsonSlurperClassic().parseText(url.getText(requestProperties: ["Authorization": "Basic ${authString}"]))
  } else {
    return new JsonSlurperClassic().parseText(url.text)
  }
}
