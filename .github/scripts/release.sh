#!/bin/bash
set -euo pipefail
IFS=$'\n\t'

printNewSection() {
  echo ""
  echo ""
}

printHelp() {
  printNewSection
  echo "HELP"
  echo "    This script can do the following things based on your commit that you checked out,"
  echo "    and it will lead you through all steps:"
  echo "    "
  echo "    Release"
  echo "        Create a version tag with the current corresponding pom.xml version."
  echo "        This tag is than pushed to a selected remote repository."
  echo "    Setup"
  echo "        Request a new version. This version is than updated in the pom.xml file."
  echo "        The CHANGELOG.md file will be updated and the timestamp of the previous release is added."
  echo "        Than the changes are pushed to a selected remote repository and branch."
  echo "        Than a PullRequest is created for the selected remote repository and branch."
  echo "    "
  echo "    A value in brackets before a input value indicates a default value if no input was given."
}

deletePreviousLines() {
  for (( i=0; i<$1; ++i)); do
    echo -en "\033[1A\033[2K"
  done
}

goToRootDirectory() {
  CURRENT_PATH=$(pwd)
  if [[ "${CURRENT_PATH}" == */.github/scripts ]]; then
    cd ../../
  fi
  if [[ "${CURRENT_PATH}" == */.github ]]; then
    cd ../
  fi
}

checkMaven() {
  if ! mvn --version &> /dev/null
  then
    printNewSection
    echo "Maven is not installed or it is not added to the path!"
    exit 1
  fi
}

checkGitHubCLI() {
  if ! gh --version &> /dev/null
  then
    GH_CLI_SUPPORT=false
    printNewSection
    echo "GitHub CLI is either not installed or not added to the path."
    echo "Some features of this script will not work:"
    echo "  - The date of the last release cannot be resolved automatically"
    echo "  - The Pull Request cannot be created automatically"
  else
    GH_CLI_SUPPORT=true
  fi
}

selectAction() {
  printNewSection
  echo "Action:"
  echo "    1. Release and Setup"
  echo "    2. Release"
  echo "    3. Setup"
  echo "    "
  echo "    ? Enter the number of the action you want to execute"
  echo -n "    Selection: "

  read -r ACTION
  if [ "${ACTION}" == 1 ]; then
    ACTION_TYPE="Release and Setup"
  fi
  if [ "${ACTION}" == 2 ]; then
    ACTION_TYPE="Release"
  fi
  if [ "${ACTION}" == 3 ]; then
    ACTION_TYPE="Setup"
  fi
  if [ -z "${ACTION}" ] ||  [ -z "${ACTION_TYPE}" ]; then
    printNewSection
    echo "You need to select a valid action!"
    exit 1
  fi
  deletePreviousLines 7
  echo "Action: ${ACTION_TYPE}"
}

version() {
  printNewSection
  echo "Version:"
  versionCurrent
  if [ "${ACTION}" == 1 ] || [ "${ACTION}" == 3 ]; then
    versionNew
  fi
}

versionCurrent() {
  CURRENT_VERSION="$(mvn help:evaluate -Dexpression=version -q -DforceStdout)"
  echo "    Current: ${CURRENT_VERSION}"
}

versionNew() {
  echo -n "    New: "
  read -r NEW_VERSION
  if [ -z "${NEW_VERSION}" ]; then
    printNewSection
    echo "You need to enter a new version!"
    exit 1
  fi
}

releasePrepare() {
  printNewSection
  echo "Release:"
  releaseSelectRemote
}

releaseSelectRemote() {
  echo ""
  echo "    ? Enter the name (not URL) of the remote repository you want to create the release in"
  echo -n "    Remote repository (upstream): "
  read -r RELEASE_REMOTE_REPOSITORY
  if [ -z "${RELEASE_REMOTE_REPOSITORY}" ]; then
    RELEASE_REMOTE_REPOSITORY="upstream"
  fi
  deletePreviousLines 3
  echo "    Remote repository: ${RELEASE_REMOTE_REPOSITORY}"
}

releasePublish() {
  echo "Release"

  echo "    Create version tag..."
  git tag v"${CURRENT_VERSION}" HEAD 2>&1 > /dev/null | sed 's/^/        /'

  echo "    Push version tag..."
  git push "${RELEASE_REMOTE_REPOSITORY}" v"${CURRENT_VERSION}" 2>&1 > /dev/null | sed 's/^/        /'

  echo "    DONE"
}

setupPrepare() {
  printNewSection
  echo "Setup:"
  setupSelectRemote
  setupSelectBranch
  setupSelectTime
}

setupSelectRemote() {
  echo ""
  echo "    ? Enter the name (not URL) of the remote repository you want to create the setup in"
  echo -n "    Remote repository (origin): "
  read -r SETUP_REMOTE_REPOSITORY
  if [ -z "${SETUP_REMOTE_REPOSITORY}" ]; then
    SETUP_REMOTE_REPOSITORY="origin"
  fi
  deletePreviousLines 3
  echo "    Remote repository: ${SETUP_REMOTE_REPOSITORY}"
}

setupSelectBranch() {
  echo ""
  echo "    ? Enter the name of the remote branch you want to create the setup in"
  DEFAULT_BRANCH="VersionBump-${NEW_VERSION}"
  echo -n "    Remote branch (${DEFAULT_BRANCH}): "
  read -r SETUP_REMOTE_BRANCH
  if [ -z "${SETUP_REMOTE_BRANCH}" ]; then
    SETUP_REMOTE_BRANCH="${DEFAULT_BRANCH}"
  fi
  deletePreviousLines 3
  echo "    Remote branch: ${SETUP_REMOTE_BRANCH}"
}

