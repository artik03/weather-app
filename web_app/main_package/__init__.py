from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_login import LoginManager
from flask_jwt_extended import JWTManager

jwt = JWTManager()
db = SQLAlchemy()

def create_app(configuration):
    app = Flask(__name__)
    app.config.from_object(configuration)
    db.init_app(app)

    from .pages import pages as pages_blueprints
    from .auth import auth as auth_blueprint
    
    app.register_blueprint(pages_blueprints)
    app.register_blueprint(auth_blueprint)
    
    login_manager = LoginManager(app)
    login_manager.login_view = 'auth.login'
    login_manager.init_app(app)
    
    from .models import User, City
    
    @login_manager.user_loader
    def load_user(user_id):
        return User.query.get(user_id)
    
    with app.app_context():
        db.create_all()
        
    jwt.init_app(app)

    return app
