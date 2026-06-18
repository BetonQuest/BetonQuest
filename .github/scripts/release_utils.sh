#!/bin/bash
set -euo pipefail
IFS=$'\n\t'

printNewSection() {
  echo
  echo
}

checkRequirements() {
  echo 'Requirements'
  findSshKey
  checkSsh
  checkGit
  checkMaven
  checkGitHubCLI
}

findSshKey() {
  keys=()
  for k in ~/.ssh/*;
  do
    if [[ -f "$k" && "${k##*/}" != *.* && "${k##*/}" != "known_hosts" ]];
    then
      keys+=("$k")
    fi
  done

  if [ ${#keys[@]} -eq 0 ];
  then
    echo "    SSH: failed [no keys found]"
    exit 1
  fi

  if [ ${#keys[@]} -eq 1 ];
  then
      SSH_KEY="${keys[0]}"
  else
    echo "    Select SSH key:"
    select SSH_KEY in "${keys[@]}";
    do
      if [[ -n "$SSH_KEY" ]];
      then
        break
      fi
    done
    deletePreviousLines 2
    deletePreviousLines "${#keys[@]}"
  fi
}

checkSsh() {
  if ssh-add "$SSH_KEY" &> /dev/null;
  then
    echo '    SSH: ok'
    return 0
  fi
  if [ -z "${SSH_AUTH_SOCK:-}" ];
  then
    eval "$(ssh-agent -s)" > /dev/null
    trap 'ssh-agent -k >/dev/null' EXIT
    if ssh-add "$SSH_KEY" &> /dev/null;
    then
        echo '    SSH: ok [local ssh-agent]'
        return 0
    fi
  fi
  echo '    SSH: failed [not authenticated]'
  exit 1
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
