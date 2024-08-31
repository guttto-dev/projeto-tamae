import os

from flask import (
        Flask,
        g,
        render_template,
        request,
        redirect,
        url_for,
        session,
        flash)
from flask_login import (
        UserMixin,
        LoginManager,
        login_user,
        logout_user,
        login_required,
        current_user)
from flask_bcrypt import Bcrypt

from models.product import Product
from util import db

DATABASE = 'store.sqlite'

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///' + DATABASE
app.config['SECRET_KEY'] = 'supersecret'
login_manager = LoginManager()
login_manager.init_app(app)
bcrypt = Bcrypt(app)
db.init_app(app)

DATABASE = f'{app.instance_path}/{DATABASE}'

def register_blueprints():
    from routes.product import product
    from routes.client import client
    from routes.occurrence import occurrence
    app.register_blueprint(product)
    app.register_blueprint(client)
    app.register_blueprint(occurrence)
register_blueprints()

class User(UserMixin, db.Model):
    __tablename__ = 'User'

    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.Text, unique=True, nullable=False)
    password = db.Column(db.Text, nullable=False)
    access_level = db.Column(db.Text, nullable=False) # 'adm', 'man' or 'op'
    occurrences = db.relationship('Occurrence', backref='user')

    def allowed(self, access_levels):
        return self.access_level in access_levels

    def __repr__(self):
        return f'<User {self.username}>'


@login_manager.user_loader
def load_user(user_id):
    return User.query.get(user_id)


@app.before_request
def before_request():
    g.current_user = current_user
    g.first_user = User.query.count() == 0
    g.current_route = request.endpoint


@app.route('/')
def start_page():
    if g.first_user:
        flash('No users found, create a user admin.', 'warn')
        return redirect(url_for('register_page'))
    elif not g.current_user.is_authenticated:
        flash('User is not autenticated, please login.', 'info')
        return redirect(url_for('login_page'))
    if g.current_user.access_level == 'adm':
        users = User.query.all()
        return render_template('index-admin.html', page_title='Admin page', users=users)
    else:
        return render_template('index.html', page_title='Start page')


@app.route('/register', methods=['GET', 'POST'])
def register_page():
    if not g.first_user and (not hasattr(g.current_user, 'access_level') or g.current_user.access_level != 'adm'):
        flash('You do not have permission to access this page.', 'error')
        return redirect(url_for('error_page'))

    if request.method == 'POST':
        username = request.form['username']
        password = request.form['password']
        access_level = request.form['access_level']

        if not username or not password or not access_level:
            flash('All fields are required.', 'error')
            return redirect(url_for('register_page'))
        if User.query.filter_by(username=username).first():
            flash('Username already exists.', 'error')
            return redirect(url_for('register_page'))

        hashed_password = bcrypt.generate_password_hash(password).decode('utf-8')
        new_user = User(username=username, password=hashed_password, access_level=access_level)
        db.session.add(new_user)
        db.session.commit()
        if g.first_user:
            g.first_user = False
            login_user(new_user)
        flash(f'User {username} has been successfully registered!', 'info')
        return redirect(url_for('start_page'))
    return render_template('register.html', page_title='User registration')


@app.route('/delete-user/<int:id>')
@login_required
def delete_user(id):
    if g.current_user.access_level != 'adm':
        flash('You do not have permission to access this page.', 'error')
        return redirect(url_for('error_page'))
    user = User.query.get_or_404(id)
    db.session.delete(user)
    db.session.commit()
    flash('User deleted successfully.', 'info')
    return redirect(url_for('start_page'))


@app.route('/login', methods=['GET', 'POST'])
def login_page():
    if request.method == 'POST':
        username = request.form['username']
        password = request.form['password']
        user = User.query.filter_by(username=username).first()
        if not username or not password:
            flash('Both username and password are required.', 'error')
            return redirect(url_for('login_page'))
        if user and bcrypt.check_password_hash(user.password, password):
            login_user(user)
            flash(f'Login successful!', 'info')
            return redirect(url_for('start_page'))
        else:
            flash('Invalid username or password.', 'error')
            return redirect(url_for('login_page'))
    return render_template('login.html', page_title='User login')


@app.route('/logout')
@login_required
def logout_page():
    flash(f'User {g.current_user.username} has been logged out.', 'info')
    logout_user()
    return redirect(url_for('login_page'))


@app.route('/error')
def error_page():
    error_message = session.pop('error_message', '')
    return render_template('error.html', page_title='Error', error_message=error_message)


with app.app_context():
    if os.path.exists(DATABASE):
        db.create_all()
    else:
        db.create_all()
        products = [
                Product(name='Abacaxi',
                        category_id=None,
                        unit='kg',
                        unit_price=389,
                        units_stored=50,
                        units_min=48,
                        units_sold=0),
                Product(name='Abacate',
                        category_id=None,
                        unit='kg',
                        unit_price=650,
                        units_stored=20,
                        units_min=10,
                        units_sold=0),
                Product(name='Pneu Continental Aro 14',
                        category_id=None,
                        unit='each',
                        unit_price=37490,
                        units_stored=4,
                        units_min=2,
                        units_sold=0),
                ]
        db.session.add_all(products)
        db.session.commit()
