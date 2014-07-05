package parsing.lfg

sealed abstract class Equation[ID <: Identifier] {
  def negation: Equation[ID] = this match {
    case Compound(ce) => Compound(ce.negation)
    case Defining(de) => Constraint(de.negation)
    case Constraint(ce) => Constraint(ce.negation)
  }
  def ground(up: AbsoluteIdentifier, down: AbsoluteIdentifier)
    (implicit evidence: ID <:< RelativeIdentifier): Equation[AbsoluteIdentifier] = this match {
    case Compound(ce) => Compound(ce.ground(up, down))
    case Defining(de) => Defining(de.ground(up, down))
    case Constraint(ce) => Constraint(ce.ground(up, down))
  }
}

case class Compound[ID <: Identifier](
  equation: CompoundEquation[ID])
  extends Equation[ID]
case class Defining[ID <: Identifier](
  equation: DefiningEquation[ID])
  extends Equation[ID]
case class Constraint[ID <: Identifier](
  equation: ConstraintEquation[ID])
  extends Equation[ID]

sealed abstract class CompoundEquation[ID <: Identifier] {
  def negation: CompoundEquation[ID] = this match {
    case Disjunction(l, r) => Conjunction(l.negation, r.negation)
    case Conjunction(l, r) => Disjunction(l.negation, r.negation)
  }
  def ground(up: AbsoluteIdentifier, down: AbsoluteIdentifier)
    (implicit evidence: ID <:< RelativeIdentifier): CompoundEquation[AbsoluteIdentifier] = this match {
    case Disjunction(l, r) => Disjunction(l.ground(up, down), r.ground(up, down))
    case Conjunction(l, r) => Conjunction(l.ground(up, down), r.ground(up, down))
  }
}
case class Disjunction[ID <: Identifier](
  left: Equation[ID], right: Equation[ID])
  extends CompoundEquation[ID]
case class Conjunction[ID <: Identifier](
  left: Equation[ID], right: Equation[ID])
  extends CompoundEquation[ID]

sealed abstract class DefiningEquation[ID <: Identifier] {
  def negation: ConstraintEquation[ID] = this match {
    case Assignment(left, right) => Equals(false, left, right)
    case Containment(elem, cont) => Contains(false, elem, cont)
  }
  def ground(up: AbsoluteIdentifier, down: AbsoluteIdentifier)
    (implicit evidence: ID <:< RelativeIdentifier): DefiningEquation[AbsoluteIdentifier] = this match {
    case Assignment(l, r) => Assignment(l.ground(up, down), r.ground(up, down))
    case Containment(e, c) => Containment(e.ground(up, down), c.ground(up, down))
  }
}
case class Assignment[ID <: Identifier](
  left: Expression[ID], right: Expression[ID])
  extends DefiningEquation[ID]
case class Containment[ID <: Identifier](
  element: Expression[ID], container: Expression[ID])
  extends DefiningEquation[ID]

sealed abstract class ConstraintEquation[ID <: Identifier] {
  def negation: ConstraintEquation[ID] = this match {
    case Equals(pos, l, r) => Equals(!pos, l, r)
    case Contains(pos, e, c) => Contains(!pos, e, c)
    case Exists(pos, e) => Exists(!pos, e)
  }
  def ground(up: AbsoluteIdentifier, down: AbsoluteIdentifier)
    (implicit evidence: ID <:< RelativeIdentifier): ConstraintEquation[AbsoluteIdentifier] = this match {
    case Equals(pos, l, r) => Equals(pos, l.ground(up, down), r.ground(up, down))
    case Contains(pos, e, c) => Contains(pos, e.ground(up, down), c.ground(up, down))
    case Exists(pos, e) => Exists(pos, e.ground(up, down))
  }
}
case class Equals[ID <: Identifier](
  pos: Boolean, left: Expression[ID], right: Expression[ID])
  extends ConstraintEquation[ID]
case class Contains[ID <: Identifier](
  pos: Boolean, element: Expression[ID], container: Expression[ID])
  extends ConstraintEquation[ID]
case class Exists[ID <: Identifier](
  pos: Boolean, exp: Expression[ID])
  extends ConstraintEquation[ID]
