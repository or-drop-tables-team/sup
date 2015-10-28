#!/bin/bash -e
#
# Travis-CI VM allows us to run arbitrary shell stuff. This script is executed
# in order to generate Doxygen documentation after a successful build and
# publish it to Sup's GitHub Pages, at http://or-drop-tables-team.github.io/sup/
# 
# This script adapted from https://github.com/tgockel/nginxconfig/blob/master/config/publish-doxygen

# Define some variables
REPO_PATH=git@github.com:or-drop-tables-team/sup.git
HTML_PATH=docs/doxygen/html
COMMIT_USER="Documentation Builder"
COMMIT_EMAIL="sajarvis@bu.edu"
# This grabs the current revision in HEAD to associate a build with the docs
CHANGESET=$(git rev-parse --verify HEAD)

# Get a clean version of the HTML documentation repo.
rm -rf ${HTML_PATH}
mkdir -p ${HTML_PATH}
git clone -b gh-pages "${REPO_PATH}" --single-branch ${HTML_PATH}

# rm all the files through git to prevent stale files.
# rm everything ensures that stale docs are actually removed, otherwise they'll
# persist unchanged
cd ${HTML_PATH}
git rm -rf .
cd -

# Generate the HTML documentation.
doxygen Doxyfile

# Create and commit the documentation repo.
cd ${HTML_PATH}
git add .
git config user.name "${COMMIT_USER}"
git config user.email "${COMMIT_EMAIL}"
git commit -m "Automated documentation build for changeset ${CHANGESET}."
git push origin gh-pages
cd -
