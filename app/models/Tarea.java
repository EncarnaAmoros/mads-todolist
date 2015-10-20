package models;

import javax.persistence.*;
import play.data.validation.Constraints;
import play.data.format.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Entity
public class Tarea {
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
   public Integer id;
  @ManyToOne
  @JoinColumn(name="usuarioId")
  public Usuario usuario;
  public String descripcion;
}
