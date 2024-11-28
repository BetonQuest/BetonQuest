#!/bin/bash
set -euo pipefail
IFS=$'\n\t'

printNewSection() {
  echo
  echo
}

printHelp() {
  printNewSection
  echo 'HELP'
  echo '    This script guides you through all steps.'
  echo '    It can do the following based on the currently checked out commit:'
  echo
  echo '    Release'
  echo '        Create a version tag with the current pom.xml version.'
  echo '        This tag is than pushed to a selected remote repository.'
  echo '    Setup'
  echo '        Request a new version. This version is then set in the pom.xml.'
  echo '        The CHANGELOG.md file will be updated and the timestamp of the previous release is added.'
  echo '        Then the changes are pushed to a selected remote repository and branch.'
  echo '        Additionally a Pull Request is created for the selected remote repository and branch.'
  echo
  echo '    A value in (parentheses) before an input value is the default value if no input was given.'
}

deletePreviousLines() {
  for (( i=0; i<$1; ++i)); do
    echo -en '\033[1A\033[2K'
  done
}

goToRootDirectory() {
  CURRENT_PATH="$(pwd)"
  if [[ "$CURRENT_PATH" == */.github/scripts ]]; then
    cd ../../
  fi
  if [[ "$CURRENT_PATH" == */.github ]]; then
    cd ../
  fi
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
    echo 'Some features of this script will not work:'
    echo '  - The date of the last release cannot be resolved automatically'
    echo '  - The Pull Request cannot be created automatically'
  else
    if ! gh auth status &> /dev/null
    then
      GH_CLI_SUPPORT=false
      printNewSection
      echo 'GitHub CLI is installed but you are not logged in.'
      echo 'Some features of this script will not work:'
      echo '  - The date of the last release cannot be resolved automatically'
      echo '  - The Pull Request cannot be created automatically'
    else
      GH_CLI_SUPPORT=true
    fi
  fi
}

selectAction() {
  printNewSection
  echo 'Action:'
  echo '    1. Release and Setup'
  echo '    2. Release'
  echo '    3. Setup'
  echo
  echo '    ? Enter the action you want to execute'
  echo -n '    Selection: '

  read -r ACTION
  case "$ACTION" in
    1|'Release and Setup'|'RS')
        ACTION_TYPE='Release and Setup'
        ACTION_DO_RELEASE=true
        ACTION_DO_SETUP=true
        ;;
    2|'Release'|'R')
        ACTION_TYPE='Release'
        ACTION_DO_RELEASE=true
        ACTION_DO_SETUP=false
        ;;
    3|'Setup'|'S')
        ACTION_TYPE='Setup'
        ACTION_DO_RELEASE=false
        ACTION_DO_SETUP=true
        ;;
    *)  printNewSection
        echo 'You need to select a valid action!'
        exit 1
        ;;
  esac
  deletePreviousLines 7
  echo "Action: $ACTION_TYPE"
}

version() {
  printNewSection
  echo 'Version:'
  versionCurrent
  if [ "$ACTION_DO_SETUP" = true ]; then
    versionNew
  fi
}

versionCurrent() {
  CURRENT_VERSION="$(./mvnw help:evaluate -Dexpression=revision -q -DforceStdout)"
  echo "    Current: $CURRENT_VERSION"
}

versionNew() {
  echo -n '    New: '
  read -r NEW_VERSION
  if [ -z "$NEW_VERSION" ]; then
    printNewSection
    echo 'You need to enter a new version!'
    exit 1
  fi
}

releasePrepare() {
  printNewSection
  echo 'Release:'
  releaseSelectRemote
}

releaseSelectRemote() {
  echo
  echo '    ? Enter the name (not URL) of the Git remote repository you want to create the release in'
  echo -n '    Remote repository (upstream): '
  read -r RELEASE_REMOTE_REPOSITORY
  if [ -z "$RELEASE_REMOTE_REPOSITORY" ]; then
    RELEASE_REMOTE_REPOSITORY='upstream'
  fi
  deletePreviousLines 3
  echo "    Remote repository: $RELEASE_REMOTE_REPOSITORY"
}

releasePublish() {
  echo 'Release'

  echo '    Creating version tag...'
  git tag "v$CURRENT_VERSION" HEAD 2>&1 > /dev/null | sed 's/^/        /'

  echo '    Pushing version tag...'
  git push "$RELEASE_REMOTE_REPOSITORY" "v$CURRENT_VERSION" 2>&1 > /dev/null | sed 's/^/        /'

  echo '    DONE'
}

setupPrepare() {
  printNewSection
  echo 'Setup:'
  setupSelectRemote
  setupSelectBranch
  setupSelectTime
}

setupSelectRemote() {
  echo
  echo '    ? Enter the name (not URL) of the Git remote repository you want to create the setup in'
  echo -n '    Remote repository (origin): '
  read -r SETUP_REMOTE_REPOSITORY
  if [ -z "$SETUP_REMOTE_REPOSITORY" ]; then
    SETUP_REMOTE_REPOSITORY='origin'
  fi
  deletePreviousLines 3
  echo "    Remote repository: $SETUP_REMOTE_REPOSITORY"
}

