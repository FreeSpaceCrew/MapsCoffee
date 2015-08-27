package controllers

import play.Logger
import play.api.libs.json._
import play.api.libs.functional.syntax._
import javax.inject.Inject
import play.api.Play.current
import scala.concurrent.Future
import play.api.mvc._
import play.api.libs.ws._

case class CoffeePoint(id: String, name: String="no name", lat: Double = 0.0, lon: Double = 0.0)

object XML2CoffeePoint{

  def decode(node: scala.xml.Node): CoffeePoint = {

//    Logger.debug(s" $node ")

    CoffeePoint(
      (node \ "@id" text),
      node.child.filter(tag => (tag \ "@k" text) == "name") \ "@v" text,
      (node \ "@lat" text).toDouble,
      (node \ "@lon" text).toDouble
    )
  }
}


class API @Inject() (ws: WSClient) extends Controller {

  implicit val context = play.api.libs.concurrent.Execution.Implicits.defaultContext

  def index = Action {
    Ok(views.html.api("RESTful API"))
  }


  implicit val locationWrites: Writes[CoffeePoint] = (
    (JsPath \ "id").write[String] and
    (JsPath \ "name").write[String] and
    (JsPath \ "lat").write[Double] and
      (JsPath \ "lon").write[Double]
    )(unlift(CoffeePoint.unapply))

  def xml2points(s: String): List[CoffeePoint] = {

    val nodes = scala.xml.XML.loadString(s) \\ "node"

    nodes.map{ node => XML2CoffeePoint.decode(node) }.toList

  }


  def makeBody(s: String, n: String, w: String, e: String): String =
      s"""<bbox-query s="$s" n="$n" w="$w" e="$e"/>
         <query type="node">
            <item/>
            <has-kv k="amenity" v="cafe"/>
            <has-kv k="cuisine" v="coffee_shop"/>
         </query>
         <print/>""";

  def points(s: String, n: String, w: String, e: String) = Action.async {

    val request: WSRequest = ws.url(current.configuration.getString("overpassAPIUrl").get)

    val body = makeBody(s, n, w, e)

    Logger.debug(s" $body ")

    val futureResponse: Future[WSResponse] = request.post(body)

    // FIXME: error handling
    futureResponse.map( res => Ok(
        Json.obj(
          "status"  -> "ok",
          "points"  -> Json.toJson(xml2points(res.body))
        )
      )
    )

  }

}