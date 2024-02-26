from flask import render_template
from time import time
import os
import jwt
import smtplib, ssl
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText

def send_email(user):
    expires = 900
    
    sender_email = os.getenv('MAIL_USERNAME')
    receiver_email = user.email
    password = os.getenv('MAIL_PASSWORD') 

    token = jwt.encode({'reset_password': user.email, 'exp': time() + expires},
                        key=os.getenv('SECRET_KEY'))
    
    msg = MIMEMultipart('alternative')
    msg['Subject'] = "WeatherFrog | Password reset"
    msg['From'] = sender_email
    msg['To'] = receiver_email 
    
    html_msg = render_template('email/reset_mail.html', token=token)
    msg.attach(MIMEText(html_msg, 'html'))

    context = ssl.create_default_context()

    with smtplib.SMTP_SSL("smtp.gmail.com", 465, context=context) as server:
        server.login(sender_email, password)
        server.sendmail(sender_email, receiver_email, msg.as_string())
        server.quit()