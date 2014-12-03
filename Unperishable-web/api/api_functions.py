from django.db import IntegrityError
from django.core.exceptions import ObjectDoesNotExist
from django.views.decorators.csrf import csrf_exempt
from django.http import HttpResponse
from api.models import Item, User
from django.core import serializers
import json
import time

# Create your views here.
def index(request):
    return HttpResponse("{\"word\":\"hello\"}")

@csrf_exempt
def adduser(request):
    json_request = json.loads(request.read())
    username = json_request['name']
    password = json_request['password']
    try:
        User.objects.create(name=username, password=password)
        return HttpResponse("Success")
    except IntegrityError:
        return HttpResponse("Failed", status=403)

@csrf_exempt
def loginuser(request):
    json_request = json.loads(request.read())
    username = json_request['name']
    password = json_request['password']
    try:
        User.objects.get(name=username, password=password)
        return HttpResponse("Success")
    except ObjectDoesNotExist:
        return HttpResponse("Failed", status=403)

@csrf_exempt
def additem(request):
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
    try:
        p_add_item(name=name, category=category, quantity=quantity, master=user,
                   input_date=input_date, expiration_date=expiration_date, owner=user)
        return HttpResponse("Success")
    except IntegrityError:
        return HttpResponse("Failed", status=403)

@csrf_exempt
def updateitem(request):
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
    try:
        p_update_item(name=name, category=category, quantity=quantity, master=user,
                      input_date=input_date, expiration_date=expiration_date, owner=user.name)
        return HttpResponse("Success")
    except ObjectDoesNotExist:
        return HttpResponse("Failed, object does not exist", status=403)

@csrf_exempt
def deleteitem(request):
    string = request.read()
    json_request = json.loads(string)
    json_owner = json_request.get('name')
    item_name = json_request.get('item_name')
    try:
        Item.objects.filter(name=item_name, owner=json_owner).delete()
        return HttpResponse("Success")
    except ObjectDoesNotExist:
        return HttpResponse("Failed, object does not exist", status=403)

@csrf_exempt
def sync(request):
    string = request.read()
    json_request = json.loads(string)
    user = User.objects.filter(name=json_request['name'])
    return HttpResponse(serializers.serialize("json", Item.objects.filter(master_account=user)))

def current_time_millis():
    return int(round(time.time() * 1000))

def p_update_item(name, category, quantity, master, input_date=current_time_millis(),
                  expiration_date=current_time_millis(), owner=""):
    try:
        i = Item.objects.filter(name=name, master_account=master)\
                        .update(category=category, quantity=quantity,
                                input_date=input_date, expiration_date=expiration_date,
                                owner=owner)
        return i
    except ObjectDoesNotExist:
        raise ObjectDoesNotExist

def p_add_item(name, category, quantity, master, input_date=current_time_millis(),
               expiration_date=current_time_millis(), owner=""):
    try:
        i = Item.objects.create(name=name, category=category, quantity=quantity,
                                input_date=input_date, expiration_date=expiration_date,
                                owner=owner, master_account=master)
        return i
    except IntegrityError:
        raise IntegrityError