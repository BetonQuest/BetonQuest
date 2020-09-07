#!/bin/bash
if [ -z "$WEBHOOK_URL" ]; then
  echo -e "WARNING! You need to pass the WEBHOOK_URL environment variable. For details & guide, visit: https://github.com/DiscordHooks/github-actions-discord-webhook"
  exit 1
fi

echo -e "[Webhook]: Sending webhook to Discord...";

AVATAR="https://github.com/actions.png"
BETON_QUEST_URL="https://avatars1.githubusercontent.com/u/62897788?s=200&v=4"
COMMIT_ICON_SUCCESS="https://github.githubassets.com/images/icons/emoji/unicode/1f527.png"
COMMIT_ICON_FAILURE="https://github.githubassets.com/images/icons/emoji/unicode/1f6a8.png"
COMMIT_ICON_RELEASE="https://github.githubassets.com/images/icons/emoji/unicode/2714.png"

AUTHOR_NAME="$(git log -1 "$GITHUB_SHA" --pretty="%aN")"
COMMITTER_NAME="$(git log -1 "$GITHUB_SHA" --pretty="%cN")"
COMMIT_SUBJECT="$(git log -1 "$GITHUB_SHA" --pretty="%s")"
COMMIT_MESSAGE="$(git log -1 "$GITHUB_SHA" --pretty="%b" | sed -E ':a;N;$!ba;s/\r{0,1}\n/\\n/g')"
COMMIT_URL="https://github.com/$GITHUB_REPOSITORY/commit/$GITHUB_SHA"

# If, for example, $GITHUB_REF = refs/heads/feature/example-branch
# Then this sed command returns: feature/example-branch
BRANCH_NAME="$(echo "$GITHUB_REF" | sed 's/^[^/]*\/[^/]*\///g')"
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
ADDITIONAL_INFORMATION="$LINEBREAK${COMMIT_MESSAGE//$\\n/ }$LINEBREAK\\n$CREDITS"

if [ "$RELEASE" = "release" ]; then
  RELEASE_NAME="RELEASE"
  RELEASE_DOWNLOAD_URL="https://github.com/BetonQuest/BetonQuest/releases"
  RELEASE_DOWNLOAD_URL_DIRECT="https://github.com/BetonQuest/BetonQuest/releases/tag/v$POM_MAVEN_VERSION/BetonQuest.jar"
  RELEASE_COMMIT_ICON_SUCCESS=$COMMIT_ICON_RELEASE
else
  RELEASE_NAME="Dev-Build"
  RELEASE_DOWNLOAD_URL="https://betonquest.org/old"
  RELEASE_DOWNLOAD_URL_DIRECT="https://betonquest.org/old/api/v1/builds/download/$VERSION/$VERSION_NUMBER/BetonQuest.jar"
  RELEASE_COMMIT_ICON_SUCCESS=$COMMIT_ICON_SUCCESS
fi
case "$JOB_STATUS" in
  "success" )
    EMBED_COLOR=3066993
    DEV_BUILD_DOWNLOAD="Click to Download $POM_MAVEN_VERSION!"
    STATUS_MESSAGE="${RELEASE_NAME} is now available"
    BUILD_DOWNLOAD_URL=$RELEASE_DOWNLOAD_URL_DIRECT
    DESCRIPTION="${RELEASE_NAME}s available [HERE](${RELEASE_DOWNLOAD_URL}). Report bugs [HERE](https://github.com/BetonQuest/BetonQuest/issues)"
    COMMIT_ICON=$RELEASE_COMMIT_ICON_SUCCESS
    ;;

  "failure"|"cancelled" )
    EMBED_COLOR=15158332
    STATUS_MESSAGE="There was an error building a $RELEASE_NAME!"
    DEV_BUILD_DOWNLOAD="Inspect the failure on $POM_MAVEN_VERSION!"
    BUILD_DOWNLOAD_URL=$COMMIT_URL
    DESCRIPTION=""
    COMMIT_ICON=$COMMIT_ICON_FAILURE
    ;;

  * )
    EMBED_COLOR=0
    STATUS_MESSAGE="Status Unknown"
    DEV_BUILD_DOWNLOAD="Inspect the build!"
    BUILD_DOWNLOAD_URL=$ACTION_URL
    DESCRIPTION=""
    COMMIT_ICON=$COMMIT_ICON_FAILURE
    ;;
esac

TIMESTAMP=$(date -u +%FT%TZ)
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
      "url": "'"$BETON_QUEST_URL"'"
    },
    "timestamp": "'"$TIMESTAMP"'"
  } ]
}'

(curl --fail --progress-bar -A "GitHub-Actions-Webhook" -H Content-Type:application/json -H X-Author:k3rn31p4nic#8383 -d "${WEBHOOK_DATA//	/ }" "$WEBHOOK_URL" \
  && echo -e "[Webhook]: Successfully sent the webhook.") || echo -e "[Webhook]: Unable to send webhook."