setupSelectTime() {
  setupSelectTimeDefaultValue
  echo ""
  echo "    ? Enter the time when the release '${CURRENT_VERSION}' was created ${DEFAULT_TIME_MESSAGE}"
  echo -n "    Timestamp ${CURRENT_VERSION} (${DEFAULT_TIME}): "
  read -r SETUP_TIME
  if [ -z "${SETUP_TIME}" ]; then
    SETUP_TIME="${DEFAULT_TIME}"
  fi
  deletePreviousLines 3
  echo "    Timestamp ${CURRENT_VERSION}: ${SETUP_TIME}"
}

setupSelectTimeDefaultValue() {
  DEFAULT_TIME=$(date +%Y-%m-%d)
  DEFAULT_TIME_MESSAGE=""
  if [ "${GH_CLI_SUPPORT}" ]; then
    set +e
    GH_RELEASE_DATE="$(gh release view "v${CURRENT_VERSION}" --json publishedAt >&1 2> /dev/null)"
    set -e
    GH_RELEASE_KEY=${GH_RELEASE_DATE:0:16}
    if [ "${GH_RELEASE_KEY}" == "{\"publishedAt\":\"" ]; then
      DEFAULT_TIME=${GH_RELEASE_DATE:16:10}
      DEFAULT_TIME_MESSAGE="(the default time was extracted from the release tag)"
    fi
  fi
}

setupPublish() {
  echo "Setup"

  echo "    Update pom.xml file..."
  mvn versions:set-property -DgenerateBackupPoms=false -Dproperty=version -DnewVersion="${NEW_VERSION}" 2>&1 > /dev/null | sed 's/^/        /'

  echo "    Update CHANGELOG.md file..."
  NEW_CHANGELOG="## \[Unreleased\] - \${current-date}\n### Added\n### Changed\n### Deprecated\n### Removed\n### Fixed\n### Security\n"
  sed -i "s~## \[Unreleased\] - \${current-date}~${NEW_CHANGELOG}\n## \[${CURRENT_VERSION}\] - ${SETUP_TIME}~g" CHANGELOG.md 2>&1 > /dev/null | sed 's/^/        /'

  echo "    Commit changed files..."
  git commit --all --message="Version bump to ${NEW_VERSION}" 2>&1 > /dev/null | sed 's/^/        /'

  echo "    Push committed changes..."
  git push "${SETUP_REMOTE_REPOSITORY}" "HEAD:${SETUP_REMOTE_BRANCH}" 2>&1 > /dev/null | sed 's/^/        /'

  echo "    Create Pull Request..."
  setupPublishCreatePullRequest

  echo "    Reset current branch..."
  git reset --hard "$(git rev-parse --abbrev-ref --symbolic-full-name @\{upstream\})" 2>&1 > /dev/null | sed 's/^/        /'

  echo "    DONE"
}

setupPublishCreatePullRequest() {
  if [ $GH_CLI_SUPPORT ]; then
    CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)
    if [ "${CURRENT_BRANCH}" != "HEAD" ]; then
      setupPublishCreatePullRequestSlug
      gh pr create \
        --assignee "@me" \
        --title "Version bump to ${NEW_VERSION}" \
        --body "This is an automatically created PR from the release script" \
        --base "${CURRENT_BRANCH}" \
        --head "${SETUP_REMOTE_SLUG}:${SETUP_REMOTE_BRANCH}" \
        --repo "BetonQuest/BetonQuest" \
        2>&1 > /dev/null | sed 's/^/        /'
        return
    else
      echo "    ! Looks like no branch is checked out. Create the pull request manually!"
    fi
  else
    echo "    ! You do not have 'GitHub CLI' installed. Create the pull request manually!"
  fi
  echo "    The changes are already in your repository ${SETUP_REMOTE_REPOSITORY} in branch ${SETUP_REMOTE_BRANCH}"
}

setupPublishCreatePullRequestSlug() {
  SETUP_REMOTE_SLUG_RAW="$(git config --get remote.${SETUP_REMOTE_REPOSITORY}.url)"
  if [ "${SETUP_REMOTE_SLUG_RAW:0:15}" == "git@github.com:" ]; then
    SETUP_REMOTE_SLUG=${SETUP_REMOTE_SLUG_RAW:15:${#SETUP_REMOTE_SLUG_RAW}-30}
  fi
  if [ "${SETUP_REMOTE_SLUG_RAW:0:19}" == "https://github.com/" ]; then
    SETUP_REMOTE_SLUG=${SETUP_REMOTE_SLUG_RAW:19:${#SETUP_REMOTE_SLUG_RAW}-30}
  fi
  if [ -z "${SETUP_REMOTE_SLUG}" ]; then
    printNewSection
    echo "No GitHub remote slug could be extracted from remotes!"
    exit 1
  fi
}

#
# Start of script
#

printHelp
goToRootDirectory
checkMaven
checkGitHubCLI
selectAction

version
if [ "${ACTION}" == 1 ] || [ "${ACTION}" == 2 ]; then
  releasePrepare
fi
if [ "${ACTION}" == 1 ] || [ "${ACTION}" == 3 ]; then
  setupPrepare
fi

printNewSection
echo "Create ${ACTION_TYPE}..."
if [ "${ACTION}" == 1 ] || [ "${ACTION}" == 2 ]; then
  releasePublish
fi
if [ "${ACTION}" == 1 ] || [ "${ACTION}" == 3 ]; then
  setupPublish
fi

printNewSection
echo "Script run was successful and a ${ACTION_TYPE} is now in progress on GitHub"
printNewSection
