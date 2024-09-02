from util import db


class Client(db.Model):
    __tablename__ = 'Client'

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.Text, unique=False, nullable=True)
    phone_number = db.Column(db.Text, unique=True, nullable=True)
    is_deleted = db.Column(db.Boolean, nullable=False)
    orders = db.relationship('ProductOrder', backref='client')

    def __repr__(self):
        return f'<Client id={self.id} name={self.name}>'
