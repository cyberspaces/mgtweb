package cn.changhong.web.persistent.Tables

import java.util.Date

// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */


/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
object Tables {
  val profile: scala.slick.driver.JdbcProfile=scala.slick.driver.MySQLDriver
  import profile.simple._
  import scala.slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import scala.slick.jdbc.{GetResult => GR}
  
  /** DDL for all tables. Call .create to execute. */
  lazy val ddl = AccessToken.ddl ++ AutoCode.ddl ++ Client.ddl ++ FamilyMember.ddl ++ User.ddl
  
  /** Entity class storing rows of table AccessToken
   *  @param accessToken Database column access_token DBType(VARCHAR), PrimaryKey, Length(100,true)
   *  @param refreshToken Database column refresh_token DBType(VARCHAR), Length(100,true)
   *  @param userId Database column user_id DBType(BIGINT)
   *  @param clientId Database column client_id DBType(VARCHAR), Length(200,true)
   *  @param createdAt Database column created_at DBType(BIGINT)
   *  @param expiresIn Database column expires_in DBType(BIGINT)
   *  @param clientType Database column client_type DBType(INT)
   *  @param tokenType Database column token_type DBType(INT) */
  case class AccessTokenRow(accessToken: String, refreshToken: String, userId: Long, clientId: String, createdAt: Long, expiresIn: Long, clientType: Int, tokenType: Int)
  /** GetResult implicit for fetching AccessTokenRow objects using plain SQL queries */
  implicit def GetResultAccessTokenRow(implicit e0: GR[String], e1: GR[Long], e2: GR[Int]): GR[AccessTokenRow] = GR{
    prs => import prs._
    AccessTokenRow.tupled((<<[String], <<[String], <<[Long], <<[String], <<[Long], <<[Long], <<[Int], <<[Int]))
  }
  /** Table description of table access_token. Objects of this class serve as prototypes for rows in queries. */
  class AccessToken(_tableTag: Tag) extends Table[AccessTokenRow](_tableTag, "access_token") {
    def * = (accessToken, refreshToken, userId, clientId, createdAt, expiresIn, clientType, tokenType) <> (AccessTokenRow.tupled, AccessTokenRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (accessToken.?, refreshToken.?, userId.?, clientId.?, createdAt.?, expiresIn.?, clientType.?, tokenType.?).shaped.<>({r=>import r._; _1.map(_=> AccessTokenRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column access_token DBType(VARCHAR), PrimaryKey, Length(100,true) */
    val accessToken: Column[String] = column[String]("access_token", O.PrimaryKey, O.Length(100,varying=true))
    /** Database column refresh_token DBType(VARCHAR), Length(100,true) */
    val refreshToken: Column[String] = column[String]("refresh_token", O.Length(100,varying=true))
    /** Database column user_id DBType(BIGINT) */
    val userId: Column[Long] = column[Long]("user_id")
    /** Database column client_id DBType(VARCHAR), Length(200,true) */
    val clientId: Column[String] = column[String]("client_id", O.Length(200,varying=true))
    /** Database column created_at DBType(BIGINT) */
    val createdAt: Column[Long] = column[Long]("created_at")
    /** Database column expires_in DBType(BIGINT) */
    val expiresIn: Column[Long] = column[Long]("expires_in")
    /** Database column client_type DBType(INT) */
    val clientType: Column[Int] = column[Int]("client_type")
    /** Database column token_type DBType(INT) */
    val tokenType: Column[Int] = column[Int]("token_type")
    
    /** Foreign key referencing User (database name fk_access_token_user_id) */
    lazy val userFk = foreignKey("fk_access_token_user_id", userId, User)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table AccessToken */
  lazy val AccessToken = new TableQuery(tag => new AccessToken(tag))
  
  /** Entity class storing rows of table AutoCode
   *  @param authorizationCode Database column authorization_code DBType(VARCHAR), PrimaryKey, Length(80,true)
   *  @param userId Database column user_id DBType(BIGINT)
   *  @param redirectUri Database column redirect_uri DBType(VARCHAR), Length(400,true), Default(None)
   *  @param createdAt Database column created_at DBType(INT)
   *  @param scope Database column scope DBType(VARCHAR), Length(200,true)
   *  @param clientId Database column client_id DBType(VARCHAR), Length(200,true)
   *  @param expiresIn Database column expires_in DBType(INT) */
  case class AutoCodeRow(authorizationCode: String, userId: Long, redirectUri: Option[String] = None, createdAt: Int, scope: String, clientId: String, expiresIn: Int)
  /** GetResult implicit for fetching AutoCodeRow objects using plain SQL queries */
  implicit def GetResultAutoCodeRow(implicit e0: GR[String], e1: GR[Long], e2: GR[Option[String]], e3: GR[Int]): GR[AutoCodeRow] = GR{
    prs => import prs._
    AutoCodeRow.tupled((<<[String], <<[Long], <<?[String], <<[Int], <<[String], <<[String], <<[Int]))
  }
  /** Table description of table auto_code. Objects of this class serve as prototypes for rows in queries. */
  class AutoCode(_tableTag: Tag) extends Table[AutoCodeRow](_tableTag, "auto_code") {
    def * = (authorizationCode, userId, redirectUri, createdAt, scope, clientId, expiresIn) <> (AutoCodeRow.tupled, AutoCodeRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (authorizationCode.?, userId.?, redirectUri, createdAt.?, scope.?, clientId.?, expiresIn.?).shaped.<>({r=>import r._; _1.map(_=> AutoCodeRow.tupled((_1.get, _2.get, _3, _4.get, _5.get, _6.get, _7.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column authorization_code DBType(VARCHAR), PrimaryKey, Length(80,true) */
    val authorizationCode: Column[String] = column[String]("authorization_code", O.PrimaryKey, O.Length(80,varying=true))
    /** Database column user_id DBType(BIGINT) */
    val userId: Column[Long] = column[Long]("user_id")
    /** Database column redirect_uri DBType(VARCHAR), Length(400,true), Default(None) */
    val redirectUri: Column[Option[String]] = column[Option[String]]("redirect_uri", O.Length(400,varying=true), O.Default(None))
    /** Database column created_at DBType(INT) */
    val createdAt: Column[Int] = column[Int]("created_at")
    /** Database column scope DBType(VARCHAR), Length(200,true) */
    val scope: Column[String] = column[String]("scope", O.Length(200,varying=true))
    /** Database column client_id DBType(VARCHAR), Length(200,true) */
    val clientId: Column[String] = column[String]("client_id", O.Length(200,varying=true))
    /** Database column expires_in DBType(INT) */
    val expiresIn: Column[Int] = column[Int]("expires_in")
    
    /** Foreign key referencing Client (database name fk_auth_code_client_id) */
    lazy val clientFk = foreignKey("fk_auth_code_client_id", clientId, Client)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing User (database name fk_auth_code_user_id) */
    lazy val userFk = foreignKey("fk_auth_code_user_id", userId, User)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table AutoCode */
  lazy val AutoCode = new TableQuery(tag => new AutoCode(tag))
  
  /** Entity class storing rows of table Client
   *  @param id Database column id DBType(VARCHAR), PrimaryKey, Length(200,true)
   *  @param secret Database column secret DBType(VARCHAR), Length(80,true)
   *  @param redirectUri Database column redirect_uri DBType(VARCHAR), Length(400,true), Default(None)
   *  @param scope Database column scope DBType(VARCHAR), Length(200,true), Default(None) */
  case class ClientRow(id: String, secret: String, redirectUri: Option[String] = None, scope: Option[String] = None)
  /** GetResult implicit for fetching ClientRow objects using plain SQL queries */
  implicit def GetResultClientRow(implicit e0: GR[String], e1: GR[Option[String]]): GR[ClientRow] = GR{
    prs => import prs._
    ClientRow.tupled((<<[String], <<[String], <<?[String], <<?[String]))
  }
  /** Table description of table client. Objects of this class serve as prototypes for rows in queries. */
  class Client(_tableTag: Tag) extends Table[ClientRow](_tableTag, "client") {
    def * = (id, secret, redirectUri, scope) <> (ClientRow.tupled, ClientRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, secret.?, redirectUri, scope).shaped.<>({r=>import r._; _1.map(_=> ClientRow.tupled((_1.get, _2.get, _3, _4)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id DBType(VARCHAR), PrimaryKey, Length(200,true) */
    val id: Column[String] = column[String]("id", O.PrimaryKey, O.Length(200,varying=true))
    /** Database column secret DBType(VARCHAR), Length(80,true) */
    val secret: Column[String] = column[String]("secret", O.Length(80,varying=true))
    /** Database column redirect_uri DBType(VARCHAR), Length(400,true), Default(None) */
    val redirectUri: Column[Option[String]] = column[Option[String]]("redirect_uri", O.Length(400,varying=true), O.Default(None))
    /** Database column scope DBType(VARCHAR), Length(200,true), Default(None) */
    val scope: Column[Option[String]] = column[Option[String]]("scope", O.Length(200,varying=true), O.Default(None))
  }
  /** Collection-like TableQuery object for table Client */
  lazy val Client = new TableQuery(tag => new Client(tag))
  
  /** Entity class storing rows of table FamilyMember
   *  @param userId Database column user_id DBType(BIGINT)
   *  @param sex Database column sex DBType(VARCHAR), Length(10,true), Default(None)
   *  @param age Database column age DBType(INT), Default(None)
   *  @param height Database column height DBType(DOUBLE), Default(None)
   *  @param weight Database column weight DBType(DOUBLE), Default(None)
   *  @param role Database column role DBType(VARCHAR), Length(20,true)
   *  @param created Database column created DBType(BIGINT) */
  case class FamilyMemberRow(userId: Long, sex: Option[String] = None, age: Option[Int] = None, height: Option[Double] = None, weight: Option[Double] = None, role: String,var created: Long=0)
  /** GetResult implicit for fetching FamilyMemberRow objects using plain SQL queries */
  implicit def GetResultFamilyMemberRow(implicit e0: GR[Long], e1: GR[Option[String]], e2: GR[Option[Int]], e3: GR[Option[Double]], e4: GR[String]): GR[FamilyMemberRow] = GR{
    prs => import prs._
    FamilyMemberRow.tupled((<<[Long], <<?[String], <<?[Int], <<?[Double], <<?[Double], <<[String], <<[Long]))
  }
  /** Table description of table Family_Member. Objects of this class serve as prototypes for rows in queries. */
  class FamilyMember(_tableTag: Tag) extends Table[FamilyMemberRow](_tableTag, "Family_Member") {
    def * = (userId, sex, age, height, weight, role, created) <> (FamilyMemberRow.tupled, FamilyMemberRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (userId.?, sex, age, height, weight, role.?, created.?).shaped.<>({r=>import r._; _1.map(_=> FamilyMemberRow.tupled((_1.get, _2, _3, _4, _5, _6.get, _7.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column user_id DBType(BIGINT) */
    val userId: Column[Long] = column[Long]("user_id")
    /** Database column sex DBType(VARCHAR), Length(10,true), Default(None) */
    val sex: Column[Option[String]] = column[Option[String]]("sex", O.Length(10,varying=true), O.Default(None))
    /** Database column age DBType(INT), Default(None) */
    val age: Column[Option[Int]] = column[Option[Int]]("age", O.Default(None))
    /** Database column height DBType(DOUBLE), Default(None) */
    val height: Column[Option[Double]] = column[Option[Double]]("height", O.Default(None))
    /** Database column weight DBType(DOUBLE), Default(None) */
    val weight: Column[Option[Double]] = column[Option[Double]]("weight", O.Default(None))
    /** Database column role DBType(VARCHAR), Length(20,true) */
    val role: Column[String] = column[String]("role", O.Length(20,varying=true))
    /** Database column created DBType(BIGINT) */
    val created: Column[Long] = column[Long]("created")
    
    /** Foreign key referencing User (database name fk_family_member_user_id) */
    lazy val userFk = foreignKey("fk__family_member_user_id", userId, User)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    
    /** Uniqueness Index over (userId,role) (database name uk_unique_family_member) */
    val index1 = index("uk_unique_family_member", (userId, role), unique=true)
  }
  /** Collection-like TableQuery object for table FamilyMember */
  lazy val FamilyMember = new TableQuery(tag => new FamilyMember(tag))
  
  /** Entity class storing rows of table User
   *  @param id Database column id DBType(BIGINT), AutoInc, PrimaryKey
   *  @param username Database column username DBType(VARCHAR), Length(20,true)
   *  @param iphone Database column iphone DBType(VARCHAR), Length(15,true)
   *  @param email Database column email DBType(VARCHAR), Length(40,true)
   *  @param password Database column password DBType(VARCHAR), Length(20,true)
   *  @param status Database column status DBType(VARCHAR), Length(20,true)
   *  @param utype Database column utype DBType(VARCHAR), Length(20,true)
   *  @param bind Database column bind DBType(VARCHAR), Length(40,true)
   *  @param promotedType Database column promoted_type DBType(VARCHAR), Length(40,true) */
  case class UserRow(var id: Long, username: String, iphone: String, email: String, password: String, status: String, utype: String, bind: String, promotedType: String)
  /** GetResult implicit for fetching UserRow objects using plain SQL queries */
  implicit def GetResultUserRow(implicit e0: GR[Long], e1: GR[String]): GR[UserRow] = GR{
    prs => import prs._
    UserRow.tupled((<<[Long], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String]))
  }
  /** Table description of table user. Objects of this class serve as prototypes for rows in queries. */
  class User(_tableTag: Tag) extends Table[UserRow](_tableTag, "user") {
    def * = (id, username, iphone, email, password, status, utype, bind, promotedType) <> (UserRow.tupled, UserRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, username.?, iphone.?, email.?, password.?, status.?, utype.?, bind.?, promotedType.?).shaped.<>({r=>import r._; _1.map(_=> UserRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id DBType(BIGINT), AutoInc, PrimaryKey */
    val id: Column[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column username DBType(VARCHAR), Length(20,true) */
    val username: Column[String] = column[String]("username", O.Length(20,varying=true))
    /** Database column iphone DBType(VARCHAR), Length(15,true) */
    val iphone: Column[String] = column[String]("iphone", O.Length(15,varying=true))
    /** Database column email DBType(VARCHAR), Length(40,true) */
    val email: Column[String] = column[String]("email", O.Length(40,varying=true))
    /** Database column password DBType(VARCHAR), Length(20,true) */
    val password: Column[String] = column[String]("password", O.Length(20,varying=true))
    /** Database column status DBType(VARCHAR), Length(20,true) */
    val status: Column[String] = column[String]("status", O.Length(20,varying=true))
    /** Database column utype DBType(VARCHAR), Length(20,true) */
    val utype: Column[String] = column[String]("utype", O.Length(20,varying=true))
    /** Database column bind DBType(VARCHAR), Length(40,true) */
    val bind: Column[String] = column[String]("bind", O.Length(40,varying=true))
    /** Database column promoted_type DBType(VARCHAR), Length(40,true) */
    val promotedType: Column[String] = column[String]("promoted_type", O.Length(40,varying=true))
    
    /** Uniqueness Index over (email) (database name ix_email_unique) */
    val index1 = index("ix_email_unique", email, unique=true)
    /** Uniqueness Index over (iphone) (database name ix_iphone_unique) */
    val index2 = index("ix_iphone_unique", iphone, unique=true)
    /** Uniqueness Index over (username) (database name ix_username_unique) */
    val index3 = index("ix_username_unique", username, unique=true)
  }
  /** Collection-like TableQuery object for table User */
  lazy val User = new TableQuery(tag => new User(tag))
}