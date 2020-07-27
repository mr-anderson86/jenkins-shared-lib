/**
 * Read job names from a file, and triggers them all in parallel
 *
 * @param String jobsFile (required)
 * @param List<params> parameters (optional)
 * @usage example:
 *        triggerJobsFromFile(jobsFile: 'triggers.txt')
 *        triggerJobsFromFile(jobsFile: 'triggers.txt', parameters: currentBuild.rawBuild.getAction(ParametersAction).getParameters())
 */

def call(String jobsFile, parameters = []) {
    file = readFile jobsFile
    lines = file.readLines()
    builds = [:]
    jobs = []
    if(lines != null){
        buildParams = parameters
        int i = 0
        for(job in lines){
            jobs << job
            //echo "build job: ${jobs[i]}, parameters: ${buildParams}"
            builds["Job_" + job] = {
                build job: jobs[i++], parameters: buildParams
            }
        }
        parallel builds
    }
}

def call(Map config) {
    if (!config.containsKey('jobsFile')) { throw new Exception("Method 'triggerJobsFromFile' must contain param 'jobsFile'!") }
    if (!config.containsKey('parameters')) {config.parameters = []}
    call(config.jobsFile, config.parameters)
}
