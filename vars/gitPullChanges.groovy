/**
 * Pull changes (or total clone) from Git url
 * 
 * @param String url (required)
 * @param String branch (optional) - default is master
 * @param String creds (optional) - credentials ID, if needed
 * @param Boolean justPullChanges (optional) - if true then it'll pull only change, otherwise will clone all branch
 * @return void
 *
 * @usage examples:
 *        gitPullChanges(url: 'https://github.com/my/repo.git')
 *        gitPullChanges(url: 'https://github.com/my/repo.git', branch: 'develop')
 *        gitPullChanges(url: 'https://github.com/my/repo.git', branch: 'develop', creds: 'my-credentials', justPullChanges: true)
 */

def call(String url, String branch='master', String creds='', Boolean justPullChanges=false) {
  if (url == null || url =='') {
    echo "url = ${url}"
    echo "branch = ${branch}"
    echo "credentials ID = ${creds}"
    throw new Exception("Method 'gitPullChangesGHE' must contain param 'url'!") 
  }
  if (creds != '') {
    if (!justPullChanges) {
      echo "Cloning the whole branch"
      git url: "${url}", branch: "${branch}", credentialsId: creds
    } else {
      echo "Pulling only changes"
      withCredentials([usernamePassword(credentialsId: creds, usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
        repo = url.replaceAll('https://','').replaceAll('http://','')
        protocol = url.find("http.*://")
        sh "git pull ${protocol}${USERNAME}:${PASSWORD}@${repo} ${branch}"
      }
    }
  } else {
    if (!justPullChanges) {
      echo "Cloning the whole branch"
      git url: "${url}", branch: "${branch}"
    } else {
      echo "Pulling only changes"
      sh "git pull ${url} ${branch}"
    }
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
  if (!config.containsKey('justPullChanges')) {config.justPullChanges = false}
  call(config.url, config.branch, config.creds, config.justPullChanges)
}
