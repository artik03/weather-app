from flask import Blueprint, flash, redirect, render_template, request, url_for
from .models import User
from werkzeug.security import generate_password_hash, check_password_hash
from . import db
from flask_login import login_user, logout_user, login_required

auth = Blueprint('auth', __name__)

@auth.route("/login", methods=['GET', 'POST'])
def login():
    if request.method == 'POST':
        email = request.form.get('email')
        password = request.form.get('password')
        
        user = User.query.filter_by(email=email).first()
        if user:
            if check_password_hash(user.password, password):
                login_user(user, remember=True)
                flash("You are successfully logged in", 'success-global')
                return redirect(url_for('pages.home'))
            else:
                flash("Wrong password", 'error')
        else:
            flash("No account with given email exists", 'error')
                     
    return render_template('pages/login.html')

@auth.route("/sign-up", methods=['GET', 'POST'])
def sign_up():
    if request.method == 'POST':
        email = request.form.get('email')
        password = request.form.get('password')
        repeat_password  = request.form.get('password-repeat')
        
        user = User.query.filter_by(email=email).first()
        
        if user:
            flash('Account with this email already exists', 'warning-global')
            return redirect(url_for('auth.login'))
        elif len(email) < 5:
            flash('Your email should be at least 5 characters long', 'error')
        elif len(password) < 8:
            flash('Your password should be at least 8 characters long', 'error')
        elif password != repeat_password:
            flash("Your password doesn't match with repeated password", 'error')
        else:           
            new_user  = User(email=email, password=generate_password_hash(password, "pbkdf2"))
            db.session.add(new_user)
            db.session.commit()
            flash("Your account was successfully created", 'success-global')
            return redirect(url_for('auth.login'))
        
    return render_template('pages/sign_up.html')

@auth.route('/logout')
@login_required
def logout():
    logout_user()
    flash("You successfully logged out", 'success-global')
    return redirect(url_for('pages.home'))
