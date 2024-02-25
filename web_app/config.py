import os

basedir = os.path.abspath(os.path.dirname(__file__))
db_name = 'weather.db'

class Configuration:

    Debug = True
    SQLALCHEMY_COMMIT_ON_TEARDOWN = True
    SQLALCHEMY_TRACK_MODIFICATIONS = True
    SQLALCHEMY_DATABASE_URI = 'sqlite:///' + os.path.join(basedir, db_name)
    SECRET_KEY = os.getenv('SECRET_KEY')
    OPEN_WEATHER_MAP_KEY = os.getenv('OPEN_WEATHER_MAP_KEY')
    