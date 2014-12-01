from django.shortcuts import render
from django.views.decorators.csrf import csrf_exempt
from django.http import HttpResponse
from api.models import Item, User
from django.core import serializers
import json
import time

# Create your views here.
def index(request):
    return HttpResponse("{\"word\":\"hello\"}");

@csrf_exempt
def additem(request):
    if request.method == 'GET':
        Item.objects.all().delete()
        User.objects.all().delete()
        u1 = User.objects.get_or_create(name="Bob", password="bobness")[0]
        u2 = User.objects.get_or_create(name="Steve", password="steveness")[0]
        p_add_item("bananas", 0, 2, master=u1)
        p_add_item("milk", 0, 1, master=u1)
        p_add_item("cheese", 0, 2, master=u1)
        p_add_item("salt", 2, 2, master=u2)
        p_add_item("pepper", 2, 0, master=u2)
        p_add_item("green beans", 1, 2, master=u2)
        return HttpResponse("Added an item")
    else:
        string = request.read()
        json_request = json.loads(string)
        json_owner = json_request.get('name')
        json_item = json_request.get('item')
        name = json_item['name']
        category = json_item['category']
        quantity = json_item['quantity']
        expiration_date = json_item.get('input_date', current_time_millis())
        input_date = json_item.get('expiration_date', current_time_millis())
        user = User.objects.get(name=json_owner)
        p_add_item(name=name, category=category, quantity=quantity, master=user,
                   input_date=input_date, expiration_date=expiration_date, owner=user)
        return HttpResponse("What?")

@csrf_exempt
def sync(request):
    string = request.read()
    json_request = json.loads(string)
    user = User.objects.filter(name=json_request['name'])
    return HttpResponse(serializers.serialize("json", Item.objects.filter(master_account=user)))

def current_time_millis():
    return int(round(time.time() * 1000))

def p_add_item(name, category, quantity, master, input_date=current_time_millis(),
               expiration_date=current_time_millis(), owner=""):
    i = Item.objects.get_or_create(name=name, category=category, quantity=quantity,
                                   input_date=input_date, expiration_date=expiration_date,
                                   owner=owner, master_account=master)[0]
    return i