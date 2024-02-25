from tkinter import *
import urllib.request, json 
from digidevice import location
    
OPEN_WEATHER_MAP_KEY = "33184056053dcff2a1eb49c809d5bdb1" # To Do

def get_weather_data(search):
    try:
        search = search.replace(" ", "%20")
        url = f"https://api.openweathermap.org/data/2.5/weather?q={search}&appid={OPEN_WEATHER_MAP_KEY}"

        with urllib.request.urlopen(url) as response:
            data = response.read().decode('utf-8')
            return json.loads(data)

    except urllib.error.URLError as e:
        print("Error: Failed to connect to the server:", e.reason)
        return None
    except json.JSONDecodeError as e:
        print("Error: Failed to parse JSON response:", e)
        return None
    
    
def getCoordinates():
    loc = location.Location()
    loc.valid_fix
    return loc

print(getCoordinates())