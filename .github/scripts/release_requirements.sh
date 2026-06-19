#!/bin/bash
set -euo pipefail
IFS=$'\n\t'

checkRequirements() {
  echo 'Requirements'
  checkSsh
  checkGit
  checkMaven
  checkGitHubCLI
}

checkSsh() {
  if ! ssh-add -l >/dev/null 2>&1
  then
    echo '    SSH: failed [ssh authentication]'
    exit 1
  fi
  set +o pipefail
  if ! ssh -o BatchMode=yes -T git@github.com 2>&1 | grep -q "successfully authenticated";
  then
      echo '    SSH: failed [github authentication]'
      exit 1
  fi
  set -o pipefail
  echo '    SSH: ok'
}

checkGit() {
  if ! git --version &> /dev/null
  then
    echo '    Git: failed [no install found]'
    exit 1
  fi
  echo '    Git: ok'
}

checkMaven() {
  if ! ./mvnw --version &> /dev/null
  then
    echo '    Maven: failed [no install found]'
    exit 1
  fi
  echo '    Maven: ok'
}

checkGitHubCLI() {
  if ! gh --version &> /dev/null
  then
    echo '    GitHub CLI: failed [no install found]'
    exit 1
  else
    if ! gh auth status &> /dev/null
    then
      echo '    GitHub CLI: failed [not logged in]'
      exit 1
    else
      echo '    GitHub CLI: ok'
      return 0
    fi
  fi
}
