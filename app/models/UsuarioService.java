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

 public static boolean deleteUsuario(String id) {
   try {
     UsuarioDAO.delete(id);
     return true;
   }
   catch(Exception e)
   {
     return false;
   }
 }

 //Devuelve un usuario según su login y password
 public static Usuario findUsuarioByLoginPassword(String login, String password) {
   return UsuarioDAO.findByLoginPassword(login, password);
 }

 //Devuelve un usuario según su login
 public static Usuario findUsuarioByLogin(String login) {
   return UsuarioDAO.findByLoginPassword(login);
 }

 //Devuelve un usuario según su login que no tenga determinado id
 public static Usuario findUsuarioByLoginNotId(String login, String id) {
   return UsuarioDAO.findByLoginNotId(login, id);
 }

}
