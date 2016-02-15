package controllers;

import play.libs.F;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;

public class API extends Controller {

    @Inject
    WSClient ws;

    private static final String ELASTIC_URL = "http://localhost:9200/map/coffee/_search";

    public F.Promise<Result> points(String s, String n, String w, String e) {

        WSRequest request = ws.url(ELASTIC_URL);
        F.Promise<WSResponse> responsePromise = request.post(getRequestBody(s, w, n, e));
        return responsePromise.map(response -> ok(getRequestBody(s, w, n, e)));
    }

    private String getRequestBody(String s, String w, String n, String e) {
        return String.format("{\n" +
                        " \"size\" : 1000," +   // number of documents (points)\n
                        " \"query\":{\n" +
                        "    \"bool\" : {\n" +
                        "        \"must\" : {\n" +
                        "            \"match_all\" : {}\n" +             // search query (empty match all documents)\n
                        "         },\n" +
                        "        \"filter\" : {\n" +
                        "            \"geo_bounding_box\" : {\n" +       // bbox (s,w) -> (n,e)\n"
                        "                \"location\" : {\n" +
                        "                    \"top_left\" : {\n" +
                        "                        \"lat\" : %s,\n" + //s
                        "                        \"lon\" : %s\n" +  //w
                        "                    },\n" +
                        "                    \"bottom_right\" : {\n" +
                        "                        \"lat\" : %s,\n" + //n
                        "                        \"lon\" : %s\n" + //e
                        "                    }\n" +
                        "                }\n" +
                        "            }\n" +
                        "        }\n" +
                        "    }\n" +
                        "  }\n" +
                        "}",
                s, w, n, e);
    }

    public Result addPoint(String json) {

        WSRequest request = ws.url(ELASTIC_URL);
        F.Promise<WSResponse> responsePromise = request.put(json);

        return ok();
    }




}
