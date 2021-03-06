package bootstrap.liftweb

import net.liftweb.util._
import net.liftweb.http._
import net.liftweb.sitemap._
import net.liftweb.sitemap.Loc._
import Helpers._
import net.liftweb.mapper._
import java.sql.{Connection, DriverManager}
import vinod.model._
 
/**
  * A class that's instantiated early and run.  It allows the application
  * to modify lift's environment
  */
class Boot {
  def boot {
    DB.defineConnectionManager(DefaultConnectionIdentifier, DBVendor)
    LiftServlet.addToPackages("vinod")
     
    Schemifier.schemify(true, Log.infoF _, User, Entry)
    LiftServlet.addTemplateBefore(User.templates) // LiftNote 5
    
    LiftServlet.localeCalculator = r => User.currentUser.map(_.locale.isAsLocale).openOr(LiftServlet.defaultLocaleCalculator(r))

    // Build SiteMap
    val entries = Menu(Loc("Home", "/", "Home")) :: 
    Menu(Loc("Request Details", "/request", "Request Details")) :: 
    User.sitemap ::: Entry.sitemap

    LiftServlet.setSiteMap(SiteMap(entries:_*))
    S.addAround(User.requestLoans)    
  }
}

object DBVendor extends ConnectionManager {
  def newConnection(name: ConnectionIdentifier): Can[Connection] = {
    try {
      Class.forName("org.apache.derby.jdbc.EmbeddedDriver")
      val dm = DriverManager.getConnection("jdbc:derby:lift_example;create=true")
      Full(dm)
    } catch {
      case e : Exception => e.printStackTrace; Empty
    }
  }
  def releaseConnection(conn: Connection) {conn.close}
}
