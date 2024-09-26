import os


class Config:
    DATABASE_PATH = 'store.sqlite'

    DEBUG = True
    TESTING = False
    SQLALCHEMY_DATABASE_URI = 'sqlite:///' + DATABASE_PATH
    SECRET_KEY = os.getenv('SECRET_KEY', 'default_secret_key')
    SESSION_COOKIE_SAMESITE='Lax'

    LANGUAGES = ['en', 'pt']
    DEFAULT_LANGUAGE = 'en'
