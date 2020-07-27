/**
 * Loads properties from file into env map
 * (Both in pipeline and in Shell)
 * Properties file needs to be at this structure:
 * 
 * VAR_1=Something
 * var_2="somthing"
 * var_3='something'
 * var_4=1234
 * var_5=1.2.3
 * var_6="1.2.3"
 * And os on...
 * @return void
 *
 * @usage examples:
 *        loadProperties('path/to/file')
 *        loadProperties(filePath: 'path/to/file')
 */

def call(String path) {    
    def props = readFile path
    //Debugging
    //echo props
    props.split('\n').each { prop ->
        String key = (String)prop.split('=')[0]
        value = prop.split('=')[1]
        env."${key}" = value
    }
}

def call(Map config) {
  call(config.filePath)
}
