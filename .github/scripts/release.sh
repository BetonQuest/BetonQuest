#!/bin/bash
CURRENT_PATH=$(pwd)
if [[ "${CURRENT_PATH}" == */.github/scripts ]]; then
  cd ../../
fi

MAVEN_VERSION=$(mvn help:evaluate -Dexpression=version -q -DforceStdout)
echo "Release version: ${MAVEN_VERSION}"
echo -n "Enter next version: "
read -r NEXT_VERSION
if [ -z "${NEXT_VERSION}" ]; then
  echo "You need to enter the next version!"
  exit 1
fi
echo -n "Enter the name (not URL) of the remote repository you want to create the release in. 'upstream' is the fallback if no input is provided: "
read -r REMOTE_REPOSITORY
if [ -z "${REMOTE_REPOSITORY}" ]; then
  REMOTE_REPOSITORY=upstream
fi
echo "Remote repository: ${REMOTE_REPOSITORY}"

echo "Create version tag and push it..."
git tag v"${MAVEN_VERSION}" HEAD
git push "${REMOTE_REPOSITORY}" v"${MAVEN_VERSION}"
echo "Version tag created and pushed."

echo "Update version and changelog..."
sed -i "s~<version>${MAVEN_VERSION}</version>~<version>${NEXT_VERSION}</version>~g" pom.xml
DATE=$(date +%Y-%m-%d)
NEW_CHANGELOG="## \[Unreleased\] - \${current-date}\n### Added\n### Changed\n### Deprecated\n### Removed\n### Fixes\n### Security\n"
sed -i "s~## \[Unreleased\] - \${current-date}~${NEW_CHANGELOG}\n## \[${MAVEN_VERSION}\] - ${DATE}~g" CHANGELOG.md
echo "Version and changelog updated."

echo "Commit and push 'pom.xml' and 'CHANGELOG.md'..."
git commit --all --message="Version bump to ${NEXT_VERSION}"
git push ${REMOTE_REPOSITORY}
echo "'pom.xml' and 'CHANGELOG.md' committed and pushed."

echo ""
echo "Release script run was successful and a release is now in progress on GitHub."
