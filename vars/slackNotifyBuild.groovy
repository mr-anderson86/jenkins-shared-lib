/**
 * Simply sends a Slack notification.
 * Need to have "Slack Notification Plugin" v2.0.1 installed in your Jenkins
 *
 * If you wish to use the method without suppluying domain, token and channel
 * Then configure the slack domain and team under Manage Jenkins -> Configure system -> Slack
 * (Good tutorial: https://www.youtube.com/watch?v=TWwvxn2-J7E )
 *
 * @param String buildStatus (optional)
 * @param String slackDomain (optional)
 * @param String slackToken (optional)
 * @param String slackChannel (optional)
 * @return void
 *
 * @usage examples: 
 *        slackeNotifyBuild() //will send "Build Started" msg, to default domain and channel
 *        slackeNotifyBuild(currentBuild.result)
 *        slackeNotifyBuild(currentBuild.result, 'my-domain', 'mYt0ken', 'my-channel')
 */

def call(String buildStatus = 'STARTED', String slackDomain = '', String slackToken = '', String slackChannel = '') {
  // build status of null means successful
  buildStatus =  buildStatus ?: 'SUCCESS'

  // Default values
  def colorName = 'RED'
  def colorCode = '#b30000'
  def subject = "${buildStatus}: ${env.JOB_NAME} - #${env.BUILD_NUMBER}"
  def summary = "${subject} (<${env.BUILD_URL}|Open>)"

  // Override default values based on build status
  if (buildStatus == 'STARTED') {
    color = 'BLUE'
    colorCode = '#002db3'
  } else if (buildStatus == 'SUCCESS') {
    color = 'GREEN'
    colorCode = '#00b33c'
  } else if (buildStatus == 'UNSTABLE') {
    color = 'YELLOW'
    colorCode = '#e6e600'
  } else if (buildStatus == 'ABORTED' || buildStatus == 'NOT_BUILT') {
    color = 'GREY'
    colorCode = '#808080'
  } else {
    color = 'RED'
    colorCode = '#b30000'
  }

  // Send notifications
  if (String slackDomain == '' || String slackToken == '' || String slackChannel == '') {
    slackSend (color: colorCode, message: summary)
  } else {
    slackSend (color: colorCode, message: summary, teamDomain: slackDomain , token: slackToken, channel: slackChannel)
  }
}
