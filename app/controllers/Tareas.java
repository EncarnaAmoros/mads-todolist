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
          return ok(listaTareas.render(usuarioId, tareas));
        } else {
          return notFound(error.render("404", "recurso no encontrado."));
        }
    }

    @Transactional(readOnly = true)
    // Devuelve una página con el formulario para crear tareas
    public Result formularioNuevaTarea(Integer usuarioId) {
        Usuario usuario = UsuarioDAO.find(usuarioId);
        if(usuario!=null) {
          return ok(formCreacionTarea.render(Form.form(Tarea.class), usuarioId, ""));
        } else {
          return notFound(error.render("404", "recurso no encontrado."));
        }
    }

    @Transactional
    // Llama al modelo para crear la tarea
    public Result grabaNuevaTarea(Integer usuarioId) {
        Usuario usuario = UsuarioDAO.find(usuarioId);
        if(usuario==null)
          return notFound(error.render("404", "recurso no encontrado."));

        Form<Tarea> tareaForm = Form.form(Tarea.class).bindFromRequest();
        if (tareaForm.hasErrors())
          return badRequest(formCreacionTarea.render(tareaForm, usuarioId, "La descripción no puede estar vacía."));

        Tarea tarea = new Tarea(usuario, tareaForm.get().descripcion);
        TareaService.grabaTarea(tarea);

        List<Tarea> tareas = TareaService.findAllTareasUsuario(usuarioId);
        return ok(listaTareas.render(usuarioId, tareas));
    }

}
