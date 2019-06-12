#ref https://stackoverflow.com/questions/2257441/random-string-generation-with-upper-case-letters-and-digits
import RPi.GPIO as GPIO
import time
import datetime
import grovepi
from grovepi import *
import pyrebase
from cv2 import *
import cv2
import threading

#PyreBase firebase configuration
config = {
    
    "apiKey": "AIzaSyBEqXQPvApfb7hcVO7Uui1Ef3Q1AkK2ssg",
    "authDomain": "plantpi-f9f46.firebaseapp.com",
    "databaseURL": "https://plantpi-f9f46.firebaseio.com",
    "projectId": "plantpi-f9f46",
    "storageBucket": "plantpi-f9f46.appspot.com",
    "messagingSenderId": "721743745119",
    "appId": "1:721743745119:web:06c98d371df82ab2",
    "serviceAccount": "/home/pi/Plant_Pi/plantpi-f9f46-firebase-adminsdk-ws87c-df8f0cc396.json"
    
    }


#Initialis config info
firebase = pyrebase.initialize_app(config)

#set up firebase storage and database
storage = firebase.storage()
db = firebase.database()

#GPIO Pins for Relay control
pin_A = 17
pin_B = 27

#GrovePi moisture sensors and ports
moisture_sensor_A = 0
moisture_sensor_B = 1
dht_sensor_port = 7
dht_sensor_type = 0

#setup GPIO pins
GPIO.setmode(GPIO.BCM)
GPIO.setup(pin_A,GPIO.OUT)
GPIO.setup(pin_B,GPIO.OUT)

##################### Plant A Start ###################

#Get moisture sensor data
def plant_A_moisture():
    try:
        moisture = grovepi.analogRead(moisture_sensor_A)
    except Exception as e:
        print ("Moisure Sensor A Error: ",e)
    return moisture

#Pump A controll
def pump_A():
    moisture = plant_A_moisture()
    print ("Plant A moisture is: ",moisture)
    try:
        if moisture > 50 and moisture <= 350: #Triggers when the moisture reading is within range
            
            try:
                pump_on(pin_A)                #Turns the pump On for 5 seconds
                print ("pump A on")
                time.sleep(5)
                
                pump_off(pin_A)               #Turns the pump Off
                print ("pump A off")
            except Exception as e:
                print ("Error with turning pump A on/off: ",e)

            watering_object = {               #Object that contains plant name, moisture and timestamp
                "plant":"A",
                "soil_moisture": moisture,
                "time_stamp":timestamp()
                }

            try:    
                db.child("Plants/list").push(watering_object) #Uploads Object to global firebase list
                db.child("Plant_A/list").push(watering_object)#Uploads Object to its own firebase list
            except Exception as e:
                print("Firebase A Upload Error: ",e)

            time.sleep(10)                    #After the upload, the thread waits 10 seconds before repeating
        else:
            pump_off(pin_A)                   
            print ("The Plant A is wet")
            time.sleep(1)                     #If the moisture condition isn't met, repeats every 1 second

    except Exception as e:
        pump_off(pin_A)                       #If an error occurs, the pump is turned off
        print ("Pump_B Error: ",e)
        time.sleep(1)
        
##################### Plant A End #####################


##################### Plant B Start ###################
        
def plant_B_moisture():
    try:
        moisture = grovepi.analogRead(moisture_sensor_B) 
    except Exception as e:
         print ("Moisure Sensor B Error: ",e)
    return moisture

