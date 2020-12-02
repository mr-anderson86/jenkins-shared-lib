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
 * @param String slackTokenId (optional)
 * @param String slackChannel (optional)
 * @return void
 *
 * @usage examples: 
 *        slackNotifyBuild() //will send "Build Started" msg, to default domain and channel
 *        slackNotifyBuild(buildStatus: currentBuild.result)
 *        slackNotifyBuild(slackDomain: 'my-domain', slackTokenId: 'my-token-id', slackChannel: 'my-channel') //will send "Build Started" msg and override default domain/channel 
 *        slackNotifyBuild(buildStatus: currentBuild.result, slackDomain: 'my-domain', slackTokenId: 'my-token-id', slackChannel: 'my-channel')
 */

def call(String buildStatus = 'STARTED', String slackDomain = '', String slackTokenId = '', String slackChannel = '') {
  def colorCode = ''
  def msg = "${env.JOB_NAME} - #${env.BUILD_NUMBER}"
  
  if(currentBuild.number > 1 && currentBuild.getPreviousBuild().result != 'SUCCESS' && buildStatus == 'FAILURE') {
    buildStatus = 'STILL_FAILING'
  }
  if(currentBuild.number > 1 && currentBuild.getPreviousBuild().result != 'SUCCESS' && buildStatus == 'SUCCESS') {
    buildStatus = 'BACK_TO_NORMAL'
  }
  if(currentBuild.number > 1 && currentBuild.getPreviousBuild().result == 'SUCCESS' && buildStatus == 'BACK_TO_NORMAL') {
    /* just in case someone wants to use it to report only when "back to normal", and not every success.
     * (In such case, he/she can put it under post --> success --> slackNotifyBuild(buildStatus: "BACK_TO_NORMAL") )
     */
    return
  }
  
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
    msg = "${msg} ${buildStatus.toLowerCase().capitalize().replace('_',' ')} after ${durationString}"
    
    if (buildStatus == 'SUCCESS' || buildStatus == 'BACK_TO_NORMAL' ) {
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
  if (slackDomain == '' || slackTokenId == '' || slackChannel == '') {
    slackSend (color: colorCode, message: msg)
  } else {
    slackSend (color: colorCode, message: msg, teamDomain: slackDomain , tokenCredentialId: slackTokenId, channel: slackChannel)
  }
}

def call(Map config) {
  if (!config.containsKey('buildStatus')) {config.buildStatus = 'STARTED'}
  if (!config.containsKey('slackDomain') || !config.containsKey('slackTokenId')|| !config.containsKey('slackChannel')) {
    config.slackDomain = ''
  }
  
  if (config.slackDomain == '' || config.slackTokenId == '' || config.slackChannel == '') {
    call(config.buildStatus)
  } else {
    call(config.buildStatus, config.slackDomain, config.slackTokenId, config.slackChannel)
  } 
}
