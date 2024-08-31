from flask import g, Blueprint, render_template, request, redirect, url_for, flash, jsonify

from sqlalchemy import desc

from models.occurrence import Occurrence
from util import db, requires_access_level

occurrence = Blueprint('occurrence', __name__, url_prefix='/occurrence')


@occurrence.route('/')
@requires_access_level(['man'])
def index():
    occurrences = Occurrence.query.order_by(Occurrence.is_solved, desc(Occurrence.id)).all()
    return render_template('occurrence/occurrences.html', occurrences=occurrences, page_title='Occurrences')


@occurrence.route('/add', methods=['GET', 'POST'])
@requires_access_level(['adm', 'man', 'op'])
def add_page():
    if request.method == 'POST':
        user_id = None
        if 'identify_user' in request.form:
            user_id = g.current_user.id
        type = request.form['type']
        text = request.form['text']
        occurrence = Occurrence(user_id=user_id,
                                type=type,
                                text=text,
                                is_solved=False)
        db.session.add(occurrence)
        db.session.commit()
        flash('Occurrence added successfully.', 'info')
        return redirect(request.url)
    return render_template('occurrence/add.html', page_title='Add new occurrence')


@occurrence.route('/resolve/<int:id>')
@requires_access_level(['man'])
def resolve_page(id):
    occurrence = Occurrence.query.get_or_404(id)
    occurrence.is_solved = True
    db.session.commit()
    flash(f'Occurrence {id} is marked as resolved.', 'info')
    return redirect(url_for('occurrence.index'))


@occurrence.route('/read/<int:id>')
@requires_access_level(['man'])
def read_page(id):
    occurrence = Occurrence.query.get_or_404(id)
    return occurrence.text
