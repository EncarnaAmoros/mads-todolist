package models;

import play.*;
import play.mvc.*;
import play.db.jpa.*;
import java.util.List;
import java.util.Date;
import models.*;

public class UsuarioDAO {

 public static Usuario create (Usuario usuario) {
   usuario.nulificaAtributos();
   System.out.println("Mira su fecha:"+usuario.fechaNacimiento);
   JPA.em().persist(usuario);
   // Hacemos un flush y un refresh para asegurarnos de que se realiza
   // la creación en la BD y se devuelve el id inicializado
   JPA.em().flush();
   JPA.em().refresh(usuario);
   Logger.debug(usuario.toString());
   return usuario;
 }

 public static List<Usuario> findAll() {
   return (List<Usuario>) JPA.em().createQuery("select u from Usuario u ORDER BY id").getResultList();
 }

 //Devuelve un usuario según su id
 public static Usuario find(String id) {
   return (Usuario) JPA.em().find(Usuario.class, id);
 }

}
