import org.junit.*;
import play.test.*;
import play.Application;
import play.mvc.*;
import static play.test.Helpers.*;
import static org.junit.Assert.*;
import play.db.jpa.*;
import java.util.List;
import models.*;
import org.dbunit.*;
import org.dbunit.dataset.*;
import org.dbunit.dataset.xml.*;
import java.util.HashMap;
import java.io.FileInputStream;

import play.data.format.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import play.data.format.*;

import play.libs.ws.*;

public class ModificarTareaTests {

    JndiDatabaseTester databaseTester;
    Application app;

    // Devuelve los settings necesarios para crear la aplicación fake
    // usando la base de datos de integración
    private static HashMap<String, String> settings() {
        HashMap<String, String> settings = new HashMap<String, String>();
        settings.put("db.default.url", "jdbc:mysql://localhost:3306/mads_test");
        settings.put("db.default.username", "root");
        settings.put("db.default.password", "mads");
        settings.put("db.default.jndiName", "DefaultDS");
        settings.put("jpa.default", "mySqlPersistenceUnit");
        return(settings);
    }

    // Crea la conexión con la base de datos de prueba y
    // la inicializa con las tablas definidas por las entidades JPA
    @BeforeClass
    public static void createTables() {
        Application fakeApp = Helpers.fakeApplication(settings());
        // Abrimos una transacción para que JPA cree en la BD
        // las tablas correspondientes a las entidades
        running (fakeApp, () -> {
            JPA.withTransaction(() -> {});
        });
    }

    // Se ejecuta antes de cada tests, inicializando la BD con los
    // datos del dataset
    @Before
    public void inicializaBaseDatos() throws Exception {
        app = Helpers.fakeApplication(settings());
        databaseTester = new JndiDatabaseTester("DefaultDS");
        IDataSet initialDataSet = new FlatXmlDataSetBuilder().build(new
            FileInputStream("test/resources/tareas_dataset_1.xml"));
        databaseTester.setDataSet(initialDataSet);
        databaseTester.onSetup();
    }

    @After
    public void cierraBaseDatos() throws Exception {
        databaseTester.onTearDown();
    }

    @Test
    public void testUpdateTarea() {
        running (app, () -> {
            JPA.withTransaction(() -> {
                Usuario usuario = UsuarioDAO.find(1);
                Tarea tarea = TareaDAO.find(2);
                tarea.descripcion = "Preparar el parcial de MADS";
                TareaDAO.update(tarea);
                List<Tarea> tareas = usuario.tareas;
                JPA.em().refresh(usuario);
                assertEquals(tareas.size(), 3);
                assertTrue(tareas.contains(
                    new Tarea(usuario, "Preparar el parcial de MADS")));
            });
        });
    }

    @Test
    public void testModificarTarea() {
        running (app, () -> {
            JPA.withTransaction(() -> {
                Usuario usuario = UsuarioDAO.find(1);
                Tarea tarea = TareaDAO.find(2);
                tarea.descripcion = "Preparar el super parcial de MADS";
                TareaService.modificarTarea(tarea);
                List<Tarea> tareas = usuario.tareas;
                JPA.em().refresh(usuario);
                assertEquals(tareas.size(), 3);
                assertTrue(tareas.contains(
                    new Tarea(usuario, "Preparar el super parcial de MADS")));
            });
        });
    }

    @Test
    public void testWebPaginaFormModificarTarea() {
        running(testServer(3333, app), () -> {
            int timeout = 10000;
            WSResponse response = WS
                .url("http://localhost:3333/usuarios/1/tareas/2/editar")
                .get()
                .get(timeout);
            assertEquals(OK, response.getStatus());
            String body = response.getBody();
            assertTrue(body.contains("Modificar tarea"));
        });
    }

    @Test
    public void testWebApiModificarTarea() {
        running(testServer(3333, app), () -> {
            int timeout = 10000;
            WSResponse response = WS.url("http://localhost:3333/usuarios/1/tareas/modifica")
                .setFollowRedirects(true)
                .setContentType("application/x-www-form-urlencoded")
                .post("id=2&descripcion=Entregar práctica 3 de MADS&estado=pendiente")
                .get(timeout);
            assertEquals(OK, response.getStatus());
            String body = response.getBody();
            assertTrue(body.contains(
                "Preparar el trabajo del tema 1 de biología"));
            assertTrue(body.contains(
                "Entregar práctica 3 de MADS"));
            assertTrue(body.contains(
                "Leer el libro de inglés"));
        });
    }

