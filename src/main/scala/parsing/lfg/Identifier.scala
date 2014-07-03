package parsing.lfg

sealed trait Identifier

sealed abstract class RelativeIdentifier extends Identifier
case object Up extends RelativeIdentifier
case object Down extends RelativeIdentifier

case class AbsoluteIdentifier(id: String) extends Identifier
