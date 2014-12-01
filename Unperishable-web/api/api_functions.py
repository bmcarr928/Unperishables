from django.shortcuts import render
from django.http import HttpResponse
import json
import time

# Create your views here.
def index(request):
    return HttpResponse("Hellllllo");

def additem(request):
    if request.GET:
        return HttpResponse("Added an item")
    else:
        return HttpResponse("What?")


def sync(request):
    json_array = []
    json_item1 = {
        "name":"bananas",
        "category":0,
        "quantity":1,
        "input_date":current_time_millis(),
        "expiration_date":current_time_millis(),
        "owner":""
    }
    json_item2 = {
        "name":"bananas",
        "category":0,
        "quantity":1,
        "input_date":current_time_millis(),
        "expiration_date":current_time_millis(),
        "owner":""
    }
    json_item3 = {
        "name":"bananas",
        "category":0,
        "quantity":1,
        "input_date":current_time_millis(),
        "expiration_date":current_time_millis(),
        "owner":""
    }
    json_item4 = {
        "name":"bananas",
        "category":0,
        "quantity":1,
        "input_date":current_time_millis(),
        "expiration_date":current_time_millis(),
        "owner":""
    }
    json_array.append(json_item1)
    json_array.append(json_item2)
    json_array.append(json_item3)
    json_array.append(json_item4)
    return HttpResponse(json.dumps(json_array));



def current_time_millis():
    return int(round(time.time() * 1000))