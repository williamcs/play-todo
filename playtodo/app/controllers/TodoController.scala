package controllers

import javax.inject.Inject
import models.Todo
import models.JsonFormats._
import repositories.{TodoRepositoryImpl}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import reactivemongo.bson.BSONObjectID

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * https://github.com/ricsirigu/play26-swagger-reactivemongo/blob/master/app/controllers/TodoController.scala
  */
class TodoController @Inject()(cc: ControllerComponents, todoRepo: TodoRepositoryImpl) extends AbstractController(cc) {

  def getAllTodos = Action.async {
    todoRepo.getAll().map { todos =>
      Ok(Json.toJson(todos))
    }
  }

  def getTodo(todoId: BSONObjectID) = Action.async {
    todoRepo.getTodo(todoId).map { maybeTodo =>
      maybeTodo.map { todo =>
        Ok(Json.toJson(todo))
      }.getOrElse(NotFound)
    }
  }

  def createTodo() = Action.async(parse.json) { req =>
    req.body.validate[Todo].map { todo =>
      todoRepo.addTodo(todo).map { todo =>
        //Created
        Ok(Json.toJson(todo))
      }
    }.getOrElse(Future.successful(BadRequest("Invalid Todo format")))
  }

  def updateTodo(todoId: BSONObjectID) = Action.async(parse.json) { req =>
    req.body.validate[Todo].map { todo =>
      todoRepo.updateTodo(todoId, todo).map {
        case Some(todo) => Ok(Json.toJson(todo))
        case _ => NotFound
      }
    }.getOrElse(Future.successful(BadRequest("Invalid Json")))
  }

  def deleteTodo(todoId: BSONObjectID) = Action.async { req =>
    todoRepo.deleteTodo(todoId).map {
      case Some(todo) => Ok(Json.toJson(todo))
      case _ => NotFound
    }
  }
}
