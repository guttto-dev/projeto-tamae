import os

from flask import (
        Flask,
        g,
        render_template,
        request,
        session,
        flash
        )
from flask_babel import Babel, _
from flask_login import LoginManager, current_user

from config import Config
from models.user import User, AccessLevel, LanguageConfig
from util import db, bcrypt

app = Flask(__name__)
app.config.from_object(Config)
login_manager = LoginManager()
login_manager.init_app(app)
bcrypt.init_app(app)
db.init_app(app)
babel = Babel(app)

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


@app.context_processor
def inject_on_templates():
    lang_config = LanguageConfig.query.first()
    return dict(get_locale=get_locale,
                current_lang=lang_config.lang,
                languages=Config.LANGUAGES,
                AccessLevel=AccessLevel)


@app.errorhandler(401)
def page_not_found(e):
    return render_template('error.html', page_title=_('Error: Unauthorized')), 401


@app.errorhandler(404)
def page_not_found(e):
    return render_template('error.html', page_title=_('Error: Page Not Found')), 404


@app.errorhandler(500)
def internal_error(e):
    db.session.rollback()
    return render_template('error.html', page_title=_('Error: Internal Server')), 500


def get_locale():
    lang_config = LanguageConfig.query.first()
    if lang_config:
        return lang_config.lang
    return Config.DEFAULT_LANGUAGE
babel.init_app(app, locale_selector=get_locale)


with app.app_context():
    if not os.path.exists(f'{app.instance_path}/{Config.DATABASE_PATH}'):
        db.create_all()
        if Config.DEBUG:
            import db_init_example
    if not LanguageConfig.query.first():
        db.session.add(LanguageConfig(lang=Config.DEFAULT_LANGUAGE))
        db.session.commit()
