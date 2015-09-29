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

 //Modifica los datos del usuario y lo devuelve
 public static void modificarUsuario(Usuario usuario) {
   UsuarioDAO.update(usuario);
 }

 public static List<Usuario> findAllUsuarios() {
   return UsuarioDAO.findAll();
 }

 //Devuelve un usuario según su id
 public static Usuario findUsuario(String id) {
   return UsuarioDAO.find(id);
 }

}
