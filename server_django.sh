#!/bin/bash
realpath(){
  local x=$1
  echo $(cd $(dirname $0);pwd)/$x

}
echo $(whoami)
echo $PYTHONPATH
echo $FUCK
python3 - <<EOF
import sys
print(sys.path)
EOF
python3 $(realpath ./django/manage.py) migrate
python3 $(realpath ./django/manage.py) runserver 0.0.0.0:10000
