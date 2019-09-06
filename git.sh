!#/bin/bash


branch=test-$1
git checkout -b ${branch}
git add -A
git commit -m "test $1"
git push --set-upstream origin ${branch}
git tag $1 -m "---------------------nopush-------------------"
git push origin $1



