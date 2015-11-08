package controllers

import play.Logger
import play.api.libs.json._
import play.api.libs.functional.syntax._
import javax.inject.Inject
import play.api.Play.current
import scala.concurrent.Future
import play.api.mvc._
import play.api.libs.ws._


class APIv2 @Inject() (ws: WSClient) extends Controller {

  implicit val context = play.api.libs.concurrent.Execution.Implicits.defaultContext

  implicit val locationWrites: Writes[CoffeePoint] = (
    (JsPath \ "id").write[String] and
      (JsPath \ "name").write[String] and
      (JsPath \ "lat").write[Double] and
      (JsPath \ "lon").write[Double]
    )(unlift(CoffeePoint.unapply))

  def index = Action {
    Ok(views.html.api("RESTful API v2.0"))
  }

  def mkElasticUrl = "http://" + current.configuration.getString("elasticHost").get + ":9200/map/coffee/_search"

  def mkBody(s: String, n: String, w: String, e: String) = s"""{
  "query":{
    "bool" : {
        "must" : {
            "match_all" : {}
        },
        "filter" : {
            "geo_bounding_box" : {
                "location" : {
                    "top_left" : {
                        "lat" : $n,
                        "lon" : $w
                    },
                    "bottom_right" : {
                        "lat" : $s,
                        "lon" : $e
                    }
                }
            }
        }
    }
  }
}"""

  def formatJson(resp: String): List[CoffeePoint] =
    (Json.parse(resp) \ "hits" \ "hits").as[List[JsValue]]
      .map{ i => CoffeePoint(
        (i \ "_id" ).as[String],
        (i \ "_source" \ "name").as[String],
        (i \ "_source" \ "location" \ "lat").as[Double],
        (i \ "_source" \ "location" \ "lon").as[Double]
      ) }

  def points(s: String, n: String, w: String, e: String) = Action.async {
    val request: WSRequest = ws.url(mkElasticUrl)

    val body = mkBody(s, n, w, e)

    Logger.debug(s" $body ")

    val futureResponse: Future[WSResponse] = request.post(body)

    // FIXME: error handling
    futureResponse.map( res => Ok(
      Json.obj(
        "status"  -> "ok",
        "points"  -> Json.toJson(formatJson(res.body))
      )
    )
    )


  }


}