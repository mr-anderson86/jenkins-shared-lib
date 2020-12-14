/**
 * Simply returns the user ID of the current user who triggered the build.
 *
 * @usage example:
 *        def current_user = getBuildUser()
 *
 */

def call() {
  return currentBuild.rawBuild.getCause(Cause.UserIdCause).getUserId()
}
