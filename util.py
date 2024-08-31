from functools import wraps

from flask_sqlalchemy import SQLAlchemy

db = SQLAlchemy()

from flask import redirect, url_for, flash

from flask_login import (
        login_required,
        current_user)


def requires_access_level(access_levels):
    def decorator(f):
        @login_required
        @wraps(f)
        def decorated_function(*args, **kwargs):
            if not current_user.allowed(access_levels):
                flash('You do not have permission to access that page.', 'error')
                return redirect(url_for('start_page'))
            return f(*args, **kwargs)
        return decorated_function
    return decorator
