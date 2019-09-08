import os
import json

from django.http import JsonResponse

# Create your views here.

cur_dir = os.path.dirname(os.path.realpath(__file__))
json_filepath = os.path.join(cur_dir, 'progress.json')


def update_progress(request):
	username = request.GET.get('username', '')
	identifier = request.GET.get('identifier', '')
	page_num = int(request.GET.get('page_num', 0))
	_write_progress(username, identifier, page_num)
	return JsonResponse({
		'data': 'OK',
		'err': ''
	})

def get_latest_progress(request):
	username = request.GET.get('username', '')
	identifier = request.GET.get('identifier', '')
	page_num = _read_progress(username, identifier)
	return JsonResponse({
		'page_num': page_num,
		'err': ''
	})


def _write_progress(username, identifier, page_num):
	# get from json file
	fr = open(json_filepath, 'r')
	progress = json.load(fr)
	fr.close()
	# write to json file
	key = username + '=' + identifier
	progress[key] = page_num
	fw = open(json_filepath, 'w')
	json.dump(progress, fw)
	fw.close()

def _read_progress(username, identifier):
	# read from json file
	fr = open(json_filepath, 'r')
	progress = json.load(fr)
	key = username + '=' + identifier
	return progress.get(key, 0)
