from django.db import models

class Item(models.Model):
    name = models.CharField(max_length=128, unique=True)
    quantity = models.IntegerField(default=0)
    category = models.IntegerField(default=0)
    input_date = models.IntegerField(default=0)
    expiration_date = models.IntegerField(default=0)
    owner = models.CharField(max_length=128)

    def __unicode__(self):
        return self.name

class User(models.Model):
    name = models.CharField(max_length=128)
    password = models.CharField(max_length=128)
    master_account = models.ForeignKey('self')

    def __unicode__(self):
        return self.title