    @Test
    public void testWebApiModificarTareaDescripcionVacia() {
        running(testServer(3333, app), () -> {
            int timeout = 10000;
            WSResponse response = WS.url("http://localhost:3333/usuarios/1/tareas/modifica")
                .setFollowRedirects(true)
                .setContentType("application/x-www-form-urlencoded")
                .post("id=2&descripcion=&estado=pendiente")
                .get(timeout);
            assertEquals(BAD_REQUEST, response.getStatus());
            String body = response.getBody();
            assertTrue(body.contains(
                "La descripción no puede estar vacía."));
        });
    }

    @Test
    public void testWebPaginaFormModificarTareaUsuarioNotFound() {
        running(testServer(3333, app), () -> {
            int timeout = 10000;
            WSResponse response = WS
                .url("http://localhost:3333/usuarios/9999/tareas/2/editar")
                .get()
                .get(timeout);
            assertEquals(NOT_FOUND, response.getStatus());
            String body = response.getBody();
            assertTrue(body.contains("404"));
            assertTrue(body.contains("recurso no encontrado."));
        });
    }

    @Test
    public void testWebApiModificarTareaUsuarioNotFound() {
        running(testServer(3333, app), () -> {
            int timeout = 10000;
            WSResponse response = WS.url("http://localhost:3333/usuarios/9999/tareas/modifica")
                .setFollowRedirects(true)
                .setContentType("application/x-www-form-urlencoded")
                .post("id=2&descripcion=Practicar canción Dias de Elias con la guitarra&estado=pendiente")
                .get(timeout);
            assertEquals(NOT_FOUND, response.getStatus());
            String body = response.getBody();
            assertTrue(body.contains("404"));
            assertTrue(body.contains("recurso no encontrado."));
        });
    }

    @Test
    public void testWebPaginaFormModificarTareaDescripcionPuesta() {
        running(testServer(3333, app), () -> {
            int timeout = 10000;
            WSResponse response = WS
                .url("http://localhost:3333/usuarios/1/tareas/2/editar")
                .get()
                .get(timeout);
            assertEquals(OK, response.getStatus());
            String body = response.getBody();
            assertTrue(body.contains("Estudiar el parcial de matemáticas"));
        });
    }

    @Test
    public void testWebApiModificarTareaMensajeConfirmacion() {
        running(testServer(3333, app), () -> {
            int timeout = 10000;
            WSResponse response = WS.url("http://localhost:3333/usuarios/1/tareas/modifica")
                .setFollowRedirects(true)
                .setContentType("application/x-www-form-urlencoded")
                .post("id=2&descripcion=Entregar práctica 3 de MADS&estado=pendiente")
                .get(timeout);
            assertEquals(OK, response.getStatus());
            String body = response.getBody();
            assertTrue(body.contains(
                "Preparar el trabajo del tema 1 de biología"));
            assertTrue(body.contains(
                "Entregar práctica 3 de MADS"));
            assertTrue(body.contains(
                "Leer el libro de inglés"));
            assertTrue(body.contains(
                "La tarea se ha grabado correctamente."));
        });
    }

    @Test
    public void testWebPaginaFormModificarTareaNotFound() {
        running(testServer(3333, app), () -> {
            int timeout = 10000;
            WSResponse response = WS
                .url("http://localhost:3333/usuarios/1/tareas/9999999/editar")
                .get()
                .get(timeout);
            assertEquals(NOT_FOUND, response.getStatus());
            String body = response.getBody();
            assertTrue(body.contains("404"));
            assertTrue(body.contains("recurso no encontrado."));
        });
    }

    @Test
    public void testWebApiModificarTareaNotFound() {
        running(testServer(3333, app), () -> {
            int timeout = 10000;
            WSResponse response = WS.url("http://localhost:3333/usuarios/1/tareas/modifica")
                .setFollowRedirects(true)
                .setContentType("application/x-www-form-urlencoded")
                .post("id=9999999&descripcion=Practicar canción Dias de Elias con la guitarra&estado=pendiente")
                .get(timeout);
            assertEquals(NOT_FOUND, response.getStatus());
            String body = response.getBody();
            assertTrue(body.contains("404"));
            assertTrue(body.contains("recurso no encontrado."));
        });
    }

    /* Test para nueva caracteristica, el estado de una tarea pendiente o realizada */

    @Test
    public void testModificarTareaEstado() {
        running (app, () -> {
            JPA.withTransaction(() -> {
                Usuario usuario = UsuarioDAO.find(1);
                Tarea tarea = TareaDAO.find(2);
                tarea.estado = "realizada";
                TareaService.modificarTarea(tarea);
                List<Tarea> tareas = usuario.tareas;
                JPA.em().refresh(usuario);
                assertEquals(tareas.size(), 3);
                assertTrue(tareas.get(1).estado.equals("realizada"));
            });
        });
    }

