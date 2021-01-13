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
 * @param boolean isBotUser (optional) - if true, then it'll send msg from the bot, using token relevant token ID
 * @param String slackEmoji (optional) - for example - slackEmoji: "thumbsup" will send a message along with a thumbs up emoji.
 * @param String slackDomain (optional)
 * @param String slackTokenId (optional)
 * @param String slackChannel (optional)
 * @return void
 *
 * @usage examples: 
 *        sendSlackMsg(msg: 'this is my msg', color: 'green', appendUrl: false)
 *        sendSlackMsg(msg: 'this is my msg', color: 'green', appendUrl: false, slackDomain: 'my-domain', slackTokenId: 'my-token-id', slackChannel: 'my-channel')
 *
 *       can also send from a bot directly to user (see slackChannel parameter):
 *       sendSlackMsg(msg: 'this is my msg', color: 'green', appendUrl: false, isBotUser: true, slackEmoji: "thumbsup", 
 *                    slackDomain: 'my-domain', slackTokenId: 'my-token-id', slackChannel: 'some.user')
 */

def call(String msg, String color = '', boolean appendUrl = true ,
         boolean isBotUser=false, String slackEmoji='',
         String slackDomain = '', String slackTokenId = '', String slackChannel = '') {
         
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
  
  conf = [:]
  conf.message = msg
  conf.color = colorCode
  conf.botUser = isBotUser
  conf.iconEmoji = slackEmoji
  
  if (!(slackDomain == '' || slackTokenId == '' || slackChannel == '')) {
    conf.teamDomain = slackDomain
    conf.tokenCredentialId = slackTokenId
    conf.channel = slackChannel
  }
  slackSend(conf)
}

def call(Map config) {
  if (!config.containsKey('msg')) { throw new Exception("Method 'sendSlackMsg' must contain param 'msg'!") }
  if (!config.containsKey('color')) { config.color = '' }
  if (!config.containsKey('slackEmoji')) { config.slackEmoji = '' }
  if (!config.containsKey('isBotUser')) { config.isBotUser = false }
  if (!config.containsKey('appendUrl')) { config.appendUrl = true }
  if (!config.containsKey('slackDomain') || !config.containsKey('slackTokenId') || !config.containsKey('slackChannel')) {
    config.slackDomain = ''
    config.slackTokenId = ''
    config.slackChannel = ''
  }
  
  call(config.msg, config.color, config.appendUrl, config.isBotUser, config.slackEmoji, config.slackDomain, config.slackTokenId, config.slackChannel) 
}
