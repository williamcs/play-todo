package models

/*
https://gist.github.com/NicolaeNMV/80e8edc1682e1b01ffcd
import reactivemongo.play.json._
should be imported, otherwise it will get the below error:

No instance of play.api.libs.json.Format is available for scala.Option[reactivemongo.bson.BSONObjectID]
in the implicit scope (Hint: if declared in the same file, make sure it's declared before)
 */

import reactivemongo.play.json._

import play.api.libs.json._
import reactivemongo.bson.{BSONObjectID}

case class Todo(_id: Option[BSONObjectID], title: String, completed: Option[Boolean])

object JsonFormats {

  implicit val todoForma: OFormat[Todo] = Json.format[Todo]
}