#!/bin/bash
set -euo pipefail
IFS=$'\n\t'

deletePreviousLines() {
  for (( i=0; i<$1; ++i)); do
    echo -en '\033[1A\033[2K'
  done
}

prompt() {
  local opt_question="$1"
  local promptText="$2"
  local resultVar="$3"
  local opt_defaultValue="$4"
  local opt_errorMessage="$5"

  echo
  if [ -n "$opt_question" ]; then
    echo "    ? $opt_question"
  fi
  if [ -n "$opt_defaultValue" ]; then
    echo -n "    $promptText ($opt_defaultValue): "
  else
    echo -n "    $promptText: "
  fi

  read -r input

  if [ -z "$input" ]; then
    if [ -n "$opt_errorMessage" ]; then
      echo "$opt_errorMessage"
      exit 1
    else
      input="$opt_defaultValue"
    fi
  fi

  if [ -n "$opt_question" ]; then
    deletePreviousLines 3
  else
    deletePreviousLines 2
  fi
  echo "    $promptText: $input"

  printf -v "$resultVar" '%s' "$input"
}

promptNewVersion() {
  prompt \
    "" \
    "New" \
    NEW_VERSION \
    "" \
    "You need to enter a new version!"
}

promptShouldBumpVersion() {
  prompt \
    "" \
    "Bump any module" \
    ACTION_DO_BUMP \
    "false" \
    ""
}

promptReleaseRemote() {
  prompt \
    "Enter the name (not URL) of the Git remote repository you want to create the release in" \
    "Remote repository" \
    RELEASE_REMOTE_REPOSITORY \
    "upstream" \
    ""
}

promptSetupRemote() {
  prompt \
    "Enter the name (not URL) of the Git remote repository you want to create the setup in" \
    "Remote repository" \
    SETUP_REMOTE_REPOSITORY \
    "origin" \
    ""
}

promptSetupBranch() {
  local version="$1"
  prompt \
    "Enter the name of the remote branch you want to create the setup in" \
    "Remote branch" \
    SETUP_REMOTE_BRANCH \
    "VersionBump-$version" \
    ""
}

promptReleaseTime() {
  local version="$1"
  local message="$2"
  local default="$3"
  prompt \
    "Enter the time when the release '$version' was created $message" \
    "Timestamp $version" \
    RELEASE_TIME \
    "$default" \
    ""
}
