# TODO: categories CRUD
# TODO: .arff generator
# TODO: languages

import os

from flask import (
        Flask,
        g,
        render_template,
        request,
        redirect,
        url_for,
        )
from flask_login import LoginManager, current_user
from flask_bcrypt import Bcrypt

from models.user import User
from models.product import Product
from util import db, requires_access_level

DATABASE = 'store.sqlite'

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///' + DATABASE
app.config['SECRET_KEY'] = os.getenv('SECRET_KEY', 'default_secret_key')
app.config.update(
        #SESSION_COOKIE_SECURE=True,
        #SESSION_COOKIE_HTTPONLY=True,
        SESSION_COOKIE_SAMESITE='Lax',
        )

login_manager = LoginManager()
login_manager.init_app(app)
bcrypt = Bcrypt(app)
db.init_app(app)

DATABASE = f'{app.instance_path}/{DATABASE}'

from routes import *
app.register_blueprint(index_bp)
app.register_blueprint(product_bp)
app.register_blueprint(client_bp)
app.register_blueprint(occurrence_bp)
app.register_blueprint(order_bp)


@login_manager.user_loader
def load_user(user_id):
    return User.query.get(user_id)


@app.before_request
def before_request():
    g.current_user = current_user
    g.first_user = User.query.count() == 0
    g.current_route = request.endpoint


@app.errorhandler(401)
def page_not_found(e):
    return render_template('error.html', page_title='Error: Unauthorized'), 401


@app.errorhandler(404)
def page_not_found(e):
    return render_template('error.html', page_title='Error: Page Not Found'), 404


@app.errorhandler(500)
def internal_error(e):
    db.session.rollback()
    return render_template('error.html', page_title='Error: Internal Server'), 500


with app.app_context():
    if not os.path.exists(DATABASE):
        db.create_all()

        Product.add_to_db(name='Abacaxi',
                          category_id=None,
                          unit_price=389,
                          units_stored=50,
                          units_min=48,
                          units_sold=0)
        Product.add_to_db(name='Abacate',
                          category_id=None,
                          unit_price=650,
                          units_stored=20,
                          units_min=10,
                          units_sold=0)
        Product.add_to_db(name='Pneu Continental Aro 14',
                          category_id=None,
                          unit_price=37490,
                          units_stored=4,
                          units_min=2,
                          units_sold=0)
