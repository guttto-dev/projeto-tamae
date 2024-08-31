from util import db


class Product(db.Model):
    __tablename__ = 'Product'

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.Text, unique=True, nullable=False)
    category_id = db.Column(db.Integer,
                            db.ForeignKey('ProductCategory.id'),
                            nullable=True)
    unit = db.Column(db.Text, nullable=False) # 'each' or 'kg'
    unit_price = db.Column(db.Integer, nullable=False)
    units_stored = db.Column(db.Integer, nullable=False)
    units_min = db.Column(db.Integer, nullable=False)
    units_sold = db.Column(db.Integer, nullable=False)

    def __repr__(self):
        return f'<Product id={self.id} name={self.name}>'


class ProductCategory(db.Model):
    __tablename__ = 'ProductCategory'

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.Text, unique=True, nullable=False)
    products = db.relationship('Product', backref='category')

    def __repr__(self):
        return f'<ProductCategory id={self.id} name={self.name}>'


class ProductOrder(db.Model):
    __tablename__ = 'ProductOrder'

    id = db.Column(db.Integer, primary_key=True)
    client_id = db.Column(db.Integer,
                           db.ForeignKey('Client.id'),
                           nullable=True)
    value = db.Column(db.Integer, nullable=False)
    is_paid = db.Column(db.Boolean, nullable=False)
    datetime = db.Column(db.DateTime, nullable=False)
    products = db.relationship('ProductTransaction', backref='order')

    def __repr__(self):
        return f'<ProductOrder id={self.id} client_id={self.client_id}>'


class ProductTransaction(db.Model):
    __tablename__ = 'ProductTransaction'

    id = db.Column(db.Integer, primary_key=True)
    product_id = db.Column(db.Integer,
                           db.ForeignKey('Product.id'),
                           nullable=False)
    order_id = db.Column(db.Integer,
                           db.ForeignKey('ProductOrder.id'),
                           nullable=True)
    unit_price = db.Column(db.Integer, nullable=False)
    units = db.Column(db.Integer, nullable=False)

    def __repr__(self):
        return f'<ProductTransaction id={self.id} product_id={self.product_id} order_id={self.order_id}>'
