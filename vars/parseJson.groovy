import groovy.json.JsonSlurper
import static groovy.json.JsonOutput.*
def call(String text) {
  /*Gets json as text,
    return it as map (using jsonSlurper: https://groovy-lang.org/json.html)
    jsonSlurper also puts the data within the map as: string, number, array, boolean, null 
    and date (based on the yyyy-MM-dd’T’HH:mm:ssZ date format)
    
    Might need admin script appoval
  */
  
  def jsonSlurper = new JsonSlurper()
  def data = jsonSlurper.parseText(text)
  assert data instanceof Map
  return data
}

def toJson(Map data) {
  return toJson(data)
}

def prettyPrint(Map data) {
  echo prettyPrint(toJson(data))
}
