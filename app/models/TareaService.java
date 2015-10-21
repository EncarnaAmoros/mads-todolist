package models;

import play.*;
import play.mvc.*;
import play.db.jpa.*;
import java.util.List;
import java.util.Date;
import java.util.List;

public class TareaService {

  //Devuelve un usuario según su id
  public static List<Tarea> findAllTareasUsuario(Integer id) {
    Usuario usuario = UsuarioDAO.find(id);
    return usuario.tareas;
  }

}
