/**
 * Just a simple wrapper for the git plugin
 * 
 * @param String url (required)
 * @param String branch (optional) - default is master
 * @param String creds (optional) - credentials ID, if needed
 * @return void
 *
 * @usage examples:
 *        gitPullChanges(url: 'https://github.com/my/repo.git')
 *        gitPullChanges(url: 'https://github.com/my/repo.git', branch: 'develop')
 *        gitPullChanges(url: 'https://github.com/my/repo.git', branch: 'develop', creds: 'my-credentials')
 */

def call(String url, String branch='master', String creds='') {
  if (url == null || url =='') {
    echo "url = ${url}"
    echo "branch = ${branch}"
    echo "credentials ID = ${creds}"
    throw new Exception("Method 'gitPullChanges' must contain param 'url'!") 
  }
  if (creds != '') {
    git url: "${url}", branch: "${branch}", credentialsId: creds    
  } else {
    git url: "${url}", branch: "${branch}"
  }
}

def call(Map config) {
  if (!config.containsKey('url')) {
    echo "url = ${config.url}"
    echo "branch = ${config.branch}"
    echo "creds = ${config.creds}"
    throw new Exception("Method 'gitPullChangesGHE' must contain param 'url'!") 
  }
  if (!config.containsKey('branch')) {config.branch = 'master'}
  if (!config.containsKey('creds')) {config.creds = ''}
  call(config.url, config.branch, config.creds)
}
