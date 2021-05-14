package queries

import model._
import skunk._
import skunk.implicits._

trait TodoQueries extends _root_.codec.TodoCodec {

  val getTodosQuery: Query[Void, Todo] = sql"SELECT id, description, importance FROM todo".query(todo)

  val getTodoQuery: Query[Int, Todo] = sql"SELECT id, description, importance FROM todo WHERE id = $id".query(todo)

  val createTodoQuery: Query[Todo, Int] =
    sql"INSERT INTO todo (description, importance) VALUES ($description,  $importance) RETURNING id"
      .query(id)
      .contramap { case Todo(_, d, i) =>
        d ~ i
      }

  val deleteTodoQuery: Command[Int] = sql"DELETE FROM todo WHERE id = $id".command

  val updateTodoQuery: Query[Int ~ Todo, Int] =
    sql"UPDATE todo SET description = $description, importance = $importance WHERE id = $id RETURNING id"
      .query(id)
      .contramap { case id ~ Todo(_, d, i) =>
        d ~ i ~ id
      }
}
