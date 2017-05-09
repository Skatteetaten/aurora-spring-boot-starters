def jenkinsfile
def version='v2.9.2'
fileLoader.withGit('https://git.aurora.skead.no/scm/ao/aurora-pipeline-scripts.git', version) {
   jenkinsfile = fileLoader.load('templates/bibliotek')
}
def overrides = [piTests: false, sonar: false]

jenkinsfile.run(version, overrides)