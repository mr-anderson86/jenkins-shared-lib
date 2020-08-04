/**
 * Build and push docker image into docker registry
 * If forceBuild==false, then it'll build with cache-from latest, and compare hash between latest and new image.
 * and only if hashes are different, then it'll push new image. (Otherwise, won't push new image)
 * Works only on Linux
 * 
 * @param String registry (required)
 * @param String creds (required)
 * @param String image (required)
 * @param String tag (required)
 * @param Boolean tagLatest (optional, default=true)
 * @param Boolean forceBuild (optional, default=false)
 * @param String arguments (optional)
 * @return void
 *
 * @usage examples:
 *        buildAndPushDockerImage(registry: "my.example.registry", creds: "my-creds-1234", image: 'repo/my-image', tag: '1.0.0')
 *        buildAndPushDockerImage(..., tagLatest:false) //if there's new image to push, it won't tag it as latest
 *        buildAndPushDockerImage(..., forceBuild: true)
 *        buildAndPushDockerImage(..., arguments: "-f MyDockerFile ./my_sub_dir")
 */

def call(String registry, String creds, String image, String tag, Boolean tagLatest=true, Boolean forceBuild=false, String arguments='') {
  for (var in [registry, creds, image, tag]) {
    if (var == null || var == '') {
      throw new Exception("Method 'buildAndPushDockerImage' must contain params 'registry' ,'creds', 'image', 'tag' !!!")
    }
  }
  String dockerRegistry = registry.replaceAll('https://','').replaceAll('http://','')
  String dockerCredential = creds
  String dockerImageName = "${image}"
  String dockerImageFull = "${dockerRegistry}/${dockerImageName}:${tag}"
  String dockerImageLatest = "${dockerRegistry}/${dockerImageName}:latest"
  if (arguments == '') {
    arguments = "."
  }
  
  sh "docker pull ${dockerImageLatest} || true"
  echo "Building and pushing docker image ${dockerImageFull}"
  docker.withRegistry("${registry}", "${dockerCredential}") {
    def newImage
    if (!forceBuild) {
      newImage = docker.build(dockerImageFull, "--cache-from ${dockerImageLatest} ${arguments}")
      image_hash = sh(returnStdout: true, script: "docker inspect --format='{{index .RepoDigests 0}}' ${dockerImageFull}").trim()
      latest_exist = sh(returnStdout: true, script: "docker images | grep -c ${dockerImageLatest}").trim() as Integer
      if (latest_exist > 0 ) {
        latest_hash = sh(returnStdout: true, script: "docker inspect --format='{{index .RepoDigests 0}}' ${dockerImageLatest}").trim()
        if (image_hash != latest_hash) {
          newImage.push()
          if (tagLatest) {
            newImage.push('latest')
          }
        }
      }
    } else {
      newImage = docker.build(dockerImageFull, "${arguments}")
      newImage.push()
      if (tagLatest) {
        newImage.push('latest')
      }
    }
    newImage.push()
  }
  
  //Cleanup
  sh "docker rmi ${dockerImageFull} ${dockerImageLatest}"
  sh "docker rmi ${dockerImageLatest} || true"
  sh "docker images -f dangling=true | grep ${dockerRegistry}/${dockerImageName} | awk '{print \$3}' | xargs --no-run-if-empty docker rmi"
}

def call(Map config) {
  if (!config.containsKey('tagLatest')) { config.tagLatest = true }
  if (!config.containsKey('forceBuild')) { config.forceBuild = false }
  if (!config.containsKey('arguments')) { config.arguments = '' }
  call(config.registry, config.creds, config.image, config.tag, config.tagLatest, config.forceBuild, config.arguments)
}
