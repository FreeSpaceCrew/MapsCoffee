package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class API extends Controller {


    public Result points(String s, String n, String w, String e) {
        return ok(index.render(String.format("s = %s, n = %s, w = %s, e = %s", s, n, w, e)));
    }




}
