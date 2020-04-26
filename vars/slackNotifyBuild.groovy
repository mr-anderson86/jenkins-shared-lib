/**
 * Simply sends a Slack notification.
 * Need to have "Slack Notification Plugin" v2.0.1 installed in your Jenkins
 * In addition, configure the domain and team under Manage Jenkins -> Configure system
 * (Good tutorial: https://www.youtube.com/watch?v=TWwvxn2-J7E )
 *
 * @param String buildStatus
 * @return void
 *
 * @usage examples: 
 *        default: slackeNotifyBuild() //will send "Build Started" msg
 *        regular: slackeNotifyBuild(currentBuild.result)
 */

def call(String buildStatus = 'STARTED') {
  // build status of null means successful
  buildStatus =  buildStatus ?: 'SUCCESS'

  // Default values
  def colorName = 'RED'
  def colorCode = '#b30000'
  def subject = "${buildStatus}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'"
  def summary = "${subject} (${env.BUILD_URL})"

  // Override default values based on build status
  if (buildStatus == 'STARTED') {
    color = 'BLUE'
    colorCode = '#002db3'
  } else if (buildStatus == 'SUCCESS') {
    color = 'GREEN'
    colorCode = '#006400'
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
  slackSend (color: colorCode, message: summary)
}
