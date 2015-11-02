package models;

import javax.persistence.*;
import play.data.validation.Constraints;
import play.data.format.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import play.data.format.*;

@Entity
public class Tarea {
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
   public Integer id;
  @ManyToOne
  @JoinColumn(name="usuarioId")
  public Usuario usuario;
  @Constraints.Required
  public String descripcion;
  @Constraints.Required
  public String estado;
  @Formats.DateTime(pattern="dd-MM-yyyy")
  public Date fecha;

  public Tarea() {}

  public Tarea(Usuario usuario, String descripcion) {
      if (descripcion == null || usuario == null)
        throw new IllegalArgumentException();
      this.descripcion = descripcion;
      this.usuario = usuario;
      this.estado = "pendiente";
  }

  @Override public boolean equals(Object obj) {
      if (obj == this) {
          return true;
      } if (obj == null || obj.getClass() != this.getClass()) {
          return false;
      }
      Tarea otraTarea = (Tarea) obj;

      // Si las dos tareas tienen id (ya se han grabado en la base
      // de datos) comparamos los id. En otro caso, comparamos los
      // atributos no nulos.

      if (id != null && otraTarea.id != null) return (id == otraTarea.id);
      else return (descripcion.equals(otraTarea.descripcion)) &&
                  (usuario.equals(otraTarea.usuario)) &&
                  (estado.equals(otraTarea.estado));
  }

  @Override public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result +
          ((id == null) ? 0 : id);
      result = prime * result + descripcion.hashCode();
      return result;
  }

  // Sustituye por null todas las cadenas vacías que pueda tener
  // una tarea en sus atributos
  public void nulificaAtributos() {
    if (descripcion != null && descripcion.isEmpty()) descripcion = null;
    if (estado != null && estado.isEmpty()) estado = "pendiente";
  }

  public String toString() {
    String fechaStr = null;
    if (fecha != null) {
      SimpleDateFormat formateador = new SimpleDateFormat("dd-MM-yyyy");
      fechaStr = formateador.format(fecha);
    }

    return String.format("Tarea id: %s descripcion: %s estado: %s fecha: %s",
                          id, descripcion, estado, fechaStr);
  }

}
