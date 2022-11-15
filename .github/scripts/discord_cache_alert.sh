#!/bin/bash
if [ -z "$WEBHOOK_URL" ]; then
  echo -e "WARNING! You need to pass the WEBHOOK_URL environment variable. For details & guide, visit: https://github.com/DiscordHooks/github-actions-discord-webhook"
  exit 1
fi

echo -e "[Webhook]: Sending webhook to Discord...";

AVATAR="https://github.com/actions.png"
BETON_QUEST_URL="https://avatars1.githubusercontent.com/u/62897788?s=200&v=4"
COMMIT_ICON="https://github.githubassets.com/images/icons/emoji/unicode/1f6a8.png"
COMMIT_URL="https://github.com/$GITHUB_REPOSITORY/commit/$GITHUB_SHA"
ACTION_URL="$COMMIT_URL/checks"

EMBED_COLOR=15158332
STATUS_MESSAGE="Daily Dependency Check failed!"
STATUS_MESSAGE_DETAILS="Click here to inspect which dependencies couldn't be resolved."

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
    "title": "'"${STATUS_MESSAGE_DETAILS}"'",
    "url": "'"$ACTION_URL"'",
    "thumbnail": {
      "url": "'"$BETON_QUEST_URL"'"
    },
    "timestamp": "'"$TIMESTAMP"'"
  } ]
}'

(curl --fail --progress-bar -A "GitHub-Actions-Webhook" -H Content-Type:application/json -H X-Author:k3rn31p4nic#8383 -d "$WEBHOOK_DATA" "$WEBHOOK_URL" \
  && echo -e "[Webhook]: Successfully sent the webhook.") || echo -e "[Webhook]: Unable to send webhook."
