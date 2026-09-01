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
  echo '    This script guides you through all release steps.'
  echo '    You can either setup a new version and/or release it on the currently checked out commit:'
  echo
  echo '    Release'
  echo '        Create all required version tags for their current pom.xml versions.'
  echo '        The created tags are than pushed to a selected remote repository.'
  echo '    Setup'
  echo '        Request a new version. This version is then set in the pom.xml.'
  echo '        The CHANGELOG.md file will be updated and the timestamp of the previous release is added.'
  echo '        Then the changes are pushed to a selected remote repository and branch.'
  echo '        Additionally a Pull Request is created for the selected remote repository and branch.'
  echo '    Bump'
  echo '        Bump the version of lazy versioned modules to the current version of BetonQuest.'
  echo '        Select either API & Library or just the Library to bump.'
  echo
  echo '    A value in (parentheses) before an input value is the default value if no input is given.'
}

selectAction() {
  printNewSection
  echo 'Action:'
  echo '    1. Release and Setup'
  echo '    2. Release'
  echo '    3. Setup'
  echo '    4. Bump'
  echo
  echo '    ? Select the action you want to execute'
  echo -n '    Selection: '

  read -r ACTION
  case "$ACTION" in
    1|'Release and Setup'|'RS')
        ACTION_TYPE='Release and Setup'
        ACTION_DO_RELEASE=true
        ACTION_DO_SETUP=true
        ACTION_DO_BUMP=false
        ;;
    2|'Release'|'R')
        ACTION_TYPE='Release'
        ACTION_DO_RELEASE=true
        ACTION_DO_SETUP=false
        ACTION_DO_BUMP=false
        ;;
    3|'Setup'|'S')
        ACTION_TYPE='Setup'
        ACTION_DO_RELEASE=false
        ACTION_DO_SETUP=true
        ACTION_DO_BUMP=false
        ;;
    4|'Bump'|'B')
        ACTION_TYPE='Bump'
        ACTION_DO_RELEASE=false
        ACTION_DO_SETUP=false
        ACTION_DO_BUMP=true
        ;;
    *)  printNewSection
        echo 'You need to select a valid action!'
        exit 1
        ;;
  esac
  deletePreviousLines 8
  echo "Action: $ACTION_TYPE"
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

version() {
  printNewSection
  echo 'Version:'
  versionCurrent
  releasePrepareModule "api"
  releasePrepareModule "lib"
  if [ "$ACTION_DO_SETUP" = true ]; then
    promptNewVersion
    promptShouldBumpVersion
    if [ "$ACTION_DO_BUMP" = true ]; then
      ACTION_TYPE="$ACTION_TYPE with Bump"
    fi
  fi
}

versionCurrent() {
  CURRENT_VERSION="$(./mvnw -B --raw-streams help:evaluate -Dexpression=revision -q -DforceStdout)"
  if [ "$(git tag -l "v$CURRENT_VERSION")" ]; then
    echo "    betonquest: $CURRENT_VERSION (already released)"
  else
    echo "    betonquest: $CURRENT_VERSION"
  fi
}

releasePrepare() {
  printNewSection
  echo 'Release:'
  promptReleaseRemote
}

releasePrepareModule() {
  local module="$1"
  CURRENT_MODULE_VERSION="$(./mvnw -B --raw-streams help:evaluate -Dexpression=revision -q -DforceStdout --projects ":$module")"
  if [ "$(git tag -l "v$CURRENT_MODULE_VERSION-$module")" ]; then
    echo "    $module: $CURRENT_MODULE_VERSION (already released)"
    printf -v "CURRENT_MODULE_VERSION_$module" '%s' ""
  else
    echo "    $module: $CURRENT_MODULE_VERSION"
    printf -v "CURRENT_MODULE_VERSION_$module" '%s' "v$CURRENT_MODULE_VERSION-$module"
  fi
}

