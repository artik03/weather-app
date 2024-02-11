import os
from dotenv import load_dotenv
load_dotenv()

OPEN_WEATHER_MAP_KEY = os.getenv('OPEN_WEATHER_MAP_KEY')
GEOAPIFY_KEY = os.getenv('GEOAPIFY_KEY')
