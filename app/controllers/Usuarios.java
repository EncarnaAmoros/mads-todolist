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
import java.util.List;
import java.util.Date;

public class Usuarios extends Controller {

    //Guardamos la información del usuario para tener formulario
    //relleno por defecto al entrar (cuando volvemos de errores badRequest)
    Usuario usuario_a_modificar;


    @Transactional(readOnly = true)
    // Devuelve una página con la lista de usuarios
    public Result listaUsuarios() {
      // Obtenemos el mensaje flash guardado en la petición
      // por el controller grabaUsuario
      String mensaje = flash("grabaUsuario");
      if(mensaje == null) mensaje = "";
      List<Usuario> usuarios = UsuarioService.findAllUsuarios();
      return ok(listaUsuarios.render(usuarios, mensaje));
    }

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
     return redirect(controllers.routes.Usuarios.listaUsuarios());
   }

   @Transactional(readOnly = true)
   // Devuelve una página con el detalle del usuario por su id
   public Result detalleUsuario(String id) {
     Usuario usuario = UsuarioService.findUsuario(id);
     return ok(detalleUsuario.render(usuario));
   }

   @Transactional(readOnly = true)
   // Devuelve una página con un formulario relleno con los
   //datos del usuario pudiendose modificar
   public Result editarUsuario(String id) {
     Usuario usuario = UsuarioService.findUsuario(id);
     usuario_a_modificar = usuario;
     return ok(formModificarUsuario.render(Form.form(Usuario.class), usuario, ""));
   }

   @Transactional
  // Modifica un usuario en la BD y devuelve código HTTP
  // de redirección a la página de listado de usuarios
  public Result grabaUsuarioModificado() {
    Form<Usuario> usuarioForm = Form.form(Usuario.class).bindFromRequest();
    if (usuarioForm.hasErrors()) {
      return badRequest(formModificarUsuario.render(usuarioForm, usuario_a_modificar, "Hay errores en el formulario"));
    }
    Usuario usuario = usuarioForm.get();
    System.out.println("EN USUFORM.get:::::::"+usuario.toString());
    UsuarioService.modificarUsuario(usuario);
    flash("grabaUsuario", "El usuario se ha grabado correctamente");
    return redirect(controllers.routes.Usuarios.listaUsuarios());
  }

}
