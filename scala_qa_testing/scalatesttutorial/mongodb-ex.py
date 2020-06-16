#MongoDB Example
import pymongo import MongoClient
import datetime

#Open connection with localhost and port 27017
client = MongoClient("localhost", 27017)

#Access test Database
db = client.test_database

#Get the test collection
collection = db.test_collection

#Use Python Dictionary to create document
post = {"author": "Mike",
        "text": "This is a test",
        "tags": ["mongodb", "python", "pymongo"],
        "date": datetime.datetime.utcnow()
        }

#Insert a Document
posts = db.posts
post_id = posts.insert_one(post).inserted_id
post_id