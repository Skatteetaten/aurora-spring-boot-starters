def jenkinsfile
def version='v2.9.2'
fileLoader.withGit('https://git.sits.no/git/scm/ao/aurora-pipeline-scripts.git', version) {
   jenkinsfile = fileLoader.load('templates/bibliotek')
}
def overrides = [piTests: false]

jenkinsfile.run(version, overrides)