releasePublish() {
  echo 'Release'
  checkReleaseTagsFor . "v$CURRENT_VERSION*"
  checkReleaseTagsFor docs/_tutorials "v$CURRENT_VERSION"
  checkDeploymentMaven
  checkDeploymentGhPages

  echo '    Creating version tag for betonquest...'
  git tag "v$CURRENT_VERSION" HEAD 2>&1 > /dev/null | sed 's/^/        /'
  TAGS_TO_PUSH=("v$CURRENT_VERSION")

  if [ -n "$CURRENT_MODULE_VERSION_api" ]; then
    echo '    Creating version tag for api...'
    git tag "$CURRENT_MODULE_VERSION_api" HEAD 2>&1 > /dev/null | sed 's/^/        /'
    TAGS_TO_PUSH+=("$CURRENT_MODULE_VERSION_api")
  fi

  if [ -n "$CURRENT_MODULE_VERSION_lib" ]; then
    echo '    Creating version tag for lib...'
    git tag "$CURRENT_MODULE_VERSION_lib" HEAD 2>&1 > /dev/null | sed 's/^/        /'
    TAGS_TO_PUSH+=("$CURRENT_MODULE_VERSION_lib")
  fi

  echo '    Pushing version tags...'
  git push "$RELEASE_REMOTE_REPOSITORY" "${TAGS_TO_PUSH[@]}" 2>&1 > /dev/null | sed 's/^/        /'

  echo '    DONE'
}

checkDeploymentMaven() {
  api="https://repo.betonquest.org/api/pommapper/id/BetonQuest?snapshots=false"

  local response
  if ! response="$(curl --silent --show-error --fail "$api")"; then
    echo '    Failed to query the Maven repository API!'
    exit 1
  fi

  if echo "$response" | grep -q "\"group\":\"$CURRENT_VERSION\""; then
    echo "    Version $CURRENT_VERSION is already deployed to the Maven repository!"
    exit 1
  fi
}

checkDeploymentGhPages() {
  local major="${CURRENT_VERSION%.*}"
  local url="https://raw.githubusercontent.com/BetonQuest/BetonQuest/refs/heads/gh-pages/$major/Documentation/CHANGELOG/index.html"

  local response
  if ! response="$(curl --silent --fail "$url" 2>/dev/null)"; then
    return
  fi

  if echo "$response" | grep -Fq "$CURRENT_VERSION" &&
     ! echo "$response" | grep -Fq "${CURRENT_VERSION}-DEV"; then
    echo "    Version $CURRENT_VERSION is already deployed to the documentation!"
    exit 1
  fi
}


checkReleaseTagsFor() {
  local repo=$1
  local pattern=$2
  if [[ -n $(git -C "$repo" tag -l "$pattern") ]]; then
    echo "Found matching tags that already exist in $repo"
    git -C "$repo" tag -l "$pattern" | sed 's/^/        /'
    exit 1
  fi
}

setupPrepare() {
  printNewSection
  echo 'Setup:'
  promptSetupRemote
  promptSetupBranch "$NEW_VERSION"
  setupPrepareSelectTimeDefaultValue
  promptReleaseTime "$CURRENT_VERSION" "$DEFAULT_RELEASE_TIME"
}

setupPrepareSelectTimeDefaultValue() {
  DEFAULT_RELEASE_TIME=''
  set +e
  GH_RELEASE_DATE="$(gh release view "v$CURRENT_VERSION" --repo "BetonQuest/BetonQuest" --json publishedAt >&1 2> /dev/null)"
  set -e
  GH_RELEASE_KEY="${GH_RELEASE_DATE:0:16}"
  if [ "$GH_RELEASE_KEY" == '{\"publishedAt\":\"' ]; then
    DEFAULT_RELEASE_TIME=${GH_RELEASE_DATE:16:10}
  fi
}

