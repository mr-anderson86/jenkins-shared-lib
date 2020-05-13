/**
 * Simply sends a Slack notification.
 * Need to have "Slack Notification Plugin" v2.0.1 installed in your Jenkins
 *
 * If you wish to use the method without supplying domain, token and channel
 * Then configure the slack domain and team under Manage Jenkins -> Configure system -> Slack
 * (Good tutorial: https://www.youtube.com/watch?v=TWwvxn2-J7E )
 *
 * Can use both with direct strings or as a map, see examples below:
 * @param String buildStatus (optional)
 * @param String slackDomain (optional)
 * @param String slackToken (optional)
 * @param String slackChannel (optional)
 * @return void
 *
 * @usage examples: 
 *        slackNotifyBuild() //will send "Build Started" msg, to default domain and channel
 *        slackNotifyBuild(currentBuild.result)
 *        slackNotifyBuild(currentBuild.result, 'my-domain', 'mYt0ken', 'my-channel') //will override default domain/channel
 *
 *        //Examples with map (instead of direct strings input)
 *        slackNotifyBuild(buildStatus: currentBuild.result)
 *        slackNotifyBuild(slackDomain: 'my-domain', slackToken: 'mYt0ken', slackChannel: 'my-channel') //will send "Build Started" msg and override default domain/channel 
 *        slackNotifyBuild(buildStatus: currentBuild.result, slackDomain: 'my-domain', slackToken: 'mYt0ken', slackChannel: 'my-channel')
 */

def call(String buildStatus = 'STARTED', String slackDomain = '', String slackToken = '', String slackChannel = '') {
  def colorCode = ''
  def msg = "${env.JOB_NAME} - #${env.BUILD_NUMBER}"
  
  if (buildStatus == 'STARTED') {
    //BLUE
    colorCode = '#002db3'
    msg = "${msg} ${currentBuild.getBuildCauses()[0].shortDescription}"
  }
  else {
    def durationMills = currentBuild.duration
    def durationCents = durationMills / 1000 - (int)(durationMills / 1000)
    def durationSeconds = (int)(durationMills / 1000) % 60 + durationCents
    int durationMinutes = (int)(durationMills / (1000*60)) % 60
    int durationHours   = (int)(durationMills / (1000*60*60)) % 24
    def durationString = ""
    if (durationHours >= 1) { durationString = "${durationHours} hrs, "}
    if (durationMinutes >= 1) { durationString = "${durationString}${durationMinutes} mins, "}
    durationString = "${durationString}${durationSeconds} secs" 
    msg = "${msg} ${buildStatus.toLowerCase().capitalize()} after ${durationString}"
    
    if (buildStatus == 'SUCCESS') {
      //GREEN
      colorCode = '#00b33c'
    } else if (buildStatus == 'UNSTABLE') {
      //YELLOW
      colorCode = '#e6e600'
    } else if (buildStatus == 'ABORTED' || buildStatus == 'NOT_BUILT') {
      //GREY
      colorCode = '#808080'
    } else {
      //RED
      colorCode = '#b30000'
    }
  }
  msg = "${msg} (<${env.BUILD_URL}|Open>)"

  // Send notifications
  if (slackDomain == '' || slackToken == '' || slackChannel == '') {
    slackSend (color: colorCode, message: msg)
  } else {
    slackSend (color: colorCode, message: msg, teamDomain: slackDomain , token: slackToken, channel: slackChannel)
  }
}

def call(Map config) {
  if (!config.containsKey('buildStatus')) {config.buildStatus = 'STARTED'}
  if (!config.containsKey('slackDomain') || !config.containsKey('slackToken')|| !config.containsKey('slackChannel')) {
    config.slackDomain = ''
  }
  
  if (config.slackDomain == '' || config.slackToken == '' || config.slackChannel == '') {
    call(config.buildStatus)
  } else {
    call(config.buildStatus, config.slackDomain, config.slackToken, config.slackChannel)
  } 
}
