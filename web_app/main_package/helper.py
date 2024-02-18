import datetime
import urllib.request, json

def timestamp_to_time(timestamp):
    dt_object = datetime.datetime.utcfromtimestamp(timestamp)
    return dt_object.strftime('%H:%M')

def get_visibility_description(visibility):
    if visibility < 100:
        return "Zero"
    elif visibility < 1600:
        return "Very Poor"
    elif visibility < 5000:
        return "Poor"
    elif visibility < 8000:
        return "Moderate"
    elif visibility < 10000:
        return "Good"
    else:
        return "Excellent"
    
def wind_deg_to_dir(deg):
    compass_directions = ['NW', 'N', 'NE', 'E', 'SE', 'S', 'SW', 'W']
    return compass_directions[round(deg / 360 * 8) % 8]

def getWeatherdata(url): 
    try:
        
        data = urllib.request.urlopen(url).read()
        weatherData = json.loads(data)
        
        # convert sunrise/sunset to HH:MM        
        try:
            weatherData['sys']['sunrise'] = timestamp_to_time(int(weatherData['sys']['sunrise'] + weatherData['timezone']))
            weatherData['sys']['sunset'] = timestamp_to_time(int(weatherData['sys']['sunset'] + weatherData['timezone']))
        except Exception as e:
            return (None, f"Error converting the weather data: {e}")
        
        # visibility num -> god/bad/normal...
        try:
            weatherData['visibility'] = get_visibility_description(weatherData['visibility'])
        except Exception as e:
            return (None, f"Error making visibility description: {e}")
        
        # wind deg to direction
        try:
            weatherData["wind"]["deg"] = wind_deg_to_dir(weatherData["wind"]["deg"])
        except Exception as e:
            return (None, f"Error converting wind degrees to direction: {e}")

    except Exception as e:
        return (None, f"Error getting the weather data: {e}")

    return (weatherData, None)