setupCommit() {
  echo 'Setup'

  echo '    Updating BetonQuest pom.xml file...'
  ./mvnw -B versions:set-property -DgenerateBackupPoms=false -Dproperty=revision -DnewVersion="$NEW_VERSION" --projects -:lib,-:api 2>&1 > /dev/null | sed 's/^/        /'

  for module in $(./mvnw -B -DforceStdout help:evaluate -Dexpression=project.modules | sed -n 's:.*<string>\(.*\)</string>.*:\1:p'); do
    module="${module#code/}"
    case "$module" in
      api|lib|"") continue ;;
    esac
    find . -name "pom.xml" -type f -exec sed -i \
      "s|<betonquest\.${module}\.version>[^<]*</betonquest\.${module}\.version>|<betonquest.${module}.version>${NEW_VERSION}\${changelist}</betonquest.${module}.version>|g" {} +
  done

  echo '    Updating CHANGELOG.md file...'
  NEW_CHANGELOG="## \[Unreleased\] - \${maven.build.timestamp}\n### Added\n### Changed\n### Deprecated\n### Removed\n### Fixed\n### Security\n"
  sed -i "s~## \[Unreleased\] - \${maven\.build\.timestamp}~$NEW_CHANGELOG\n## \[$CURRENT_VERSION\] - $RELEASE_TIME~g" CHANGELOG.md 2>&1 > /dev/null | sed 's/^/        /'

  setupRoadmap

  CURRENT_MAJOR="${CURRENT_VERSION%%.*}"
  NEW_MAJOR="${NEW_VERSION%%.*}"
  if [ "$CURRENT_MAJOR" != "$NEW_MAJOR" ]; then
    echo '    Updating SECURITY.md file...'
    sed -i 's/:white_check_mark:/:x:               /g' SECURITY.md
    printf -v PADDED_MAJOR "%-7s" "$NEW_MAJOR"
    NEW_SECURITY="|---------|--------------------|\n| $PADDED_MAJOR | :white_check_mark: |"
    sed -i "s~|---------|--------------------|~$NEW_SECURITY~g" SECURITY.md 2>&1 > /dev/null | sed 's/^/        /'
  fi

  echo '    Committing changed files...'
  git -c core.safecrlf=false commit --all --message="Bump version of BetonQuest to $NEW_VERSION" 2>&1 > /dev/null | sed 's/^/        /'
}

setupRoadmap() {
  CURRENT_VERSION_SHORT="${CURRENT_VERSION%.*}"

  CURRENT_ROADMAP_VERSION="$(jq -r '.meta.history.start' docs/_roadmap/roadmap.json).0"

  if [[ "$NEW_VERSION" == "$CURRENT_ROADMAP_VERSION" ]]; then
    echo '    No changes to Roadmap needed. Skipping...'
    return
  fi

  CHANGELOG_LINK="Documentation/CHANGELOG/#${CURRENT_VERSION//./}-${RELEASE_TIME}"

  echo '    Updating roadmap.json file...'
  jq --arg version "$CURRENT_VERSION_SHORT" \
     '(.meta.history.start) = $version' docs/_roadmap/roadmap.json > map-tmp.json && mv map-tmp.json docs/_roadmap/roadmap.json

  echo '    Updating info-text.json file...'
  jq --arg link "$CHANGELOG_LINK" \
     --arg version "$CURRENT_VERSION_SHORT" \
     '(.info[] | select(.id == $version) | .tiles.center.link) = $link' docs/_roadmap/info-text.json > text-tmp.json && mv text-tmp.json docs/_roadmap/info-text.json
}

bumpCommit() {
  echo 'Bump'

  echo "    Updating pom.xml files for mudules $BUMP_MODULES..."
  FORMATTED_BUMP_MODULES=":${BUMP_MODULES//,/,:}"
  ./mvnw -B versions:set-property -DgenerateBackupPoms=false -Dproperty=revision -DnewVersion="$NEW_VERSION" --projects "$FORMATTED_BUMP_MODULES" 2>&1 > /dev/null | sed 's/^/        /'

  for module in $(echo "$BUMP_MODULES" | tr ',' '\t'); do
    find . -name "pom.xml" -type f -exec sed -i \
      "s|<betonquest\.${module}\.version>[^<]*</betonquest\.${module}\.version>|<betonquest.${module}.version>${NEW_VERSION}\${changelist}</betonquest.${module}.version>|g" {} +
  done

  if [ ! "$(git status --porcelain)" ]; then
    echo 'No version to bump to was found!'
    exit 1
  fi

  if grep -q "^## \[$CURRENT_VERSION\] -" API-CHANGELOG.md; then
    echo "        API-CHANGELOG.md already contains an entry for $CURRENT_VERSION. Skipping..."
  else
    echo '    Updating API-CHANGELOG.md file...'
    NEW_CHANGELOG="## \[Unreleased\] - \${maven.build.timestamp}\n### API\n#### Added\n#### Changed\n#### Deprecated\n#### Removed\n### Library\n#### Added\n#### Changed\n#### Deprecated\n#### Removed\n"
    sed -i "s~## \[Unreleased\] - \${maven\.build\.timestamp}~$NEW_CHANGELOG\n## \[$CURRENT_VERSION\] - $RELEASE_TIME~g" API-CHANGELOG.md 2>&1 > /dev/null | sed 's/^/        /'
  fi

  echo '    Committing changed files...'
  git -c core.safecrlf=false commit --all --message="Bump version of $BUMP_MODULES to $NEW_VERSION" 2>&1 > /dev/null | sed 's/^/        /'
}

