import random
from flask import Blueprint, flash, redirect, render_template, request, url_for
from flask_login import login_required, current_user
from .helper import getIpLocation, getWeatherdata

import os
from dotenv import load_dotenv
load_dotenv()

from .variables import larger_capital_cities as capital_cities
from .models import City
from . import db

pages = Blueprint('pages', __name__)

@pages.route("/")
def home():
    return render_template('pages/index.html')

@pages.post('/search-city/')
def search_city():
    city_to_search = request.form.get('search')
    return redirect(url_for('pages.current_weather', search=city_to_search))

@pages.post('/device-loc/')
def device_loc():
    request_data = request.get_json()
    lat = str(request_data['lat'])
    lon = str(request_data['lon'])
    
    # if some unexpected error happened and data are missing redirect to ip location
    if not lat or not lon: return redirect(url_for('pages.current_weather', search="MY-LOCATION"))
    else: return redirect(url_for('pages.current_weather', lat=lat, lon=lon))

@pages.route('/current-weather/') #to do
def current_weather():
         
    # init
    weather_data = {}  
    search = request.args.get('search')
    lat = request.args.get('lat')
    lon = request.args.get('lon')
    
    # check for coords
    if lat and lon:
        url = f"https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid={app.config}&units=metric"
        weather_data, error = getWeatherdata(url)
        
        # if error redirect to ip location
        if error: return redirect(url_for('pages.current_weather', search="MY-LOCATION"))
        else: 
            # change city in url to current
            if search != weather_data['name']:
                return redirect(url_for('pages.current_weather', search=weather_data['name'], lat=lat, lon=lon))
    elif (lon and not lat) or (lat and not lon):
        return redirect(url_for('pages.current_weather', search=search))
    
    # if lat,lon,search are none generate random
    if not search:
        city_to_search = capital_cities[random.randint(0, len(capital_cities)-1)]
        return redirect(url_for('pages.current_weather', search=city_to_search))
    
    # my location if browser location is not permited
    if search == 'MY-LOCATION':
        ip = request.remote_addr
        ip = "84.245.80.28"

        data = getIpLocation(ip)

        flash("To get more precise results allow browser location.", category='warning-global')
        
        return redirect(url_for('pages.current_weather', lat=data.latitude, lon=data.longitude))

    if not lat:
        search = search.replace(" ", "%20")
        url = f"https://api.openweathermap.org/data/2.5/weather?q={search}&appid={os.getenv('OPEN_WEATHER_MAP_KEY')}&units=metric"
                    
        weather_data, error = getWeatherdata(url)

        if error:
            weather_data = {}
            weather_data["name"] = "NOT FOUND"
        
    favCityList = []
    if current_user:
        try:
            favCityList = current_user.city
        except:
            flash("You have no favourite cities. Add them in your profile.", category='warning-global') 
    else:
        flash("You must be logged in to set your favourite cities.", category='warning-global') 
    
    
    return render_template('pages/current_weather.html', favCityList=favCityList, weatherData=weather_data, user=current_user)



@pages.route('/profile', methods=['GET', 'POST'])
@login_required
def profile():
    
    if request.method == 'POST':
        city_name = request.form.get('newCity')
        city_name = city_name.replace(" ", "%20")
        
        url = f"https://api.openweathermap.org/data/2.5/weather?q={city_name}&appid={OPEN_WEATHER_MAP_KEY}&units=metric"                 
        weather_data, error = getWeatherdata(url)
        
        if error:
            flash(f"Sorry, we don't provide weather forecast for {city_name}. Check for spelling mistakes.", 'warning-global')
            sorted_cities = sorted(current_user.city, key=lambda city: city.name)
            return render_template('pages/profile.html', favCityList=sorted_cities)
        
        city_name = city_name.replace("%20", " ")
        city_to_add = City.query.filter_by(name = request.form.get('newCity')).first()
       
        if city_to_add in current_user.city:
            flash('This city already exists in your list', 'warning-global')
        else:
            if not city_to_add:
                city_to_add = City(name=city_name)
                db.session.add(city_to_add)
            
            current_user.city.append(city_to_add)
            db.session.commit()
            
    sorted_cities = sorted(current_user.city, key=lambda city: city.name)
    return render_template('pages/profile.html', favCityList=sorted_cities)

@pages.post('/profile/del-city/<city_name>')
@login_required
def delete_city(city_name):
    city_to_delete = City.query.filter_by(name=city_name).first()
    
    if city_to_delete in current_user.city:
        current_user.city.remove(city_to_delete)
        if City.query.filter_by(name=city_name).count() == 0:
            db.session.remove(city_to_delete)
        db.session.commit()
    else:
        flash('City you are trying to delete is not in your list', category='warning-global')
    return redirect(url_for('pages.profile'))

@pages.route('/download-app/') # to do 
def download():
    pass