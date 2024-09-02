from flask import (
        Blueprint,
        render_template,
        request,
        redirect,
        url_for,
        flash
        )

from sqlalchemy import desc

from models.user import AccessLevel
from models.product import Product, ProductCategory, ProductTransaction
from util import db, requires_access_level

product_bp = Blueprint('product', __name__, url_prefix='/product')


@product_bp.route('/')
@requires_access_level(AccessLevel.MANAGER)
def start_page():
    products = Product.query.order_by(Product.category_id, desc(Product.units_sold), Product.name).all()
    for product in products:
        if product.units_stored < product.units_min:
            if product.category:
                flash(f'Product "{product.name}" of category {product.category.name} has less than minimum quantity.', 'warn')
            else:
                flash(f'Product "{product.name}" has less than minimum quantity.', 'warn')
    return render_template('product/products.html', page_title='Products',
                           products=products)


@product_bp.route('/add', methods=['GET', 'POST'])
@requires_access_level(AccessLevel.MANAGER)
def add_page():
    categories = ProductCategory.query.all()
    if request.method == 'POST':
        name = request.form['name']

        if Product.query.filter_by(name=name).first():
            flash(f'Product "{name}" already exists.', 'error')
            return redirect(url_for('.add_page'))

        category_id = request.form['category_id'] or None
        if category_id:
            category_id = int(category_id)

        product = Product.add_to_db(name=name,
                          category_id=category_id,
                          unit_price=int(float(request.form['unit_price']) * 100),
                          units_stored=int(request.form['units_stored']),
                          units_min=int(request.form['units_min']),
                          units_sold=0)
        flash(f'Product #{product.id} added successfully.', 'info')
        return redirect(url_for('.start_page'))

    return render_template('product/add.html', page_title='Add new product',
                           categories=categories)


@product_bp.route('/update/<int:id>', methods=['GET', 'POST'])
@requires_access_level(AccessLevel.MANAGER)
def update_page(id):
    product = Product.query.get_or_404(id)

    categories = ProductCategory.query.all()
    if request.method == 'POST':
        name = request.form['name']

        if name != product.name and Product.query.filter_by(name=name).first():
            flash(f'Product "{name}" already exists.', 'error')
            return redirect(url_for('.start_page'))

        category_id = request.form['category_id'] or None
        if category_id:
            category_id = int(category_id)

        product.category_id = category_id
        product.unit_price = int(float(request.form['unit_price']) * 100)
        units_stored = int(request.form['units_stored'])
        product.units_min = int(request.form['units_min'])
        product_t = ProductTransaction(product_id=product.id,
                                       order_id=None,
                                       unit_price=product.unit_price,
                                       units=units_stored - product.units_stored,
                                       is_valid=True)
        product.units_stored = units_stored
        db.session.add(product_t)
        db.session.commit()
        flash('Product updated successfully.', 'info')
        return redirect(url_for('.start_page'))

    return render_template('product/update.html', page_title=f'Update product "{product.name}"',
                           product=product, categories=categories)


@product_bp.route('/transactions')
@requires_access_level(AccessLevel.MANAGER)
def transactions_page():
    product_ts = ProductTransaction.query.order_by(desc(ProductTransaction.id)).all()
    return render_template('product/transactions.html', page_title=f'Stock changes',
                           product_ts=product_ts)


@product_bp.route('/delete/<int:id>')
@requires_access_level(AccessLevel.MANAGER)
def delete_product(id):
    product = Product.query.get_or_404(id)
    ProductTransaction.query.filter_by(product_id=id).delete(synchronize_session=False)
    db.session.delete(product)
    db.session.commit()
    flash('Product deleted successfully.', 'info')
    return redirect(url_for('.start_page'))
