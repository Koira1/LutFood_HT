
import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore, storage

import pandas as pd
import numpy as np
import requests
from bs4 import BeautifulSoup as bs
import time
import json

cred = credentials.Certificate("./service-account-file.json")
app = firebase_admin.initialize_app(cred)


db = firestore.client()
print("Connection created")


url = "https://www.foodora.fi/en/restaurants/lat/61.052173/lng/28.0985505/plz/53850/city/"+"Lappeenranta/address/Korpimets%25C3%25A4nkatu%252010%252C%252053850%2520Lappeenranta%252C%2520Finland/Korpimets%25C3%25A4nkatu/10"


page = requests.get(url)

soup = bs(page.content, "html.parser")
data = soup.find_all("a", href=True)
data = [(d["href"], d.find('span', {'class' : 'name fn'}).text) for d in data if "restaurant/" in d["href"]]



df = pd.DataFrame(columns = ["restName", "category", "foodName", "ingredients", "version", "price"])

for restaurant, name in data:
    restName = name
    cUrl = "https://www.foodora.fi/en"+restaurant
    page = requests.get(cUrl)
    soup = bs(page.content, "html.parser")
    menu_items = soup.find_all('li', {'class':"dish-card h-product menu__item"})
    for item in menu_items:
        category = item["data-menu-category"]
        item_data = json.loads(item["data-object"])
        foodName = item_data["name"]
        description = item_data["description"]
        ingredients = description.replace("Sisältää: ", "").split(", ")
        pVariations = item_data["product_variations"]
        for product in pVariations:
            version = product["name"]
            price = product["price"]
            df.loc[len(df)] =             [restName, category, foodName, ingredients, version, price]
    time.sleep(0.2)





cols = list(df.columns)
cols.remove("ingredients")
df = df.drop_duplicates(cols)





df["index"] = df.index





df.loc[df["restName"]=="tastyfood", "price"]




rest_names = list(df["restName"].unique())

for rest_name in rest_names:
    doc_ref = db.collection("RestaurantNames").document(rest_name)
    doc_ref.set({
        u'restaurantName' : rest_name
        })


#["foodName", "ingredients", "version", "price", "index"]

#for rest_name in rest_names:
    #print("Starting: " + rest_name)
    #doc_ref = db.collection(rest_name)
    #categories = df.loc[df["restName"]==rest_name, "category"].unique()
    #for category in categories:
        #doc_ref = db.collection(rest_name).document(category)
        #for idx, row in df.loc[(df["restName"]==rest_name) & (df["category"]==category), :].iterrows():
            #print(row)
            #doc_ref = db.collection("LUTFood").document(rest_name).collection(category).document("Item" + str(row["index"]))
            #doc_ref.set({
            #u'foodName' : row["foodName"],
            #u'ingredients': row["ingredients"],
            #u'version': row["version"],
            #u'price': row["price"]
            #})

#batch = db.batch()

#for idx, row in df.iterrows():
    #doc_ref = db.collection(row["restName"] + str(row["index"] + 1)).document(row["foodName"] + row["version"])
    #batch.delete(doc_ref)

#for rest_name in rest_names:
    #for idx, row in df.loc[df["restName"]==rest_name].iterrows():
        #doc_ref = db.collection(rest_name).document(row["restName"] + str(row["index"]))
        #print(row)
        #doc_ref.set({
           # u'restaurant' : row["restName"],
           # u'category' : row["category"],
           # u'food' : row["foodName"],
            #u'ingredients' : row["ingredients"],
           # u'version' : row["version"],
            #u'price' : row["price"]
           # })

#for idx, row in df.iterrows():
    #doc_ref = db.collection(row["restName"] + str(row["index"])).document(row["foodName"] + row["version"])
    #print(row)
    #doc_ref.set({
        #u'restaurant' : row["restName"],
        #u'category' : row["category"],
        #u'food' : row["foodName"],
        #u'ingredients' : row["ingredients"],
        #u'version' : row["version"],
        #u'price' : row["price"]
        #})


print("Finished")
print(list(df["restName"].unique()))




