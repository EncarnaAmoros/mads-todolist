package models;

import java.util.Date;
import java.util.List;
import javax.persistence.*;
import play.data.validation.Constraints;
import play.data.format.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    public Integer id;
    @Constraints.Required
    public String login;
    public String nombre;
    public String apellidos;
    public String eMail;
    @Formats.DateTime(pattern="dd-MM-yyyy")
    public Date fechaNacimiento;
    @Constraints.Required
    public String password;
    @OneToMany(mappedBy="usuario")
    public List<Tarea> tareas;

    public Usuario() {}

    public Usuario(String login, String password) {
        this.login = login;
        this.password = password;
    }

    @Override public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Usuario otroUsuario = (Usuario) obj;

        // Si los dos usuarios tienen id (ya se han grabado en la base
        // de datos) comparamos los ids. En otro caso, comparamos los
        // atributos no nulos.

        if (id != null && otroUsuario.id != null)
            return (id == otroUsuario.id);
        else return (login.equals(otroUsuario.login)) &&
                    (password.equals(otroUsuario.password));
    }

   // Sustituye por null todas las cadenas vacías que pueda tener
   // un usuario en sus atributos
   public void nulificaAtributos() {
     if (nombre != null && nombre.isEmpty()) nombre = null;
     if (apellidos != null && apellidos.isEmpty()) apellidos = null;
     if (eMail != null && eMail.isEmpty()) eMail = null;
     if (password != null && password.isEmpty()) password = null;
   }

   public String toString() {
     String fechaStr = null;
     if (fechaNacimiento != null) {
       SimpleDateFormat formateador = new SimpleDateFormat("dd-MM-yyyy");
       fechaStr = formateador.format(fechaNacimiento);
     }
     return String.format("Usuario id: %s login: %s nombre: %s " +
                           "apellidos: %s eMail: %s fechaNacimiento: %s" +
                           "password: %s",
                           id, login, nombre, apellidos, eMail, fechaStr, password);
   }

}
