#!/bin/bash
set -euo pipefail
IFS=$'\n\t'

checkRequirements() {
  echo 'Requirements'
  checkSsh
  checkGit
  checkMaven
  checkGitHubCLI

  checkGitState
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

checkGitState() {
  git fetch

  BRANCH=$(git rev-parse --abbrev-ref HEAD)
  if [[ "$BRANCH" != "main" && ! "$BRANCH" =~ ^main_v.+$ ]]; then
    echo
    echo "    Git State: failed [expected 'main' or 'main_v*' branch]"
    echo "        You are currently on branch '$BRANCH'."
    confirmContinue
  fi
  REMOTE=$(git config --get "branch.$BRANCH.remote")
  if ! REMOTE_HEAD=$(git rev-parse "$REMOTE/$BRANCH" 2>/dev/null); then
    echo
    echo "    Git State: failed [remote branch '$REMOTE/$BRANCH' does not exist]"
    echo "        You are currently on branch '$BRANCH'."
    confirmContinue
    return
  fi

  LOCAL_HEAD=$(git rev-parse HEAD)
  if [[ "$LOCAL_HEAD" != "$REMOTE_HEAD" ]]; then
    echo
    echo "    Git State: failed [local branch is not at the latest remote commit]"
    echo "        You are currently on '$LOCAL_HEAD' and the remote branch is on '$REMOTE_HEAD'."
    confirmContinue
  fi

  echo "    Git State: ok"
  echo "        You are currently on branch '$BRANCH'."
}

confirmContinue() {
  echo
  read -rp "        Do you really want to continue? [y/N] " answer

  case "$answer" in
    [yY]|[yY][eE][sS])
      ;;
    *)
      echo "        Aborted."
      exit 1
      ;;
  esac
}
