package org.menthal.model.graph

import org.menthal.{Session, Lock, Unlock, AppSessionFragment}
import com.twitter.algebird._
import com.twitter.algebird.Operators._

/**
 * Created by pascal on 22.08.14.
 */
class Graph() {
  var vertices = Set[String]()
  var edges = Map[(String, String), Int]()
  var totalWeight = 0

  /**
   * Add a weighted edge between two vertices v, w.
   * If one or both vertices do not exist, add them.
   * If there is already an edge between v and w, increase
   * its weight by one.
   */
  def addEdge(v: String, w: String) {
    vertices += v
    vertices += w
    edges += Map((v, w) -> 1) // map algebra: if (v,w) already exists, 1 will be added to the old value
    totalWeight += 1
  }

  /**
   * Add an edge between vertices that correspond to AppSessionFragments in the following way:
   *
   * @param a
   * @param b
   */
  def addEdge(a: AppSessionFragment, b: AppSessionFragment) {
    val as = a match {
      case u: Unlock => "Unlock"
      case l: Lock => "Lock"
      case s: Session => s.app.getOrElse("UnknownApp")
    }
    val bs = a match {
      case u: Unlock => "Unlock"
      case l: Lock => "Lock"
      case s: Session => s.app.getOrElse("UnknownApp")
    }
    addEdge(as, bs)
  }

  def union(g: Graph): Graph = {
    if (this == None) return g
    if (g == None) return this

    this.vertices += g.vertices // set algebra
    this.edges += g.edges // map algebra

    return this
  }
}
