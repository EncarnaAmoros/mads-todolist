package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

    public Result index() {
        return ok(index.render("Hola, tu nueva aplicación está lista."));
    }

    public Result saludo(String nombre) {
      return ok(saludo.render(nombre));
    }

}
