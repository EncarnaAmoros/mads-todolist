package controllers;

import play.*;
import play.mvc.*;
import views.html.*;
import models.*;
import play.*;
import play.mvc.*;
import static play.libs.Json.*;
import play.data.Form;
import play.db.jpa.*;

public class Usuarios extends Controller {

    public Result formularioNuevoUsuario() {
      return ok(formCreacionUsuario.render(Form.form(Usuario.class),""));
    }

    @Transactional
   // Añade un nuevo usuario en la BD y devuelve código HTTP
   // de redirección a la página de listado de usuarios
   public Result grabaNuevoUsuario() {
     Form<Usuario> usuarioForm = Form.form(Usuario.class).bindFromRequest();
     if (usuarioForm.hasErrors()) {
       return badRequest(formCreacionUsuario.render(usuarioForm, "Hay errores en el formulario"));
     }
     Usuario usuario = usuarioForm.get();
     usuario = UsuarioService.grabaUsuario(usuario);
     flash("grabaUsuario", "El usuario se ha grabado correctamente");
     return redirect(controllers.routes.Usuarios.logTemporal());
     //return redirect(controllers.routes.Usuarios.listaUsuarios());
   }

   //Devuelve una página temporal hasta tener la funcionalidad completa
   public Result logTemporal() {
     String mensaje = flash("grabaUsuario");
     return ok(logTemporal.render(mensaje));
   }

}
