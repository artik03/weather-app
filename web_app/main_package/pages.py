import random
from flask import Blueprint, flash, redirect, render_template, request, url_for
from flask_login import login_required, current_user
import urllib.request, json

from .settings import OPEN_WEATHER_MAP_KEY
from .variables import larger_capital_cities as capital_cities
from .models import City
from . import db

pages = Blueprint('pages', __name__)

@pages.route("/")
def home():
    return render_template('pages/index.html')

@pages.route('/current-weather/', methods=['GET', 'POST']) #to do
def current_weather():
    
    if request.method == 'POST':
        city_to_search = request.form.get('search')
        return redirect(url_for('pages.current_weather', search=city_to_search))
        
    if request.method == 'GET' and request.args.get('search') == None:
        city_to_search = capital_cities[random.randint(0, len(capital_cities)-1)]
        return redirect(url_for('pages.current_weather', search=city_to_search))
     
    search = request.args.get('search')
    dict = {}
    try:
        search = search.replace(" ", "%20")
        url = f"https://api.openweathermap.org/data/2.5/weather?q={search}&appid={OPEN_WEATHER_MAP_KEY}"

        response = urllib.request.urlopen(url)
        data = response.read()
        dict = json.loads(data)
        
    except: 
        dict["name"] = "NOT FOUND"
        
    favCityList = []
    try:
        favCityList = current_user.city
    except:
        print('Couldnt get user fav cities') 
    
    return render_template('pages/current_weather.html', favCityList=favCityList, weatherData=dict, user=current_user)



@pages.route('/profile', methods=['GET', 'POST'])
@login_required
def profile():
    
    if request.method == 'POST':
        city_name = request.form.get('newCity')
        city_to_add = City.query.filter_by(name = request.form.get('newCity')).first()
       
        if city_to_add in current_user.city:
            flash('This city already exists in your list', 'warning-global')
        else:
            favcity = city_to_add
            if not favcity:
                favcity = City(name=city_name)
                db.session.add(favcity)
            
            current_user.city.append(favcity)
            db.session.commit()
            
    
    return render_template('pages/profile.html', favCityList=current_user.city)

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