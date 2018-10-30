import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import models.JsonFormats._
import models.Todo
import org.scalatest.BeforeAndAfter

import play.api.libs.json.{ Json, JsObject }
import play.api.test.FakeRequest
import play.api.test.Helpers._

import reactivemongo.bson.BSONDocument
import reactivemongo.play.json.collection.JSONCollection
import reactivemongo.play.json._

class TodoSpec extends PlayWithMongoSpec with BeforeAndAfter {

  var todos: Future[JSONCollection] = _

  before {
    // init DB
    await {
      todos = reactiveMongoApi.database.map(_.collection("tododos"))

      todos.flatMap(_.insert[Todo](ordered = false).many(List(
        Todo(_id = None, title = "Test todo 1", completed = Some(false)),
        Todo(_id = None, title = "Test todo 2", completed = Some(true)),
        Todo(_id = None, title = "Test todo 3", completed = Some(false)),
        Todo(_id = None, title = "Test todo 4", completed = Some(true))
      )))
    }
  }

  after {
    // clean DB
    todos.flatMap(_.drop(failIfNotFound = false))
  }

  "Get all Todos" in {
    val Some(result) = route(app, FakeRequest(GET, "/todos"))
    val resultList = contentAsJson(result).as[List[Todo]]

    resultList.length mustEqual 4
    status(result) mustBe OK
  }

  "Add a Todo" in {
    val payload = Todo(_id = None, title = "Test newly added todo", completed = Some(true))
    val Some(result) = route(app, FakeRequest(POST, "/todos").withJsonBody(Json.toJson(payload)))

    status(result) mustBe OK
  }

  "Delete a Todo" in {
    val query = BSONDocument()
    val Some(todoToDelete) = await(todos.flatMap(
      _.find(query, Option.empty[JsObject]).one[Todo]
    ))

    val todoIdToDelete = todoToDelete._id.get.stringify
    val Some(result) = route(app, FakeRequest(DELETE, s"/todos/$todoIdToDelete"))

    status(result) mustBe OK
  }

  "Update a Todo" in {
    val query = BSONDocument()
    val payload = Json.obj(
      "title" -> "Todo updated"
    )
    val Some(todoUpdate) = await(todos.flatMap(
      _.find(query, Option.empty[BSONDocument]).one[Todo]
    ))

    val todoIdToUpdate = todoUpdate._id.get.stringify
    val Some(result) = route(app, FakeRequest(PATCH, s"/todos/$todoIdToUpdate").withJsonBody(payload))
    val updatedTodo = contentAsJson(result).as[Todo]

    updatedTodo.title mustEqual "Todo updated"
    status(result) mustBe OK
  }

}
