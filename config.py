import os
from datetime import time


class Config:
    DATABASE_PATH = 'store.sqlite'

    DEBUG = True
    TESTING = False
    SQLALCHEMY_DATABASE_URI = 'sqlite:///' + DATABASE_PATH
    SECRET_KEY = os.getenv('SECRET_KEY', 'default_secret_key')
    SESSION_COOKIE_SAMESITE='Lax'

    LANGUAGES = ['en', 'pt', 'es', 'ja']
    DEFAULT_LANGUAGE = 'pt'

    BUSINESS_START_HOUR = time(0, 0)
    BUSINESS_END_HOUR = time(23, 0)
