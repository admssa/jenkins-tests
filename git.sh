!#/bin/bash


branch=test-$1
git checkout -b ${branch}
git add -A
git commit -m "tedt $1"
git push --set-upstream origin ${branch}
git tag $1 -m "---------------------nocheck-------------------"
git push origin $1



