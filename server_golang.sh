#!/bin/bash
realpath(){
  local x=$1
  echo $(cd $(dirname $0);pwd)/$x

}
cd $(realpath ./golang)
pwd
test -f ../../tools/rc/noderc && . ../../tools/rc/noderc
./golang
