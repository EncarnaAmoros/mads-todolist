package controllers;

import java.util.List;

import play.*;
import play.mvc.*;
import views.html.*;
import static play.libs.Json.*;
import play.data.Form;
import play.db.jpa.*;

import models.*;

public class Tareas extends Controller {

    @Transactional(readOnly = true)
    // Devuelve una página con la lista de tareas
    public Result listaTareas(Integer usuarioId) {
        List<Tarea> tareas = TareaService.findAllTareasUsuario(usuarioId);
        if(tareas!=null) {
          return ok(listaTareas.render(tareas));
        } else {
          return notFound(error.render("404", "recurso no encontrado."));
        }
    }

    @Transactional
    // Devuelve una página con el formulario para crear tareas
    public Result formularioNuevaTarea(Integer usuarioId) {
        Usuario usuario = UsuarioDAO.find(usuarioId);
        if(usuario!=null) {
          return ok(formCreacionTarea.render(""));
        } else {
          return notFound(error.render("404", "recurso no encontrado."));
        }
    }


}
