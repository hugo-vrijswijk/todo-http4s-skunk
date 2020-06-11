package codec

import model._
import skunk._
import skunk.codec.all._
import skunk.data.Type

trait TodoCodec {
  val id          = int4
  val description = text
  val importance  = Codec.simple[Importance](
    _.value,
    {
      case "high"   => Right(High)
      case "medium" => Right(Medium)
      case "low"    => Right(Low)
      case other    => Left(s"Could not decode $other to Importance, expected one of 'high', 'medium', 'low'")
    },
    Type.text
  )

  val todo: Decoder[Todo] = (id.opt ~ description ~ importance).gmap[Todo]

}
