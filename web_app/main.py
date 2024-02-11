from flask import Flask
from main_package import create_app
import os

app = create_app()

port = int(os.environ.get('PORT', 5000))
    
if __name__ == "__main__":
    app.run(host="127.0.0.1", port=port, debug=True)
    