!#/bin/bash

git add -A
git commit -m "test $1"
git push --set-upstream origin $(git branch | grep \* | cut -d ' ' -f2)
git tag $1 -m "---------------------push-------------------"
git push origin $1



