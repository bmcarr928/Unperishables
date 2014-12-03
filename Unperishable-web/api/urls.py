from django.conf.urls import patterns, url
from api import views, api_functions

urlpatterns = patterns('',
        url(r'^$', api_functions.index, name='index'),
        url(r'^adduser', api_functions.adduser, name='adduser'),
        url(r'^loginuser', api_functions.loginuser, name='loginuser'),
        url(r'^additem', api_functions.additem, name='additem'),
        url(r'^updateitem', api_functions.updateitem, name='updateitem'),
        url(r'^deleteitem', api_functions.deleteitem, name='deleteitem'),
        url(r'^sync', api_functions.sync, name='sync'),
        )
