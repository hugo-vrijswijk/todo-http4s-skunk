package repository

import cats.effect.{IO, Resource}
import fs2.Stream
import model._
import queries.TodoQueries
import skunk._
import skunk.data.Completion.Delete

class TodoRepository(session: Resource[IO, Session[IO]]) extends TodoQueries {

  def getTodos: Stream[IO, Todo] =
    for {
      s  <- Stream.resource(session)
      ps <- Stream.resource(s.prepare(getTodosQuery))
      t  <- ps.stream(Void, 128)
    } yield t

  def getTodo(id: Int): IO[Either[TodoNotFoundError.type, Todo]] = {
    session.use { s =>
      s.prepare(getTodoQuery)
        .use(_.option(id).map {
          case Some(t) => Right(t)
          case None    => Left(TodoNotFoundError)
        })
    }
  }

  def createTodo(newTodo: Todo): IO[Todo] =
    session.use { s =>
      s.prepare(createTodoQuery)
        .use {
          _.unique(newTodo)
            .map(id => newTodo.copy(id = Some(id)))
        }
    }

  def deleteTodo(id: Int): IO[Either[TodoNotFoundError.type, Unit]] = {
    session
      .use { s =>
        s.prepare(deleteTodoQuery)
          .use {
            _.execute(id)
              .map({
                case Delete(1) => Right(())
                case _         => Left(TodoNotFoundError)
              })
          }
      }
  }

  def updateTodo(id: Int, todo: Todo): IO[Either[TodoNotFoundError.type, Todo]] = {
    session
      .use { s =>
        s.prepare(updateTodoQuery)
          .use {
            _.unique(id, todo)
              .redeem(_ => Left(TodoNotFoundError), id => Right(todo.copy(id = Some(id))))
          }
      }
  }
}
