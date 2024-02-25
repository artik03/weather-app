from flask import Flask
from main_package import create_app
import os
from config import Configuration

app = create_app(Configuration)


port = int(os.environ.get('PORT', 5000))
    
if __name__ == "__main__":
    app.run(host="0.0.0.0", port=port, debug=True)
    