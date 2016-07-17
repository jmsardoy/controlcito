from flask import Flask, request
from keys import *

app = Flask(__name__)

@app.route("/")
def index():
	return "control"

@app.route("/control", methods=['POST'])
def control():
	platform = request.form['platform']
	action = request.form['action']
	keyboard_action(action = action, platform = platform)
	return action + "\n"

@app.route("/itson", methods=['POST'])
def itson():
	return "True"

@app.route("/endpoint", methods=['POST'])
def endpoint():
	return "True"

if __name__== '__main__':
	app.debug=True
	app.run(host='0.0.0.0')