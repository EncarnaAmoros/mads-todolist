package models;

import play.*;
import play.mvc.*;
import play.db.jpa.*;
import java.util.List;
import java.util.Date;
import java.util.List;

public class UsuarioService {

 public static Usuario grabaUsuario(Usuario usuario) {
   return UsuarioDAO.create(usuario);
 }

 public static List<Usuario> findAllUsuarios() {
   return UsuarioDAO.findAllUsuarios();
 }

}
