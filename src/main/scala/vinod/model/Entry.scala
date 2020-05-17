package vinod.model

import net.liftweb.mapper._
import net.liftweb.util._
import net.liftweb.sitemap._
import net.liftweb.sitemap.Loc._

object Entry extends Entry with KeyedMetaMapper[Long, Entry] {
  override def dbTableName = "entries"
  // sitemap entry
  val sitemap = List(Menu(Loc("CreateEntry", "/entry", "Create An Entry", If(User.loggedIn_? _, "Please login"))), 
		     Menu(Loc("ViewEntry", "/view", "View An Entry", Hidden)), 
		     Menu(Loc("ViewBlog", "/blog", "View Blog")))
}

class Entry extends KeyedMapper[Long, Entry] {
  def getSingleton = Entry // what's the "meta" server
  def primaryKeyField = id

  // Fields
  object id extends MappedLongIndex(this)
  object author extends MappedLongForeignKey(this, User) {
    override def dbDisplay_? = false
  }
  object title extends MappedString(this, 128)
  object body extends MappedTextarea(this, 32000) { // Lift Note: 7
    override def setFilter = notNull _  :: trim _ :: crop _ :: super.setFilter
  }
}
