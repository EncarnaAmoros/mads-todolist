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

  public Tarea() {}

  public Tarea(Usuario usuario, String descripcion) {
      if (descripcion == null || usuario == null)
        throw new IllegalArgumentException();
      this.descripcion = descripcion;
      this.usuario = usuario;
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
                  (usuario.equals(otraTarea.usuario));
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
  // un usuario en sus atributos
  public void nulificaAtributos() {
    if (descripcion != null && descripcion.isEmpty()) descripcion = null;
  }

  public String toString() {
    return String.format("Tarea id: %s descripcion: %s",
                          id, descripcion);
  }

}
