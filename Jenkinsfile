#!/usr/bin/env groovy

def jenkinsfile
def version='v3.0.0'
fileLoader.withGit('https://git.aurora.skead.no/scm/ao/aurora-pipeline-scripts.git', version) {
   jenkinsfile = fileLoader.load('templates/leveransepakke')
}

def overrides = [
    piTests: false,
    disableAllReports: true,
    credentialsId: "github_bjartek",
    deployTo: 'maven-central'
]

jenkinsfile.run(version, overrides)