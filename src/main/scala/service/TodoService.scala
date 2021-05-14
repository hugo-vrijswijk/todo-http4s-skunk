package service

import cats.effect.IO
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.{Decoder, Encoder}
import model.{Importance, Todo, TodoNotFoundError}
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.Location
import org.http4s.{HttpRoutes, Uri}
import repository.TodoRepository

class TodoService(repository: TodoRepository) extends Http4sDsl[IO] {
  implicit private val encodeImportance: Encoder[Importance] = Encoder.encodeString.contramap[Importance](_.value)

  implicit private val decodeImportance: Decoder[Importance] =
    Decoder.decodeString.map[Importance](Importance.unsafeFromString)

  val baseRoute = "todos"
  val routes    = HttpRoutes.of[IO] {
    case GET -> Root / `baseRoute` =>
      Ok(repository.getTodos.map(_.asJson))

    case GET -> Root / `baseRoute` / IntVar(id) =>
      for {
        getResult <- repository.getTodo(id)
        response  <- todoResult(getResult)
      } yield response

    case req @ POST -> Root / `baseRoute` =>
      for {
        todo        <- req.decodeJson[Todo]
        createdTodo <- repository.createTodo(todo)
        response    <- Created(createdTodo.asJson, Location(Uri.unsafeFromString(s"/todos/${createdTodo.id.get}")))
      } yield response

    case req @ PUT -> Root / `baseRoute` / IntVar(id) =>
      for {
        todo         <- req.decodeJson[Todo]
        updateResult <- repository.updateTodo(id, todo)
        response     <- todoResult(updateResult)
      } yield response

    case DELETE -> Root / `baseRoute` / IntVar(id) =>
      repository.deleteTodo(id).flatMap {
        case Left(TodoNotFoundError) => NotFound()
        case Right(_)                => NoContent()
      }
  }

  private def todoResult(result: Either[TodoNotFoundError.type, Todo]) = {
    result match {
      case Left(TodoNotFoundError) => NotFound()
      case Right(todo)             => Ok(todo.asJson)
    }
  }
}
