#!/bin/bash

if [ $TRAVIS_PULL_REQUEST == "false" ] && [ "$TRAVIS_BRANCH" == "master" ] ; then

    echo -e "[Webhook]: Sending webhook to Discord...\\n";

    WEBHOOK_URL="https://discordapp.com/api/webhooks/$DISCORD_WEBHOOK_ID/$DISCORD_WEBHOOK_TOKEN"

    AUTHOR_NAME="$(git log -1 "$TRAVIS_COMMIT" --pretty="%aN")"
    COMMITTER_NAME="$(git log -1 "$TRAVIS_COMMIT" --pretty="%cN")"
    COMMIT_SUBJECT="$(git log -1 "$TRAVIS_COMMIT" --pretty="%s")"
    COMMIT_MESSAGE="$(git log -1 "$TRAVIS_COMMIT" --pretty="%b" | sed -E ':a;N;$!ba;s/\r{0,1}\n/\\n/g')"

    if [ "$AUTHOR_NAME" == "$COMMITTER_NAME" ]; then
      CREDITS="$AUTHOR_NAME authored & committed"
    else
      CREDITS="$AUTHOR_NAME authored & $COMMITTER_NAME committed"
    fi

    TIMESTAMP=$(date --utc +%FT%TZ)

    WEBHOOK_DATA='{
      "username": "",
      "avatar_url": "",
      "embeds": [ {
        "color": 13012051,
        "author": {
          "name": "Dev build #'"$TRAVIS_BUILD_NUMBER"' is now available",
          "url": "https://betonquest.pl/"
        },
        "title": "'"$COMMIT_SUBJECT"'",
        "url": "https://github.com/'"$TRAVIS_REPO_SLUG"'/commit/'"$TRAVIS_COMMIT"'",
        "description": "'"${COMMIT_MESSAGE//$'\n'/ }"\\n\\n"$CREDITS"'",
        "fields": [
          {
            "name": "Commit",
            "value": "'"[\`${TRAVIS_COMMIT:0:7}\`](https://github.com/$TRAVIS_REPO_SLUG/commit/$TRAVIS_COMMIT)"'",
            "inline": true
          },
          {
            "name": "Branch",
            "value": "'"[\`$TRAVIS_BRANCH\`](https://github.com/$TRAVIS_REPO_SLUG/tree/$TRAVIS_BRANCH)"'",
            "inline": true
          }
        ],
        "thumbnail": {
          "url": "https://cdn.discordapp.com/app-icons/494162764403572748/d7680ea0dfa2d1da1a95676435f526c1.png?size=64"
        },
        "timestamp": "'"$TIMESTAMP"'"
        } ]
    }'

    (curl --fail --progress-bar -A "TravisCI-Webhook" -H Content-Type:application/json -H X-Author:ungefroren#7524 -d "${WEBHOOK_DATA//	/ }" "$WEBHOOK_URL" \
      && echo -e "\\n[Webhook]: Successfully sent the webhook.") || echo -e "\\n[Webhook]: Unable to send webhook:\\n\\nWEBHHOK_DATA:\\n$WEBHOOK_DATA"

fi
