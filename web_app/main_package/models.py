from uuid import uuid4
from flask_login import UserMixin
from . import db

class User(UserMixin, db.Model):
    __tablename__ = 'user'
        
    id = db.Column(db.String(36), primary_key=True, default=str(uuid4()))
    email = db.Column(db.String(100), unique=True)
    password = db.Column(db.String(100), nullable=False)
    
    city = db.relationship('City', secondary = 'user_city', lazy='subquery',
        backref=db.backref('users', lazy=True))


class City(db.Model):
    __tablename__ = 'city'
    
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(100), unique=True, nullable = False)
    
user_city = db.Table(
  'user_city',
  db.Column('user_id', db.String(36), db.ForeignKey('user.id'), primary_key=True),
  db.Column('city_id', db.Integer, db.ForeignKey('city.id'), primary_key=True)
)