    @Test
    public void testWebPaginaModificarTareaEstado() {
        running(testServer(3333, app), () -> {
            JPA.withTransaction(() -> {
                Usuario usuario = UsuarioDAO.find(1);
                Tarea tarea = TareaDAO.find(2);
                tarea.estado = "realizada";
                TareaService.modificarTarea(tarea);
                List<Tarea> tareas = usuario.tareas;
                JPA.em().refresh(usuario);
            });

            int timeout = 10000;
            WSResponse response = WS
                .url("http://localhost:3333/usuarios/1/tareas")
                .get()
                .get(timeout);
            assertEquals(OK, response.getStatus());
            String body = response.getBody();
            assertTrue(body.contains("id='realizada'"));
        });
    }

    @Test
    public void testWebPaginaModificarTareaUrlUpdate() {
        running(testServer(3333, app), () -> {
            int timeout = 10000;
            WSResponse response = WS
                .url("http://localhost:3333/usuarios/1/tareas")
                .get()
                .get(timeout);
            assertEquals(OK, response.getStatus());
            String body = response.getBody();
            assertTrue(body.contains("up('/usuarios/1/tareas/modifica',"));
            assertTrue(body.contains("'1', 'Preparar el trabajo del tema 1 de biología', 'pendiente');"));
        });
    }

    /* Un usuario no puede modificar tareas que no son suyas */

    @Test
    public void testWebApiModificarTareaAjena() {
        running(testServer(3333, app), () -> {
            int timeout = 10000;
            WSResponse response = WS.url("http://localhost:3333/usuarios/2/tareas/modifica")
                .setFollowRedirects(true)
                .setContentType("application/x-www-form-urlencoded")
                .post("id=1&descripcion=Entregar práctica 3 de MADS&estado=pendiente")
                .get(timeout);
            assertEquals(UNAUTHORIZED, response.getStatus());
            String body = response.getBody();
            assertTrue(body.contains(
                "401"));
            assertTrue(body.contains(
                "acceso no autorizado."));
        });
    }

    /* Añadiendo atributo fecha a las tareas */

    @Test
    public void testUpdateTareaFecha() {
        running (app, () -> {
            JPA.withTransaction(() -> {
                Usuario usuario = UsuarioDAO.find(1);
                Tarea tarea = TareaDAO.find(2);
                //Creamos la fecha a modificar
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                String dateInString = "15-11-2015";
                Date f = sdf.parse(dateInString);
                tarea.fecha = f;
                TareaDAO.update(tarea);
                List<Tarea> tareas = usuario.tareas;
                JPA.em().refresh(usuario);
                assertEquals(tareas.size(), 3);
                assertTrue(tareas.get(1).fecha.getDay()==f.getDay());
                assertTrue(tareas.get(1).fecha.getMonth()==f.getMonth());
                assertTrue(tareas.get(1).fecha.getYear()==f.getYear());
            });
        });
    }

    @Test
    public void testModificarTareaFecha() {
        running (app, () -> {
            JPA.withTransaction(() -> {
                Usuario usuario = UsuarioDAO.find(1);
                Tarea tarea = TareaDAO.find(2);
                //Creamos la fecha a modificar
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                String dateInString = "30-01-2016";
                Date f = sdf.parse(dateInString);
                tarea.fecha = f;
                TareaService.modificarTarea(tarea);
                List<Tarea> tareas = usuario.tareas;
                JPA.em().refresh(usuario);
                assertEquals(tareas.size(), 3);
                assertTrue(tareas.get(1).fecha.getDay()==f.getDay());
                assertTrue(tareas.get(1).fecha.getMonth()==f.getMonth());
                assertTrue(tareas.get(1).fecha.getYear()==f.getYear());
            });
        });
    }

    @Test
    public void testWebPaginaFormModificarTareaFecha() {
        running(testServer(3333, app), () -> {
            int timeout = 10000;
            WSResponse response = WS
                .url("http://localhost:3333/usuarios/1/tareas/3/editar")
                .get()
                .get(timeout);
            assertEquals(OK, response.getStatus());
            String body = response.getBody();
            assertTrue(body.contains("Modificar tarea"));
            assertTrue(body.contains("Fecha"));
            assertTrue(body.contains("11-01-2016"));
            assertTrue(body.contains("Formato (dd-mm-yyyy)"));
        });
    }
}
