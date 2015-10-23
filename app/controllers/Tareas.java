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
          return ok(listaTareas.render(usuarioId, tareas, ""));
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
        return ok(listaTareas.render(usuarioId, tareas, ""));
    }

    @Transactional(readOnly = true)
    // Devuelve una página con un formulario relleno con los
    //datos de la tarea pudiendose modificar
    public Result editarTarea(Integer usuarioId, Integer tareaId) {
        Usuario usuario = UsuarioService.findUsuario(usuarioId);
        if(usuario==null)
          return notFound(error.render("404", "recurso no encontrado."));

        Tarea tarea = TareaService.findTarea(tareaId);
        Form<Tarea> formularioT = Form.form(Tarea.class);
        Form<Tarea> tareaForm = formularioT.fill(tarea);
        return ok(formModificarTarea.render(tareaForm, usuarioId, ""));
    }

    @Transactional
     // Modifica una tarea en la BD y devuelve código HTTP
     // de redirección a la página de listado de tareas
     public Result grabaTareaModificada(Integer usuarioId) {
       Usuario usuario = UsuarioService.findUsuario(usuarioId);
       if(usuario==null)
         return notFound(error.render("404", "recurso no encontrado."));

       Form<Tarea> tareaForm = Form.form(Tarea.class).bindFromRequest();
       if (tareaForm.hasErrors())
         return badRequest(formModificarTarea.render(tareaForm, usuarioId, "La descripción no puede estar vacía."));

       Tarea tarea = tareaForm.get();
       tarea.usuario = usuario;
       TareaService.modificarTarea(tarea);
       List<Tarea> tareas = TareaService.findAllTareasUsuario(usuarioId);
       return ok(listaTareas.render(usuarioId, tareas, "La tarea se ha grabado correctamente."));
     }

}
