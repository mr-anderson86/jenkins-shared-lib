def call(String stageName = '') {
	/*Return spesific stage log in current build.
	  if no stage name was given, or stage name was not found,
	  Then it'll return the whole build log up to function calling step.
	*/
	
	/*The getLog method below requiers Admin Script Approval.
	  I could've use currentBuild.rawBuild.log and skip the approval part, but it contains all those "(hide)" links, 
	  which in mail or file looks very messy.
	  The getLog method is the only way I found to get the log without those messy links. :/
	*/
	def buildLog = currentBuild.rawBuild.getLog(currentBuild.rawBuild.log.length()).join('\n')
	//println buildLog

	String strStageStart = "[Pipeline] { (${stageName})"
	int indexStart = buildLog.indexOf(strStageStart)
	String strStageEnd = "[Pipeline] // stage"
	int indexEnd = buildLog.indexOf(strStageEnd, indexStart)
	int endLength = strStageEnd.length()
	if (indexStart < 0) { indexStart = 0; indexEnd = -1}

	//Debugging
	/*echo "Log length is: " + buildLog.length()
	echo "strStageStart = " + strStageStart
	echo "indexStart = " + indexStart
	echo "strStageEnd = " + strStageEnd
	echo "indexEnd = " + indexEnd*/

	if ( indexEnd > 0 ) return buildLog.substring(indexStart,indexEnd + endLength)
	else return buildLog.substring(indexStart)
}
