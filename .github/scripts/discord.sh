#!/bin/bash
# This script was created by using the this source: https://github.com/DiscordHooks/github-actions-discord-webhook
set -euo pipefail
IFS=$'\n\t'

if [ -z "$WEBHOOK_URL" ]; then
  echo "[Webhook]: WARNING! You need to pass the WEBHOOK_URL environment variable."
  exit 1
fi
if [ -z "$DOCS_URL" ]; then
  echo "[Webhook]: WARNING! You need to pass the DOCS_URL environment variable."
  exit 1
fi

echo "[Webhook]: Preparing resources...";
AVATAR="https://github.com/actions.png"
BETONQUEST_URL="https://avatars1.githubusercontent.com/u/62897788?s=200&v=4"
COMMIT_ICON_SUCCESS="https://github.githubassets.com/images/icons/emoji/unicode/1f527.png"
COMMIT_ICON_FAILURE="https://github.githubassets.com/images/icons/emoji/unicode/1f6a8.png"
COMMIT_ICON_RELEASE="https://github.githubassets.com/images/icons/emoji/unicode/2714.png"

echo "[Webhook]: Preparing git related variables...";
AUTHOR_NAME="$(git log -1 "$GITHUB_SHA" --pretty="%aN")"
COMMITTER_NAME="$(git log -1 "$GITHUB_SHA" --pretty="%cN")"
COMMIT_SUBJECT="$(git log -1 "$GITHUB_SHA" --pretty="%s")"
COMMIT_MESSAGE="$(git log -1 "$GITHUB_SHA" --pretty="%b")"
#Replace newlines with literal \n
COMMIT_MESSAGE="$(echo "$COMMIT_MESSAGE" | sed -E ':a;N;$!ba;s/\r?\n|\n?\r/\\n/g;')"
#Replace double quotes with literal \"
COMMIT_MESSAGE="${COMMIT_MESSAGE//\"/\\\"}"
COMMIT_URL="https://github.com/$GITHUB_REPOSITORY/commit/$GITHUB_SHA"
BRANCH_NAME="${GITHUB_REF#*/*/}"
REPO_URL="https://github.com/$GITHUB_REPOSITORY"
BRANCH_URL="$REPO_URL/tree/$BRANCH_NAME"
ACTION_URL="$COMMIT_URL/checks"
if [ "$AUTHOR_NAME" == "$COMMITTER_NAME" ]; then
  CREDITS="$AUTHOR_NAME authored & committed"
else
  CREDITS="$AUTHOR_NAME authored & $COMMITTER_NAME committed"
fi
LINEBREAK=""
if [ -n "$COMMIT_MESSAGE" ]; then
  LINEBREAK="\n"
fi
COMMIT_MESSAGE="${COMMIT_MESSAGE//$\\n/ }"
ADDITIONAL_INFORMATION="$LINEBREAK$COMMIT_MESSAGE$LINEBREAK\\n$CREDITS"

echo "[Webhook]: Preparing type related variables...";
case "$VERSION_TYPE" in
  "release" )
    RELEASE_NAME="Release-Build"
    RELEASE_DOWNLOAD_URL="${DOCS_URL}RELEASE/Downloads/"
    RELEASE_COMMIT_ICON_SUCCESS="$COMMIT_ICON_RELEASE"
    IFS='.' read -r major minor _ <<< "$VERSION"
    DOCS_VERSION="$major.$minor"
    ;;

  "development" )
    RELEASE_NAME="Dev-Build"
    RELEASE_DOWNLOAD_URL="${DOCS_URL}DEV/Downloads/"
    RELEASE_COMMIT_ICON_SUCCESS="$COMMIT_ICON_SUCCESS"
    IFS='.' read -r major minor _ <<< "$VERSION"
    DOCS_VERSION="$major.$minor-DEV"
    ;;

  * )
    echo "[Webhook]: WARNING! You need to pass the VERSION_TYPE environment variable."
    exit 1
    ;;
esac

echo "[Webhook]: Preparing job state related variables...";
case "$JOB_STATUS" in
  "success" )
    EMBED_COLOR=3066993
    DEV_BUILD_DOWNLOAD="Click to Download $VERSION!"
    STATUS_MESSAGE="$RELEASE_NAME is now available"
    BUILD_DOWNLOAD_URL="${DOCS_URL}${DOCS_VERSION}/Downloads/?path=$UPLOAD_PATH&filename=BetonQuest.jar"
    DESCRIPTION="${RELEASE_NAME}s available [HERE](${RELEASE_DOWNLOAD_URL}). Report bugs [HERE](https://github.com/BetonQuest/BetonQuest/issues)"
    COMMIT_ICON="$RELEASE_COMMIT_ICON_SUCCESS"
    ;;

  "failure"|"cancelled" )
    if [[ $VERSION_IS_NEW == true ]]; then VERSION_HINT=$VERSION; else VERSION_HINT="the Docs"; fi
    EMBED_COLOR=15158332
    STATUS_MESSAGE="There was an error building a $RELEASE_NAME!"
    DEV_BUILD_DOWNLOAD="Inspect the failure on $VERSION_HINT!"
    BUILD_DOWNLOAD_URL="$COMMIT_URL"
    DESCRIPTION=""
    COMMIT_ICON="$COMMIT_ICON_FAILURE"
    ;;

  * )
    echo "[Webhook]: WARNING! You need to pass the JOB_STATUS environment variable."
    exit 1
    ;;
esac

echo "[Webhook]: Create the webhook data...";
TIMESTAMP="$(date -u +%FT%TZ)"
WEBHOOK_DATA='{
  "username": "GitHub Actions",
  "avatar_url": "'$AVATAR'",
  "embeds": [ {
    "color": '$EMBED_COLOR',
    "author": {
      "name": "'"$STATUS_MESSAGE"'",
      "url": "'$ACTION_URL'",
      "icon_url": "'$COMMIT_ICON'" 
    },
    "title": "'"${DEV_BUILD_DOWNLOAD}"'",
    "url": "'"$BUILD_DOWNLOAD_URL"'",
    "description": "'"$DESCRIPTION"'\n\n'"[__**${COMMIT_SUBJECT}**__](${COMMIT_URL})\\n${ADDITIONAL_INFORMATION}"'",
    "fields": [
      {
        "name": "Commit",
        "value": "'"[\`${GITHUB_SHA:0:7}\`](${COMMIT_URL})"'",
        "inline": true
      },
      {
        "name": "Branch",
        "value": "'"[\`${BRANCH_NAME}\`](${BRANCH_URL})"'",
        "inline": true
      }
    ],
    "thumbnail": {
      "url": "'"$BETONQUEST_URL"'"
    },
    "timestamp": "'"$TIMESTAMP"'"
  } ]
}'

echo "[Webhook]: Sending webhook to Discord...";
echo "$WEBHOOK_DATA"
curl --fail-with-body --no-progress-meter -A "GitHub-Actions-Webhook" -H Content-Type:application/json -d "$WEBHOOK_DATA" "$WEBHOOK_URL"
