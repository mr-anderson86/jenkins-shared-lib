import groovy.json.JsonSlurper
import static groovy.json.JsonOutput.*

/*
  INPUT: json as string
  RETURN: data as map (using jsonSlurper: https://groovy-lang.org/json.html)
  
  jsonSlurper also puts the data within the map as: string, number, array, boolean, null 
  and date (based on the yyyy-MM-dd’T’HH:mm:ssZ date format)
  
  PLEASE NOTE: Might need admin script appoval (unless using as shared library)
*/
def call(String text) {  
  def jsonSlurper = new JsonSlurper()
  def data = jsonSlurper.parseText(text)
  assert data instanceof Map
  return data
}

/*
  INPUT: Map or json (in text)
  OUTPUT: in json it'll print in one line
          in map it'll print with indentation
*/
def prettyPrint(data) {
  echo prettyPrint(toJson(data))
}

def mapToJson(Map data) {
  return toJson(data)
}
