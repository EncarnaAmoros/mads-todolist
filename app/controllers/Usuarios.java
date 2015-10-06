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
    Form<Usuario> usuarioForm;

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
   public Result grabaNuevoUsuario(int n) {
     Form<Usuario> usuarioForm = Form.form(Usuario.class).bindFromRequest();
     if (usuarioForm.hasErrors()) {
       return badRequest(formCreacionUsuario.render(usuarioForm, "Hay errores en el formulario"));
     }

     //Si el login ya está en uso ha de escribir otro
     if(UsuarioService.findUsuarioByLogin(usuarioForm.get().login) != null) {
       if(n==0) {
         return badRequest(formCreacionUsuario.render(usuarioForm, "Ya hay un usuario con dicho login"));
       } else {
         return badRequest(formRegistroUsuario.render(usuarioForm, "Ya hay un usuario con dicho login"));
       }
     }

     Usuario usuario = usuarioForm.get();
     usuario = UsuarioService.grabaUsuario(usuario);
     //El argumento 0 quiere decir que el usuario lo ha creado el administrador
     //Se le reenvia a la página con la lista de usuarios
     if(n==0) {
       flash("grabaUsuario", "El usuario se ha grabado correctamente");
       return redirect(controllers.routes.Usuarios.listaUsuarios());
     }
     //Si el argumento es 1 el usuario se ha registrado y se le envia a una
     //página de saludo
     else {
       return redirect(controllers.routes.Application.saludo(usuario.login));
     }
   }

   @Transactional(readOnly = true)
   // Devuelve una página con el detalle del usuario por su id
   //Si no lo encuentra devuelve mensaje de error not found
   public Result detalleUsuario(String id) {
     Usuario usuario = UsuarioService.findUsuario(id);
     if(usuario!=null) {
       return ok(detalleUsuario.render(usuario));
     } else {
       return notFound(error.render("404", "recurso no encontrado."));
     }
   }

   @Transactional(readOnly = true)
   // Devuelve una página con un formulario relleno con los
   //datos del usuario pudiendose modificar
   //Si el usuario no existe muestra pág error
   public Result editarUsuario(String id) {
     Usuario usuario = UsuarioService.findUsuario(id);
     if(usuario!=null) {
       Form<Usuario> formulario = Form.form(Usuario.class);
       usuarioForm = formulario.fill(usuario);
       return ok(formModificarUsuario.render(usuarioForm, ""));
     } else {
       return notFound(error.render("404", "recurso no encontrado."));
     }
   }

   @Transactional
  // Modifica un usuario en la BD y devuelve código HTTP
  // de redirección a la página de listado de usuarios
  //No modificael pa ssword del usuario
  public Result grabaUsuarioModificado() {
    String password_no_change = usuarioForm.get().password;
    Form<Usuario> usuarioForm = Form.form(Usuario.class).bindFromRequest();
    if (usuarioForm.hasErrors()) {
      return badRequest(formModificarUsuario.render(usuarioForm, "Hay errores en el formulario"));
    }

    //Si el login ya está en uso ha de escribir otro
    if(UsuarioService.findUsuarioByLoginNotId(usuarioForm.get().login, usuarioForm.get().id) != null) {
      return badRequest(formModificarUsuario.render(usuarioForm, "Ya hay un usuario con dicho login"));
    }

    Usuario usuario = usuarioForm.get();
    usuario.password = password_no_change;
    UsuarioService.modificarUsuario(usuario);
    flash("grabaUsuario", "El usuario se ha grabado correctamente");
    return redirect(controllers.routes.Usuarios.listaUsuarios());
  }

  @Transactional
  //Elimina un usuario en la BD según su id
  public Result borraUsuario(String id) {
    UsuarioService.deleteUsuario(id);
    return redirect(controllers.routes.Usuarios.listaUsuarios());
  }

  @Transactional
  //Muestra un formulario que se rellena con la información
  //del usuario que posteriormente se grabará en la BD
  public Result registrarUsuario() {
    return ok(formRegistroUsuario.render(Form.form(Usuario.class),""));
  }

  @Transactional (readOnly = true)
  //Muestra una página que se rellena con la información
  //del usuario login y password para que se logee
  public Result loginUsuario() {
    return ok(loginUsuario.render(""));
  }

  @Transactional (readOnly = true)
  //Comprueba si el login y password pertenecen a un
  //usuario real, si no devuelve mensaje de error
  public Result compruebaLoginUsuario() {
    Form<Usuario> usuarioForm = Form.form(Usuario.class).bindFromRequest();
    Usuario usuario = usuarioForm.get();

    //Si se logea admin va a listaUsuarios
    if(usuario.login.equals("admin") && usuario.password.equals("admin")) {
      return redirect(controllers.routes.Usuarios.listaUsuarios());
    }
    //Si no es un admin
    else {
      usuario = UsuarioService.findUsuarioByLoginPassword(usuario.login, usuario.password);
      //Si no encuentra el usuario mostramos mensaje de error
      if(usuario==null)
        return ok(loginUsuario.render("Lo sentimos, no se reconoce el usuario introducido"));
      //Si lo encuentra va a la página de saludo
      else
        return redirect(controllers.routes.Application.saludo(usuario.login));
    }
  }

}
