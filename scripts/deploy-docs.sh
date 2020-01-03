#!/bin/bash

# Only if DEPLOY_KEY exists
if [ -z "${DEPLOY_KEY}" ]; then
  echo "$0: Skipping deployment as no DEPLOY_KEY"
  exit 0
fi

# Prepare keys
eval "$(ssh-agent -s)"
ssh-add <(echo "${DEPLOY_KEY}" | base64 -d) || exit 1
git config user.name "Automatic Publish"

# Clone gh-pages branch
git clone --branch=gh-pages --depth=1 "git@github.com:${TRAVIS_REPO_SLUG}" gh-pages || exit 1

# If its to the master branch we build to 'latest'
if [ "${TRAVIS_BRANCH}" = "master" ];then
  echo "$0: Deploying Documents to gh-pages/en/latest"
  mkdir -p "gh-pages/en/latest" || exit 1
  mkdocs build --clean --strict --site-dir="gh-pages/en/latest" || exit 1
fi

# If its a tagged build, build to the tag version and link stable
if [ -n "${TRAVIS_TAG}" ]; then
  echo "$0: Deploying Documents to gh-pages/en/${TRAVIS_TAG}"
  mkdir -p "gh-pages/en/${TRAVIS_TAG}" || exit 1
  mkdocs build --clean --strict --site-dir="gh-pages/en/${TRAVIS_TAG}" || exit 1

  echo "$0: Linking gh-pages/stable to gh-pages/en/${TRAVIS_TAG}"
  (cd "gh-pages/en" && ln -sfn "${TRAVIS_TAG}" stable)
fi

# Build versions.json from english
cat <<EOF > gh-pages/versions.json
[
  {"version": "latest", "title": "latest", "aliases": []}
EOF
for i in $(ls -1 gh-pages/en/ | grep -v latest | grep -v stable | sort -n); do
  echo -n "  ,{\"version\": \"${i}\", \"title\": \"${i}\", \"aliases\": [" >> gh-pages/versions.json
  if [ "${i}" == "${TRAVIS_TAG}" ];then
    echo -n "\"stable\"" >> gh-pages/versions.json
  fi

  echo "]}" >> gh-pages/versions.json
done
echo "]" >> gh-pages/versions.json

# Commit
(
  cd gh-pages || exit 1
  git add -f .
  git commit -m "Deploy Documents: Build ${TRAVIS_BUILD_NUMBER}"
  git push -fq origin gh-pages > /dev/null
)
