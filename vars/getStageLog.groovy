def call(String stageName = '') {
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
