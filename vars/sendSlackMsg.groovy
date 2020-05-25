/**
 * Simply sends a customed message to a Slack channel
 * Need to have "Slack Notification Plugin" v2.0.1 installed in your Jenkins
 *
 * If you wish to use the method without supplying domain, token and channel
 * Then configure the slack domain and team under Manage Jenkins -> Configure system -> Slack
 * (Good tutorial: https://www.youtube.com/watch?v=TWwvxn2-J7E )
 *
 * Can use both with direct strings or as a map, see examples below:
 * @param String msg (requiered)
 * @param String color (optional)
 * @param boolean appendUrl (optional)
 * @param String slackDomain (optional)
 * @param String slackTokenId (optional)
 * @param String slackChannel (optional)
 * @return void
 *
 * @usage examples: 
 *        sendSlackMsg(msg: 'this is my msg', color: 'green', appendUrl: false)
 *        sendSlackMsg(msg: 'this is my msg', color: 'green', appendUrl: false, slackDomain: 'my-domain', slackTokenId: 'my-token-id', slackChannel: 'my-channel')
 */

def call(String msg, String color = '', boolean appendUrl = true ,String slackDomain = '', String slackTokenId = '', String slackChannel = '') {
  def colorCode = ''
  
  if (color.toLowerCase() == 'blue') {
    colorCode = '#002db3'
  } else if (color.toLowerCase() == 'green') {
    colorCode = '#00b33c'
  } else if (color.toLowerCase() == 'yellow') {
    colorCode = '#e6e600'
  } else if (color.toLowerCase() == 'grey') {
    colorCode = '#808080'
  } else if (color.toLowerCase() == 'red') {
    colorCode = '#b30000'
  } else if (color.toLowerCase() == 'black') {
    colorCode = '#000000'
  } else if (color.toLowerCase() == 'white') {
    colorCode = '#FFFFFF'
  } else {
    //default is grey
    colorCode = '#808080'
  }
  
  if (appendUrl) {
    msg = "${msg} (<${env.BUILD_URL}|Open>)"
  }

  // Send notifications
  if (slackDomain == '' || slackTokenId == '' || slackChannel == '') {
    slackSend (color: colorCode, message: msg)
  } else {
    slackSend (color: colorCode, message: msg, teamDomain: slackDomain , tokenCredentialId: slackTokenId, channel: slackChannel)
  }
}

def call(Map config) {
  if (!config.containsKey('msg')) { error "Method 'sendSlackMsg' must contain param 'msg'!" }
  if (!config.containsKey('color')) { config.color = '' }
  if (!config.containsKey('appendUrl')) { config.appendUrl = true }
  if (!config.containsKey('slackDomain') || !config.containsKey('slackTokenId') || !config.containsKey('slackChannel')) {
    config.slackDomain = ''
  }
  
  if (config.slackDomain == '' || config.slackTokenId == '' || config.slackChannel == '') {
    call(config.msg, config.color, config.appendUrl)
  } else {
    call(config.msg, config.color, config.appendUrl, config.slackDomain, config.slackTokenId, config.slackChannel)
  } 
}
