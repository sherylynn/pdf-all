# pdf-all

[![Build Status](https://travis-ci.com/sherylynn/pdf-all.svg?branch=master)](https://travis-ci.com/sherylynn/pdf-all)
a upgrade for pdf-sync with native pdf render and new sync

## client

### javascript plugins

./acrobat is plugin for pdf reader acrobat

U should use folder_level.js. More information is read in ./acrobat/readme.md

### android apk

./app is application for android 

### PC application

project [pdf-sync](https://github.com/sherylynn/pdf-sync) is application for windows mac linux

## server

### by simple golang

src is in ./golang

cd ./golang && go build ./

U can run ./server_golang.sh to start or run ./systemd_golang.sh to launch systemd

### by golang with gin

src is in ./golang_gin

cd ./golang_gin && go build ./ && ./golang_gin

same progress_cn.json formart with simple golang

### by python

src is in ./django .

U can run ./server_django.sh to start or run ./systemd_django.sh to launch systemd

### by go with nodejs

src is in ./golang_nodejs .

cd ./golang_nodejs && go build ./ && ./golang_noedjs

Share progress.json with python