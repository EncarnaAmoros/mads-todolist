package models;

import play.*;
import play.mvc.*;
import play.db.jpa.*;
import java.util.List;
import java.util.Date;

import javax.persistence.*;

public class TareaDAO {

    //Devuelve una tarea por su id
    public static Tarea find(Integer idTarea) {
        return JPA.em().find(Tarea.class, idTarea);
    }

    //Graba una nueva tarea y la devuelve
    public static Tarea create (Tarea tarea) {
      tarea.nulificaAtributos();
      JPA.em().persist(tarea);
      // Hacemos un flush y un refresh para asegurarnos de que se realiza
      // la creación en la BD y se devuelve el id inicializado
      JPA.em().flush();
      JPA.em().refresh(tarea);
      Logger.debug(tarea.toString());
      return tarea;
    }

    //Modifica la tarea y la devuelve
    public static void update (Tarea tarea) {
      JPA.em().merge(tarea);
    }
}
