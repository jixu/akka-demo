package com.yahoo.slingstone.demo

import akka.actor._
import akka.routing.{Broadcast, RoundRobinRouter}
import scala.io.Source
import akka.pattern.gracefulStop
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global

case class Line(line: String)

class LinePrinter extends Actor {
  def receive = {
    case Line(line) => println(line)
  }
}

object AkkaDemo extends App {
  val system = ActorSystem("akkademo")
  val router = system.actorOf(Props[LinePrinter].withRouter(RoundRobinRouter(3)), "router")

  // read file line by line
  for (line <- Source.fromFile(args(0)).getLines()) {
    router ! Line(line)
  }
  val terminate = gracefulStop(router, 2.seconds, Broadcast(PoisonPill))
  terminate onComplete { _ =>
    system.shutdown()
  }
}
