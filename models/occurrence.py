from util import db


class Occurrence(db.Model):
    __tablename__ = 'Occurrence'

    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.Integer,
                        db.ForeignKey('User.id'),
                        nullable=True)
    type = db.Column(db.Text, nullable=False) # 'int' or 'erp'
    text = db.Column(db.Text, nullable=False)
    is_solved = db.Column(db.Boolean, nullable=False)

    def __repr__(self):
        return f'<Occurrence id={self.id} user_id={self.user_id}>'