finalizeAndPublish() {
  echo 'Finalize'
  echo '    Pushing committed changes...'
  git push "$SETUP_REMOTE_REPOSITORY" "HEAD:$SETUP_REMOTE_BRANCH" 2>&1 > /dev/null | sed 's/^/        /'

  echo '    Creating Pull Request...'
  setupPublishCreatePullRequest

  echo '    Resetting current branch...'
  git reset --hard "$PREVIOUS_HEAD" 2>&1 > /dev/null | sed 's/^/        /'

  echo '    DONE'
}

setupPublishCreatePullRequest() {
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
  echo "    The changes are already in your repository $SETUP_REMOTE_REPOSITORY in branch $SETUP_REMOTE_BRANCH"
}

setupPublishCreatePullRequestSlug() {
  SETUP_REMOTE_SLUG="$(git config --get "remote.${SETUP_REMOTE_REPOSITORY}.url" | grep -o '[^:/]\+/[^/.]\+\.git$' | grep -o '^[^/]\+')"
  if [ -z "$SETUP_REMOTE_SLUG" ]; then
    printNewSection
    echo 'No GitHub remote slug could be extracted from remotes!'
    exit 1
  fi
}

bumpPrepare() {
  printNewSection
  echo 'Bump:'
  echo '    1. api & lib'
  echo '    2. api'
  echo '    3. lib'
  echo
  echo '    ? Select the module(s) you want to bump'
  echo -n '    Selection: '

  read -r ACTION
  case "$ACTION" in
    1|'api & lib')
        BUMP_MODULES='api,lib'
        ;;
    2|'api')
        BUMP_MODULES='api'
        ;;
    3|'lib')
        BUMP_MODULES='lib'
        ;;
    *)  printNewSection
        echo 'You need to select a valid module to bump!'
        exit 1
        ;;
  esac
  deletePreviousLines 6
  echo "    Modules: $BUMP_MODULES"

  if [ -z ${NEW_VERSION+x} ]; then
    NEW_VERSION="$CURRENT_VERSION"
    promptSetupRemote
    promptSetupBranch "$NEW_VERSION"
  fi
}

#
# Start of script
#

goToRootDirectory

source "./.github/scripts/release_requirements.sh"
source "./.github/scripts/release_prompts.sh"

PREVIOUS_HEAD=$(git rev-parse HEAD)
checkRequirements
printHelp
selectAction

version
if [ "$ACTION_DO_RELEASE" = true ]; then
  releasePrepare
fi
if [ "$ACTION_DO_SETUP" = true ]; then
  setupPrepare
fi
if [ "$ACTION_DO_BUMP" = true ]; then
  bumpPrepare
fi

printNewSection
echo "Create $ACTION_TYPE..."
if [ "$ACTION_DO_RELEASE" = true ]; then
  releasePublish
fi
if [ "$ACTION_DO_SETUP" = true ]; then
  setupCommit
fi
if [ "$ACTION_DO_BUMP" = true ]; then
  bumpCommit
fi

if [ "$ACTION_DO_SETUP" = true ] ||  [ "$ACTION_DO_BUMP" = true ]; then
  finalizeAndPublish
fi

printNewSection
echo "Script run was successful and a $ACTION_TYPE is now in progress on GitHub"
printNewSection
