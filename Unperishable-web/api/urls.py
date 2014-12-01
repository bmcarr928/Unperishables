from django.conf.urls import patterns, url
from api import views, api_functions

urlpatterns = patterns('',
        url(r'^$', api_functions.index, name='index'),
        url(r'^additem', api_functions.additem, name='additem'),
        url(r'^sync', api_functions.sync, name='sync'),
        )