setupSelectBranch() {
  echo
  echo '    ? Enter the name of the remote branch you want to create the setup in'
  DEFAULT_BRANCH="VersionBump-$NEW_VERSION"
  echo -n "    Remote branch ($DEFAULT_BRANCH): "
  read -r SETUP_REMOTE_BRANCH
  if [ -z "$SETUP_REMOTE_BRANCH" ]; then
    SETUP_REMOTE_BRANCH="$DEFAULT_BRANCH"
  fi
  deletePreviousLines 3
  echo "    Remote branch: $SETUP_REMOTE_BRANCH"
}

setupSelectTime() {
  setupSelectTimeDefaultValue
  echo
  echo "    ? Enter the time when the release '$CURRENT_VERSION' was created $DEFAULT_TIME_MESSAGE"
  echo -n "    Timestamp $CURRENT_VERSION ($DEFAULT_TIME): "
  read -r SETUP_TIME
  if [ -z "$SETUP_TIME" ]; then
    SETUP_TIME="$DEFAULT_TIME"
  fi
  deletePreviousLines 3
  echo "    Timestamp $CURRENT_VERSION: $SETUP_TIME"
}

setupSelectTimeDefaultValue() {
  DEFAULT_TIME="$(date +%Y-%m-%d)"
  DEFAULT_TIME_MESSAGE=''
  if [ "$GH_CLI_SUPPORT" ]; then
    set +e
    GH_RELEASE_DATE="$(gh release view "v$CURRENT_VERSION" --json publishedAt >&1 2> /dev/null)"
    set -e
    GH_RELEASE_KEY="${GH_RELEASE_DATE:0:16}"
    if [ "$GH_RELEASE_KEY" == '{\"publishedAt\":\"' ]; then
      DEFAULT_TIME=${GH_RELEASE_DATE:16:10}
      DEFAULT_TIME_MESSAGE='(the default time was extracted from the release tag)'
    fi
  fi
}

setupPublish() {
  echo 'Setup'

  echo '    Updating pom.xml file...'
  ./mvnw versions:set-property -DgenerateBackupPoms=false -Dproperty=revision -DnewVersion="$NEW_VERSION" 2>&1 > /dev/null | sed 's/^/        /'

  echo '    Updating CHANGELOG.md file...'
  NEW_CHANGELOG="## \[Unreleased\] - \${maven.build.timestamp}\n### Added\n### Changed\n### Deprecated\n### Removed\n### Fixed\n### Security\n"
  sed -i "s~## \[Unreleased\] - \${maven\.build\.timestamp}~$NEW_CHANGELOG\n## \[$CURRENT_VERSION\] - $SETUP_TIME~g" CHANGELOG.md 2>&1 > /dev/null | sed 's/^/        /'

  echo '    Committing changed files...'
  git commit --all --message="Version bump to $NEW_VERSION" 2>&1 > /dev/null | sed 's/^/        /'

  echo '    Pushing committed changes...'
  git push "$SETUP_REMOTE_REPOSITORY" "HEAD:$SETUP_REMOTE_BRANCH" 2>&1 > /dev/null | sed 's/^/        /'

  echo '    Creating Pull Request...'
  setupPublishCreatePullRequest

  echo '    Resetting current branch...'
  git reset --hard HEAD~1 2>&1 > /dev/null | sed 's/^/        /'

  echo '    DONE'
}

setupPublishCreatePullRequest() {
  if [ $GH_CLI_SUPPORT ]; then
    CURRENT_BRANCH="$(git rev-parse --abbrev-ref HEAD)"
    if [ "$CURRENT_BRANCH" != 'HEAD' ]; then
      setupPublishCreatePullRequestSlug
      gh pr create \
        --assignee '@me' \
        --title "Version bump to $NEW_VERSION" \
        --body "This is an automatically created PR from the release script" \
        --base "$CURRENT_BRANCH" \
        --head "$SETUP_REMOTE_SLUG:$SETUP_REMOTE_BRANCH" \
        --repo "BetonQuest/BetonQuest" \
        2>&1 > /dev/null | sed 's/^/        /'
        return
    else
      echo '    ! Looks like no branch is checked out. Create the pull request manually!'
    fi
  else
    echo '    ! You do not have '"'GitHub CLI'"' installed. Create the pull request manually!'
  fi
  echo "    The changes are already in your repository $SETUP_REMOTE_REPOSITORY in branch $SETUP_REMOTE_BRANCH"
}

setupPublishCreatePullRequestSlug() {
  SETUP_REMOTE_SLUG="$(git config --get remote.${SETUP_REMOTE_REPOSITORY}.url | grep -o '[^:/]\+/[^/.]\+\.git$' | grep -o '^[^/]\+')"
  if [ -z "$SETUP_REMOTE_SLUG" ]; then
    printNewSection
    echo 'No GitHub remote slug could be extracted from remotes!'
    exit 1
  fi
}

#
# Start of script
#

printHelp
goToRootDirectory
checkRequirements
selectAction

version
if [ "$ACTION_DO_RELEASE" = true ]; then
  releasePrepare
fi
if [ "$ACTION_DO_SETUP" = true ]; then
  setupPrepare
fi

printNewSection
echo "Create $ACTION_TYPE..."
if [ "$ACTION_DO_RELEASE" = true ]; then
  releasePublish
fi
if [ "$ACTION_DO_SETUP" = true ]; then
  setupPublish
fi

printNewSection
echo "Script run was successful and a $ACTION_TYPE is now in progress on GitHub"
printNewSection
