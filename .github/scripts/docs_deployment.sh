#!/bin/bash
# This script was created by using the this source: https://github.com/DiscordHooks/github-actions-discord-webhook
set -euo pipefail
IFS=$'\n\t'

git config --global user.name "BetonQuest-Bot"
git config --global user.email "contact@betonquest.org"

IFS='.' read -r major minor _ <<< "$VERSION"
TWO_DIGIT_VERSION="$major.$minor"

FLAGS=(--push --update-aliases)
VERSIONS=()
if [ "$VERSION_TYPE" = release ]; then
  VERSIONS+=("$TWO_DIGIT_VERSION" "$TWO_DIGIT_VERSION-DEV")
  if [ -n "$(git branch -r --contains tags/v${VERSION} --remote origin/main)" ]; then
    VERSIONS+=(RELEASE DEV)
  elif { mike list --json "$TWO_DIGIT_VERSION" | grep -q '"aliases" *: *\[[^]]*"RELEASE"'; }; then
    VERSIONS+=(RELEASE)
  fi
  echo "mike delete $TWO_DIGIT_VERSION-DEV"
  mike delete "$TWO_DIGIT_VERSION-DEV"
elif [ "$VERSION_TYPE" = development ]; then
  VERSIONS+=("$TWO_DIGIT_VERSION-DEV")
  if ! { mike list --json | grep -q "\"version\" *: *\"$TWO_DIGIT_VERSION\""; }; then
    VERSIONS+=("$TWO_DIGIT_VERSION")
  fi
  if [ "$GIT_REF" == 'refs/heads/main' ]; then
    VERSIONS+=(DEV);

    OLD_DEV_VERSION=$(mike list --json DEV | awk -F\" '/version/{print $4}')
    if [ "$TWO_DIGIT_VERSION-DEV" != "$OLD_DEV_VERSION" ] && [[ "$OLD_DEV_VERSION" == *"-DEV" ]]; then
      echo "mike delete $OLD_DEV_VERSION"
      mike delete "$OLD_DEV_VERSION"
      echo "mike alias ${OLD_DEV_VERSION%-DEV} $OLD_DEV_VERSION"
      mike alias ${OLD_DEV_VERSION%-DEV} $OLD_DEV_VERSION
    fi
  fi
fi

echo "mike deploy ${FLAGS[@]} ${VERSIONS[@]}"
mike deploy "${FLAGS[@]}" "${VERSIONS[@]}"
