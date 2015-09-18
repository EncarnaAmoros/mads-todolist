package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

public class Usuarios extends Controller {

    public Result formularioNuevoUsuario() {
      return ok(formularioNuevoUsuario.render());

    }

}
