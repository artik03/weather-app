import datetime
import urllib.request, json

def timestampToTime(timestamp):
    dt_object = datetime.datetime.utcfromtimestamp(timestamp)
    return dt_object.strftime('%H:%M')

def getWeatherdata(url): 
    try:
        
        data = urllib.request.urlopen(url).read()
        weatherData = json.loads(data)
                
        try:
            weatherData['sys']['sunset'] = timestampToTime(int(weatherData['sys']['sunset'] + weatherData['timezone']))
        except Exception as e:
            return (None, f"Error converting the weather data: {e}")

    except Exception as e:
        return (None, f"Error getting the weather data: {e}")

    return (weatherData, None)