/**
 * Loads properties from file into env map
 * (Both in pipeline and in Shell)
 * Properties file needs to be at this structure:
 * VAR_1=Something
 * var_2="somthing"
 * var_3='something'
 * 
 * #commented and empty lines will be skipped
 * //commented line will be skipped
 * var_4=1234
 * var_5=1.2.3
 * var_6="1.2.3"
 *
 * @param String file
 * @param Boolean toVar (optional)
 * @return void or map, depends if toVar is true/false
 * @usage examples:
 *        Into env:
 *          loadProperties(file: "/path/to/file")
 *          echo env.myKey
 *
 *        into var:
 *          def myMap = loadProperties(file: "/path/to/file", toVar: true)
 *          echo myMap.myKey
 */

def call(String path, Boolean toVar = false) {
  def props = readFile path
  //Debugging
  //echo props
  if (!toVar) {
    props.split('\n').each { prop ->
      if (!isLineSkippable((String)prop)) {
        String key = (String)prop.split('=')[0]
        value = prop.split('=')[1]
        env."${key}" = value
      }
    }
  } else {
    Map m = [:]
    props.split('\n').each { prop ->
      if (!isLineSkippable((String)prop)) {
        String key = (String)prop.split('=')[0]
        value = prop.split('=')[1]
        m."${key}" = value
      }
    }
    return m
  }
}

def call(Map config) {
  if (!config.containsKey('file')) {
    error("Must provide 'file', exmaple - loadProperties(file: '/path/to/file')")
  }
  if (config.toVar) {
    return call(config.file, config.toVar)
  } else {
    call(config.file, config.toVar)
  }
}

def isLineSkippable(String line) {
    return (line.replaceAll(' ','').replace("\t", "").trim().isEmpty() || line.matches("(#|//).*"))
}
