# Pi_Irrigation-System

# Description
Python Script running on the pi would read moisture sensor data, when the soil of the plant would be dry, the pi would trigger a relay that turns on a pump every ten seconds until the moisture sensor returns a higher moisture reading. The pi also sends temperature, humidity, timestamp and sensor data to a firebase database, and this data is displayed on an application, where the user can see every time the pump was activated, what time and the status, as well as an image of the plants that is taken every 5 minutes.
