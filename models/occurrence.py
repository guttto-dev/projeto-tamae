from enum import Enum

from util import db


class OccType(Enum):
    INTERNAL = 'int'
    ERP = 'erp'

    @classmethod
    def _missing_(cls, value):
        value = value.lower()
        for member in cls:
            if member.value == value:
                return member
        return None

    def __str__(self):
        return str(self.value).upper()


class Occurrence(db.Model):
    __tablename__ = 'Occurrence'

    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.Integer,
                        db.ForeignKey('User.id'),
                        nullable=True)
    type = db.Column(db.Enum(OccType), nullable=False)
    text = db.Column(db.Text, nullable=False)
    is_solved = db.Column(db.Boolean, nullable=False)

    def __repr__(self):
        return f'<Occurrence id={self.id} type={self.type} user_id={self.user_id}>'
