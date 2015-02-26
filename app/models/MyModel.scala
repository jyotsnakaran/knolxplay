package models

import scala.slick.driver.PostgresDriver.simple._
import play.api.db.slick.Profile
import java.util.Date
import java.text.SimpleDateFormat
import scala.slick.lifted.ProvenShape

/**
 * @author knoldus
 *
 */
object MyModel {

  implicit lazy val util2sqlDateMapper = MappedColumnType.base[java.util.Date, java.sql.Date](
    { utilDate => new java.sql.Date(utilDate.getTime()) },
    { sqlDate => new java.util.Date(sqlDate.getTime()) })

    
  class KnolTable(tag: Tag) extends Table[Knol](tag, "knol") {
    def id: Column[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name: Column[String] = column[String]("name")
    def address: Column[String] = column[String]("address")
    def company: Column[String] = column[String]("company")
    def email: Column[String] = column[String]("email")
    def password: Column[String] = column[String]("password")
    def mobile: Column[Long] = column[Long]("mobile")
    def userType: Column[String] = column[String]("userType")
    def created: Column[Date] = column[Date]("created")
    def updated: Column[Date] = column[Date]("updated")

    def * = (name, address, company, email, password, mobile, userType, created, updated, id) <> (Knol.tupled, Knol.unapply)

    def idx = index("email", (email), unique = true)
  }

  val userTable = TableQuery[KnolTable]
  val db = Database.forURL(url = "jdbc:postgresql://localhost:5432/playslick", user = "postgres", password = "postgres", driver = "org.postgresql.Driver")

/**This method inserts the details of user into the database,
 * and it is called when user registers himself
 * @param userData
 * @return
 */
def insertDetail(userData: Knol) = {
    db.withSession { implicit session =>
      // userTable.ddl.create
      userTable.insert(userData)(session)
    }
  }

  /**
   * This method shows the details of the user from the database
   * @return
   */
  def getDetail: List[Knol] = {
    db.withSession { implicit session =>
      val userList = userTable.sortBy(knol => knol.id.desc).list
      userList
    }
  }

  /**This method updates the user details of the matching columns
   * @param id
   * @param userData
   * @return
   */
  def editDetail(id: Int, userData: Knol) = {
    db.withSession { implicit session =>
      userTable.filter(_.id === id).map(
        x=>(x.name,x.address,x.company,x.email,x.password,x.mobile,x.userType,x.updated)
        ).update((userData.name,userData.address,userData.company,userData.email,userData.password,userData.mobile,userData.userType,userData.updated))
      }
  }

  /**This method finds the user by his id
   * @param id : id of the user
   * @return
   */
  def findById(id: Int) = {
    db.withSession { implicit session =>
      userTable.filter(_.id === id).firstOption
    }
  }

  /**This method finds the user by his email
   * @param email : email of the user
   * @return
   */
  def findUser(email: String) = {
    db.withSession { implicit session =>
      userTable.filter(_.email === email).firstOption
    }
  }

 /**This method authenticates the user by matching the email and password of the user
 * @param email
 * @param password
 * @return
 */
def check(email: String, password: String) = {
    db.withSession { implicit session =>
      userTable.filter(_.email === email).firstOption
      userTable.filter(_.password === password).firstOption
    }
  }
}

case class Knol(name: String, address: String, company: String, email: String, password: String, mobile: Long, userType: String, created: Date, updated: Date, id: Int = 0)
