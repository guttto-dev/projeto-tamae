from flask import Blueprint, render_template, request, redirect, url_for, flash

from sqlalchemy import desc

from models.client import Client
from util import db, requires_access_level

client = Blueprint('client', __name__, url_prefix='/client')


@client.route('/')
@requires_access_level(['man', 'op'])
def index():
    clients = Client.query.order_by(desc(Client.id)).all()
    return render_template('client/clients.html', clients=clients, page_title='Clients')


@client.route('/add', methods=['GET', 'POST'])
@requires_access_level(['man', 'op'])
def add_page():
    if request.method == 'POST':
        name = request.form['name']
        phone_number = request.form['phone_number'] or None
        if phone_number and Client.query.filter_by(phone_number=phone_number).first():
            flash('Another user already has this phone number.', 'error')
            return redirect(url_for('client.add_page'))
        client = Client(name=name,
                        phone_number=phone_number,
                        is_deleted=False)
        db.session.add(client)
        db.session.commit()
        flash('Client added successfully.', 'info')
        return redirect(url_for('client.index'))
    return render_template('client/add.html', page_title='Add new client')


@client.route('/update/<int:id>', methods=['GET', 'POST'])
@requires_access_level(['man', 'op'])
def update_page(id):
    client = Client.query.get_or_404(id)
    if request.args.get('delete'):
        client.name = client.phone_number = None
        client.is_deleted = True
        db.session.commit()
        flash('Client deleted successfully.', 'info')
        return redirect(url_for('client.index'))

    if request.method == 'POST':
        name = request.form['name']
        phone_number = request.form['phone_number']
        if phone_number != client.phone_number and Client.query.filter_by(phone_number=phone_number).first():
            flash('Another user already has this phone number.', 'error')
            return redirect(url_for('client.add_page'))
        client.name = name
        client.phone_number = phone_number
        db.session.commit()
        flash('Client updated successfully.', 'info')
        return redirect(url_for('client.index'))

    return render_template('client/update.html', client=client, page_title=f'Update client "{client.name}"')
