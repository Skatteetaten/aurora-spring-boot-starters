#!/usr/bin/env groovy

def jenkinsfile
def version='v4.0.0-rc.1'
fileLoader.withGit('https://git.aurora.skead.no/scm/ao/aurora-pipeline-scripts.git', version) {
   jenkinsfile = fileLoader.load('templates/leveransepakke')
}

def overrides = [
    piTests: false,
    disableAllReports: true,
    credentialsId: "github",
    deployTo: 'maven-central'
]

jenkinsfile.run(version, overrides)
