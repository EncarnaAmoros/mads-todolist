package models;

import play.*;
import play.mvc.*;
import play.db.jpa.*;
import java.util.List;
import java.util.Date;
import java.util.List;

public class TareaService {

  public static List<Tarea> findAllTareasUsuario(Integer id) {
    Usuario usuario = UsuarioDAO.find(id);
    if(usuario!=null)
      return usuario.tareas;
    else
      return null;
  }

  public static Tarea grabaTarea(Tarea tarea) {
    return TareaDAO.create(tarea);
  }

  //Modifica los datos de la tarea y la devuelve
  public static void modificarTarea(Tarea tarea) {
    TareaDAO.update(tarea);
  }

}
