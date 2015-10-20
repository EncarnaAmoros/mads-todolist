package models;

import play.*;
import play.mvc.*;
import play.db.jpa.*;
import java.util.List;
import java.util.Date;

import javax.persistence.*;

public class TareaDAO {
    public static Tarea find(Integer idTarea) {
        return JPA.em().find(Tarea.class, idTarea);
    }
}
