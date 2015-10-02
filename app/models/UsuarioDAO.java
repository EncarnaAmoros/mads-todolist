package models;

import play.*;
import play.mvc.*;
import play.db.jpa.*;
import java.util.List;
import java.util.Date;
import models.*;
import javax.persistence.TypedQuery;

public class UsuarioDAO {

 public static Usuario create (Usuario usuario) {
   usuario.nulificaAtributos();
   JPA.em().persist(usuario);
   // Hacemos un flush y un refresh para asegurarnos de que se realiza
   // la creación en la BD y se devuelve el id inicializado
   JPA.em().flush();
   JPA.em().refresh(usuario);
   Logger.debug(usuario.toString());
   return usuario;
 }

 //Modifica el usuario y lo devuelve
 public static void update (Usuario usuario) {
   JPA.em().merge(usuario);
 }

 public static List<Usuario> findAll() {
   return (List<Usuario>) JPA.em().createQuery("select u from Usuario u ORDER BY id").getResultList();
 }

 //Devuelve un usuario según su id
 public static Usuario find(String id) {
   return (Usuario) JPA.em().find(Usuario.class, id);
 }

 //Elimina el usuario que tenga como id el pasado por parámetro
 public static void delete(String idUsuario) {
   Usuario usuario = JPA.em().getReference(Usuario.class, idUsuario);
   JPA.em().remove(usuario);
 }

 //Devuelve un usuario según su login y password
 public static Usuario findByLoginPassword(String login, String password) {
   TypedQuery<Usuario> query = JPA.em().createQuery("SELECT u FROM Usuario AS u where u.login='" + login +
                                              "' and u.password='" + password + "'", Usuario.class);
    List<Usuario> results = query.getResultList();
    Usuario u = null;
    if(results.size()!=0) {
      u = results.get(0);
    }

    return u;

 }

}
