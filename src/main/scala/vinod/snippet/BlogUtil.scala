package vinod.snippet

import net.liftweb.util._
import net.liftweb.http._
import net.liftweb.mapper._
import net.liftweb.util.Helpers._
import net.liftweb.sitemap._
import scala.xml._
import vinod.model.Entry
import vinod.model.User

class BlogUtil {
  def entry = (new Entry).author(User.currentUser).toForm(Full("Post"), 
				 (t: Entry) => { 
				   t.save
				   S.redirectTo("/view?id=" + t.id)})

  def viewentry(xhtml : Group) : NodeSeq = {
    val t = Entry.find(S.param("id"))
    t.map(t => 
      bind("entry", xhtml,
	   'name -> t.title,
	   'body -> t.body)) openOr <span>Not found!</span>
  }

  def _entryview(e : Entry) : Node = {
    <div>
    <strong>{e.title}</strong><br />
    <span>{e.body}</span>
    </div>
  }

  def viewblog(xhtml : Group) : NodeSeq = {
    // Find all Entries by author using the parameter 
    val t = Entry.findAll(By(Entry.author, toLong(S.param("id"))), 
			OrderBy(Entry.id, false), MaxRows(20))
    t match {
      // If no 'id' was requested, then show a listing of all users.
      case Nil => User.findAll().map(u => <span><a href={"/blog?id=" + u.id}> 
				      {u.firstName + " " + u.lastName}</a>
				      <br /></span>)
      case entries => entries.map(e => _entryview(e))
    }
  }

  def requestDetails: NodeSeq = {
    <span>
    <p>
    Request's Locale: {S.locale}
    </p>
    <p>
    Request(User): Locale : {User.currentUser.map(ignore => S.locale.toString).openOr("No User logged in.")}
    </p>
    </span>
  }
}
