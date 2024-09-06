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

        #
        # Product examples
        #

        Product.add_to_db(name='Banana Nanica 1kg',
                          unit_price=350,
                          units_stored=100,
                          units_min=90)
        Product.add_to_db(name='Tomate Italiano 1kg',
                          unit_price=850,
                          units_stored=80,
                          units_min=70)
        Product.add_to_db(name='Cebola Branca 1kg',
                          unit_price=500,
                          units_stored=120,
                          units_min=110)
        Product.add_to_db(name='Maçã Gala 1kg',
                          unit_price=699,
                          units_stored=60,
                          units_min=50)
        Product.add_to_db(name='Batata Inglesa 1kg',
                          unit_price=320,
                          units_stored=150,
                          units_min=130)
        Product.add_to_db(name='Abacaxi',
                          unit_price=389,
                          units_stored=50,
                          units_min=48)
        Product.add_to_db(name='Abacate',
                          unit_price=650,
                          units_stored=20,
                          units_min=10)

        Product.add_to_db(name='Queijo Mussarela 1kg',
                          unit_price=2990,
                          units_stored=40,
                          units_min=30)
        Product.add_to_db(name='Leite Desnatado 1L',
                          unit_price=420,
                          units_stored=100,
                          units_min=80)
        Product.add_to_db(name='Manteiga com Sal 200g',
                          unit_price=1190,
                          units_stored=50,
                          units_min=40)
        Product.add_to_db(name='Iogurte Natural 170g',
                          unit_price=290,
                          units_stored=120,
                          units_min=110)
        Product.add_to_db(name='Requeijão Cremoso 200g',
                          unit_price=799,
                          units_stored=70,
                          units_min=60)

        Product.add_to_db(name='Filé de Frango 1kg',
                          unit_price=1790,
                          units_stored=50,
                          units_min=45)
        Product.add_to_db(name='Carne Moída Bovina 1kg',
                          unit_price=3490,
                          units_stored=40,
                          units_min=35)
        Product.add_to_db(name='Costela Suína 1kg',
                          unit_price=2990,
                          units_stored=30,
                          units_min=20)
        Product.add_to_db(name='Linguiça Toscana 1kg',
                          unit_price=1590,
                          units_stored=60,
                          units_min=50)
        Product.add_to_db(name='Picanha Bovina 1kg',
                          unit_price=5490,
                          units_stored=20,
                          units_min=15)

        Product.add_to_db(name='Arroz Branco 5kg',
                          unit_price=2490,
                          units_stored=90,
                          units_min=80)
        Product.add_to_db(name='Feijão Carioca 1kg',
                          unit_price=799,
                          units_stored=80,
                          units_min=70)
        Product.add_to_db(name='Açúcar Cristal 5kg',
                          unit_price=1790,
                          units_stored=70,
                          units_min=60)
        Product.add_to_db(name='Café em Pó 500g',
                          unit_price=1290,
                          units_stored=50,
                          units_min=40)
        Product.add_to_db(name='Macarrão Espaguete 500g',
                          unit_price=399,
                          units_stored=100,
                          units_min=90)

        Product.add_to_db(name='Refrigerante Coca-Cola 2L',
                          unit_price=799,
                          units_stored=100,
                          units_min=80)
        Product.add_to_db(name='Cerveja Skol Lata 350ml',
                          unit_price=350,
                          units_stored=200,
                          units_min=180)
        Product.add_to_db(name='Água Mineral 500ml',
                          unit_price=250,
                          units_stored=150,
                          units_min=130)
        Product.add_to_db(name='Suco de Laranja 1L',
                          unit_price=499,
                          units_stored=60,
                          units_min=50)
        Product.add_to_db(name='Vinho Tinto Seco 750ml',
                          unit_price=2490,
                          units_stored=40,
                          units_min=30)

        Product.add_to_db(name='Detergente Neutro 500ml',
                          unit_price=250,
                          units_stored=100,
                          units_min=90)
        Product.add_to_db(name='Sabão em Barra 5 unidades',
                          unit_price=599,
                          units_stored=80,
                          units_min=70)
        Product.add_to_db(name='Desinfetante Pinho 1L',
                          unit_price=750,
                          units_stored=60,
                          units_min=50)
        Product.add_to_db(name='Amaciante de Roupas 2L',
                          unit_price=1290,
                          units_stored=50,
                          units_min=45)
        Product.add_to_db(name='Água Sanitária 1L',
                          unit_price=450,
                          units_stored=100,
                          units_min=80)

        Product.add_to_db(name='Papel Higiênico 12 rolos',
                          unit_price=1590,
                          units_stored=80,
                          units_min=70)
        Product.add_to_db(name='Creme Dental Colgate 90g',
                          unit_price=499,
                          units_stored=90,
                          units_min=80)
        Product.add_to_db(name='Sabonete Dove 90g',
                          unit_price=350,
                          units_stored=100,
                          units_min=90)
        Product.add_to_db(name='Shampoo Pantene 400ml',
                          unit_price=1490,
                          units_stored=60,
                          units_min=50)
        Product.add_to_db(name='Desodorante Rexona Aerosol 150ml',
                          unit_price=999,
                          units_stored=70,
                          units_min=60)

        Product.add_to_db(name='Pneu Continental Aro 14',
                          unit_price=37490,
                          units_stored=4,
                          units_min=2)
