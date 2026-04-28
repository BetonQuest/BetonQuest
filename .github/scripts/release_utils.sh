#!/bin/bash
set -euo pipefail
IFS=$'\n\t'

printNewSection() {
  echo
  echo
}

checkRequirements() {
  checkGit
  checkMaven
  checkGitHubCLI
}

checkGit() {
  if ! git --version &> /dev/null
  then
    printNewSection
    echo 'Git is not installed or it is not added to the path!'
    exit 1
  fi
}

checkMaven() {
  if ! ./mvnw --version &> /dev/null
  then
    printNewSection
    echo 'Maven is not installed or it is not added to the path!'
    exit 1
  fi
}

checkGitHubCLI() {
  if ! gh --version &> /dev/null
  then
    GH_CLI_SUPPORT=false
    printNewSection
    echo 'GitHub CLI is either not installed or not added to the path.'
  else
    if ! gh auth status &> /dev/null
    then
      GH_CLI_SUPPORT=false
      printNewSection
      echo 'GitHub CLI is installed but you are not logged in.'
    else
      GH_CLI_SUPPORT=true
      return 0
    fi
  fi
  echo 'Some features of this script will not work:'
  echo '  - The date of the last release cannot be resolved automatically'
  echo '  - The Pull Request cannot be created automatically'
}
