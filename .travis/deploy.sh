if [[ "$TRAVIS_PULL_REQUEST" == "false" && $TRAVIS_BRANCH == "master" ]]; then
  export PROJECT_NAME=$(basename $TRAVIS_REPO_SLUG)
  echo -e "Deploying '$PROJECT_NAME' to maven repo:\n"

  git config --global user.email "travis-ci@users.noreply.github.com"
  git config --global user.name "travis-ci"
  git config --global push.default simple

  echo -e "Cloning maven repo ...\n"
  export REPO_DIR=$(mktemp -d 2>/dev/null || mktemp -d -t mvn-repo)
  git clone https://$GH_TOKEN:x-oauth-basic@github.com/turbolent/mvn-repo.git $REPO_DIR

  echo -e "Running maven deploy ...\n"
  mvn deploy -DrepositoryPath=$REPO_DIR -DskipTests

  echo -e "Committing and pushing ...\n"
  cd $REPO_DIR
  git add -A
  git commit -a -m "update $PROJECT_NAME"
  git push

  echo -e "Deployed.\n"
fi