def pump_B():
    moisture = plant_B_moisture()
    print ("Plant B moisture is: ",moisture)
    try:
        if moisture > 50 and moisture <= 350: #Triggers when the moisture reading is within range   
            try:
                pump_on(pin_B)                #Turns the pump On for 5 seconds
                print ("pump B on")
                time.sleep(5)
                
                
                pump_off(pin_B)               #Turns the pump Off
                print ("pump B off")
            except Exception as e:
                print ("Error with turning pump B on/off: ",e)

            watering_object = {               #Object that contains plant name, moisture and timestamp
                "plant":"B",
                "soil_moisture": moisture,
                "time_stamp":timestamp()
                }
            try:   
                db.child("Plants/list").push(watering_object) #Uploads Object to global firebase list
                db.child("Plant_B/list").push(watering_object)#Uploads Object to its own firebase list
            except Exception as e:
                print("Firebase B Upload Error: ",e)
            time.sleep(10)                     #After the upload, the thread waits 10 seconds before repeating
        else:
            pump_off(pin_B)
            print ("The Plant B is wet")
            time.sleep(1)                      #If the moisture condition isn't met, repeats every 1 second
    except Exception as e:
        pump_off(pin_B)                        #If an error occurs, the pump is turned off
        print ("Pump_B Error: ",e)
        time.sleep(1)

        
##################### Plant B End ###################

#uploads temperature, humidity and Plants moisture status
def plant_status():
    while True:
        try:
            #object contains temperature and humidity data
            status_temp_hum = {                          
                    "temperature": get_temp(),
                    "humidity": get_hum(),
                    "timestamp": timestamp(),
                }
            #object contains moisture data and a timestamp for plant A
            status_object_A = {
                    "plant":"A",
                    "soil_moisture": plant_A_moisture(),
                    "time_stamp":timestamp()
                }
            #object contains moisture data and a timestamp for plant B
            status_object_B = {
                    "plant":"B",
                    "soil_moisture": plant_B_moisture(),
                    "time_stamp":timestamp()
                }

            try:
                #Updates the status of each plants and the temp/hum status
                db.child("Plants/status").set(status_temp_hum)
                db.child("Plant_A/status").set(status_object_A)
                db.child("Plant_B/status").set(status_object_B)
                print ("status updated")
                
                #When uplaod is complete, the camera takes a picture and uploads it to firebase storage
                take_pic()
            except Exception as e:
                print("Error Updating Firebse/Storage Plant Status: ",e)
                
            #After the image has been uploaded, the thread waits 3 minutes beofre updating the status's and image
            time.sleep(180)
        except Exception as e:
            print("Plant_A_status: Error", e)
            time.sleep(1)
            
#Takes webcam piture and uplaods to firebase storage           
def take_pic():
    try:
        cam = cv2.VideoCapture(-1)
        img_counter = 0
        ret, frame = cam.read()
        cv2.waitKey(1)
        cv2.imwrite("/home/pi/Plant_Pi/images/plant.jpg", frame)
        storage.child("images/plant").put("/home/pi/Plant_Pi/images/plant.jpg")
        cam.release()
    except Exception as e:
        print("Error Taking Picture:",e)
        cam.release()

    
#Turns the pumps on
def pump_on(pin):
    try:
        GPIO.output(pin,GPIO.LOW)
    except Exception as e:
        print ("pump on error:",e)

#Turns the pumps off   
def pump_off(pin):
    try:
        GPIO.output(pin,GPIO.HIGH)
    except Exception as e:
        print ("pump off error",e)

#Get temperature data
def get_temp():
    try:
        [temp,hum] = dht(dht_sensor_port,dht_sensor_type)
    except Exception as e:
        print ("Error Collecting temp data: ",e)
    return temp

#get humidity data
def get_hum():
    try:
        [temp,hum] = dht(dht_sensor_port,dht_sensor_type)
    except Exception as e:
        print ("Error Collecting hum data: ",e)
    return hum

#get timestamp
def timestamp():
    ts = time.time()
    stamp = datetime.datetime.fromtimestamp(ts).strftime('%H:%M:%S')
    return stamp

# Running threads
def trigger_pump_A():
    print("Plant A on")
    while True:
        pump_A()
        
def trigger_pump_B():
    print("Plant B on")
    while True:
        pump_B()
        
#Starts pump A thread
threading.Thread(target=trigger_pump_A).start()

#Starts pump B thread
threading.Thread(target=trigger_pump_B).start()

#Starts status thread
threading.Thread(target=plant_status).start()