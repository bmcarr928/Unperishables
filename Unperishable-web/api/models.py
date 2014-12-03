from django.db import models

class User(models.Model):
    name = models.CharField(max_length=128, unique=True)
    password = models.CharField(max_length=128)

    def __unicode__(self):
        return self.name

class Item(models.Model):
    name = models.CharField(max_length=128)
    quantity = models.IntegerField(default=0)
    category = models.IntegerField(default=0)
    input_date = models.IntegerField(default=0)
    expiration_date = models.IntegerField(default=0)
    owner = models.CharField(max_length=128)
    master_account = models.ForeignKey('User')

    class Meta:
        unique_together = ("name", "master_account")

    def __unicode__(self):
        return self.name + " owned by " + self.master_account.name
