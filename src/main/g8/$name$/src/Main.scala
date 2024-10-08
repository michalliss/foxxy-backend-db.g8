package $name$

import foxxy.backend.*
import foxxy.repo.*
import foxxy.shared.{BadRequest, BaseEndpoints}
import sttp.tapir.generic.auto.*
import sttp.tapir.json.zio.*
import sttp.tapir.ztapir.*
import zio.*
import zio.logging.consoleLogger
import zio.logging.slf4j.bridge.Slf4jBridge

import javax.sql.DataSource

object Main extends ZIOAppDefault {

  val helloEndpoint = BaseEndpoints.publicEndpoint.get.in("hello").out(jsonBody[String])
  val todosEndpoint = BaseEndpoints.publicEndpoint.get.in("todos").out(jsonBody[List[(String, String)]])

  case class App(schema: Schema, repository: Repository, backend: Backend, migration: Database.Migration) {

    val helloHandler: FoxxyServerEndpoint = helloEndpoint.zServerLogic(_ => ZIO.succeed("Hello, World!"))

    val todosHandler: FoxxyServerEndpoint = todosEndpoint.zServerLogic(_ =>
      repository.allTodos
        .map(_.map(todo => (todo.text, todo.completed.toString)))
        .orElseFail(BadRequest("No todos found"))
    )

    val run = for {
      _ <- migration.reset
      _ <- migration.migrate

      alice <- repository.addUser(Schema.UserDB(java.util.UUID.randomUUID, "Alice"))
      bob   <- repository.addUser(Schema.UserDB(java.util.UUID.randomUUID, "Bob"))

      aliceTodo  <- repository.addTodoItem(Schema.TodoItemDB(java.util.UUID.randomUUID, alice.id, "Alice's first todo", completed = false))
      aliceTodo2 <- repository.addTodoItem(Schema.TodoItemDB(java.util.UUID.randomUUID, alice.id, "Alice's second todo", completed = false))

      bobTodo  <- repository.addTodoItem(Schema.TodoItemDB(java.util.UUID.randomUUID, bob.id, "Bob's first todo", completed = false))
      bobTodo2 <- repository.addTodoItem(Schema.TodoItemDB(java.util.UUID.randomUUID, bob.id, "Bob's second todo", completed = false))

      _ <- backend.serve(List(helloHandler, todosHandler))
    } yield ()
  }

  def configurableLogic = ZIO
    .serviceWithZIO[App](_.run)
    .provideSome[DataSource & BackendConfig](
      ZLayer.derive[App],
      Backend.live,
      Schema.live,
      Repository.live,
      Database.postgres,
      Database.Migration.live
    )
    .provideSomeLayer(Slf4jBridge.init())

  def run = configurableLogic.provide(BackendConfig.withPort(8080), Database.postgresFromEnv)
}
