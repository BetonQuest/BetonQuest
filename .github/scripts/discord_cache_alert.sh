#!/bin/bash
# This script was created by using the this source: https://github.com/DiscordHooks/github-actions-discord-webhook

if [ -z "$WEBHOOK_URL" ]; then
  echo "[Webhook]: WARNING! You need to pass the WEBHOOK_URL environment variable."
  exit 1
fi

echo "[Webhook]: Preparing resources and constants...";
AVATAR="https://github.com/actions.png"
BETONQUEST_URL="https://avatars1.githubusercontent.com/u/62897788?s=200&v=4"
COMMIT_ICON="https://github.githubassets.com/images/icons/emoji/unicode/1f6a8.png"
COMMIT_URL="https://github.com/$GITHUB_REPOSITORY/commit/$GITHUB_SHA"
ACTION_URL="$COMMIT_URL/checks"

EMBED_COLOR=15158332
STATUS_MESSAGE="Daily Dependency Check failed!"
STATUS_MESSAGE_DETAILS="Click here to inspect which dependencies couldn't be resolved."

echo "[Webhook]: Create the webhook data...";
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
      "url": "'"$BETONQUEST_URL"'"
    },
    "timestamp": "'"$TIMESTAMP"'"
  } ]
}'

echo "[Webhook]: Sending webhook to Discord...";
echo "$WEBHOOK_DATA"
(curl --fail --progress-bar -A "GitHub-Actions-Webhook" -H Content-Type:application/json -H X-Author:k3rn31p4nic#8383 -d "$WEBHOOK_DATA" "$WEBHOOK_URL" \
  && echo "[Webhook]: Successfully sent the webhook.") || echo "[Webhook]: Unable to send webhook."; exit 1
