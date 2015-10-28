package models;

import play.*;
import play.mvc.*;
import play.db.jpa.*;
import java.util.List;
import java.util.Date;
import java.util.List;

public class TareaService {

  //Devuelve una tarea por su id llamando al DAO
  public static Tarea findTarea(Integer idTarea) {
      return TareaDAO.find(idTarea);
  }

  public static List<Tarea> findAllTareasUsuario(Integer id) {
    Usuario usuario = UsuarioDAO.find(id);
    if(usuario!=null)
      return usuario.tareas;
    else
      return null;
  }

  //Graba una nueva tarea llamando al DAO y la devuelve
  public static Tarea grabaTarea(Tarea tarea) {
    return TareaDAO.create(tarea);
  }

  //Modifica los datos de la tarea llamando al DAO y la devuelve
  public static Tarea modificarTarea(Tarea tarea) {
    return TareaDAO.update(tarea);
  }

  //Elimina una tarea llamando al DAO true si va bien
  public static boolean deleteTarea(Integer id) {
    try {
      TareaDAO.delete(id);
      return true;
    } catch(Exception e) {
      return false;
    }
  }

}
