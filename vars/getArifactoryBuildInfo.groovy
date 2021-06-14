/*
 * returns map containing info about an Artifactory build which was uploaded via Jenkins.
 *
 * @param String artUrl (required)
 * @param String buildName (required)
 * @param int buildNumber (required)
 * @param String artCredentialsId (required)
 * @return Map
 *
 * @usage example:
 *        def myMap = getArifactoryBuildInfo(artUrl: 'https://my.artifactory.com/artifactory', buildName: 'my-job', 
 *                                           buildNumber: 300, artCredentialsId: 'my-artifactory-creds')
 */

def call(String artUrl, String buildName, int buildNumber, String artCredentialsId) {
  def myMap = [:]
  withCredentials([usernamePassword(credentialsId: artCredentialsId, usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
    myMap = parseJson.fromUrl("${artUrl}/api/build/${buildName}/${buildNumber}", "${USERNAME}:${PASSWORD}")
  }
  return myMap
}

def call(Map config) {
  for (var in ['artUrl', 'buildName', 'buildNumber', 'artCredentialsId'] {
    if (!config.containsKey(var)) {
      throw new Exception("Method 'getArifactoryBuildInfo' must contain params 'artUrl', 'buildName', 'buildNumber' , 'artCredentialsId' !!!")
    }
  }
  call(map.artUrl, map.buildName, map.buildNumber, map.artCredentialsId)
}
