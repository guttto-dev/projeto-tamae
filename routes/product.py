from flask import Blueprint, render_template, request, redirect, url_for, flash

from sqlalchemy import desc

from models.product import Product, ProductCategory, ProductTransaction
from util import db, requires_access_level

product = Blueprint('product', __name__, url_prefix='/product')


@product.route('/')
@requires_access_level(['man'])
def index():
    products = Product.query.order_by(Product.category_id, desc(Product.units_sold), Product.name).all()
    return render_template('product/products.html', products=products, page_title='Products')


@product.route('/add', methods=['GET', 'POST'])
@requires_access_level(['man'])
def add_page():
    categories = ProductCategory.query.all()
    if request.method == 'POST':
        name = request.form['name']
        if Product.query.filter_by(name=name).first():
            flash(f'Product "{name}" already exists.', 'error')
            return redirect(url_for('product.add_page'))
        category_id = request.form['category_id'] or None
        if category_id:
            category_id = int(category_id)
        product = Product(name=name,
                          category_id=category_id,
                          unit=request.form['unit'],
                          unit_price=int(float(request.form['unit_price']) * 100),
                          units_stored=int(request.form['units_stored']),
                          units_min=int(request.form['units_min']),
                          units_sold=0)
        db.session.add(product)
        db.session.commit()
        product_t = ProductTransaction(product_id=product.id,
                                       order_id=None,
                                       unit_price=product.unit_price,
                                       units=product.units_stored)
        db.session.add(product_t)
        db.session.commit()
        flash('Product added successfully.', 'info')
        return redirect(url_for('product.index'))
    return render_template('product/add.html', categories=categories, page_title='Add new product')


@product.route('/update/<int:id>', methods=['GET', 'POST'])
@requires_access_level(['man'])
def update_page(id):
    product = Product.query.get_or_404(id)
    if request.args.get('delete'):
        related_transactions = ProductTransaction.query.filter_by(product_id=id).delete(synchronize_session=False)
        db.session.delete(product)
        db.session.commit()
        flash('Product deleted successfully.', 'info')
        return redirect(url_for('product.index'))

    categories = ProductCategory.query.all()
    if request.method == 'POST':
        name = request.form['name']
        if name != product.name and Product.query.filter_by(name=name).first():
            flash(f'Product "{name}" already exists.', 'error')
            return redirect(url_for('product.index'))
        category_id = request.form['category_id'] or None
        if category_id:
            category_id = int(category_id)
        product.category_id = category_id
        product.unit = request.form['unit']
        product.unit_price = int(float(request.form['unit_price']) * 100)
        units_stored = int(request.form['units_stored'])
        product.units_min = int(request.form['units_min'])
        product_t = ProductTransaction(product_id=product.id,
                                       order_id=None,
                                       unit_price=product.unit_price,
                                       units=units_stored - product.units_stored)
        product.units_stored = units_stored
        db.session.add(product_t)
        db.session.commit()
        flash('Product updated successfully.', 'info')
        return redirect(url_for('product.index'))

    return render_template('product/update.html', product=product, categories=categories, page_title=f'Update product "{product.name}"')

@product.route('/transactions')
@requires_access_level(['man'])
def transactions_page():
    product_ts = ProductTransaction.query.order_by(desc(ProductTransaction.id)).all()
    return render_template('product/transactions.html', product_ts=product_ts, page_title=f'Stock changes')
