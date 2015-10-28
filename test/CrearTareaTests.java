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

import play.libs.ws.*;

public class CrearTareaTests {

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
    public void testCreateTarea() {
        running (app, () -> {
            JPA.withTransaction(() -> {
                Usuario usuario = UsuarioDAO.find(1);
                Tarea tarea = new Tarea(usuario, "Entregar práctica 5 de MADS");
                TareaDAO.create(tarea);
                List<Tarea> tareas = usuario.tareas;
                assertEquals(tareas.size(), 4);
                assertTrue(tareas.contains(
                    new Tarea(usuario, "Entregar práctica 5 de MADS")));
            });
        });
    }

    @Test
    public void testGrabaTarea() {
        running (app, () -> {
            JPA.withTransaction(() -> {
                Usuario usuario = UsuarioDAO.find(1);
                Tarea tarea = new Tarea(usuario, "Entregar práctica 5 de MADS");
                TareaService.grabaTarea(tarea);
                List<Tarea> tareas = usuario.tareas;
                assertEquals(tareas.size(), 4);
                assertTrue(tareas.contains(
                    new Tarea(usuario, "Entregar práctica 5 de MADS")));
            });
        });
    }

    @Test
    public void testWebPaginaFormCreacionTarea() {
        running(testServer(3333, app), () -> {
            int timeout = 10000;
            WSResponse response = WS
                .url("http://localhost:3333/usuarios/1/tareas/nueva")
                .get()
                .get(timeout);
            assertEquals(OK, response.getStatus());
            String body = response.getBody();
            assertTrue(body.contains("Nueva tarea"));
        });
    }

    @Test
    public void testWebApiCreacionTarea() {
        running(testServer(3333, app), () -> {
            int timeout = 10000;
            WSResponse response = WS.url("http://localhost:3333/usuarios/1/tareas/nueva")
                .setContentType("application/x-www-form-urlencoded")
                .post("descripcion=Entregar práctica 3 de MADS&estado=pendiente")
                .get(timeout);
            assertEquals(OK, response.getStatus());
            String body = response.getBody();
            assertTrue(body.contains(
                "Preparar el trabajo del tema 1 de biología"));
            assertTrue(body.contains(
                "Estudiar el parcial de matemáticas"));
            assertTrue(body.contains(
                "Leer el libro de inglés"));
                assertTrue(body.contains(
                    "Entregar práctica 3 de MADS"));
        });
    }

    @Test
    public void testWebApiCreacionTareaUsuarioNotFound() {
        running(testServer(3333, app), () -> {
            int timeout = 10000;
            WSResponse response = WS.url("http://localhost:3333/usuarios/999/tareas/nueva")
                .setContentType("application/x-www-form-urlencoded")
                .post("descripcion=Entregar práctica 3 de MADS&estado=pendiente")
                .get(timeout);
            assertEquals(NOT_FOUND, response.getStatus());
            String body = response.getBody();
            assertTrue(body.contains(
                "404"));
            assertTrue(body.contains(
                "recurso no encontrado."));
        });
    }

    @Test
    public void testWebApiCreacionTareaDescripcionVacia() {
        running(testServer(3333, app), () -> {
            int timeout = 10000;
            WSResponse response = WS.url("http://localhost:3333/usuarios/1/tareas/nueva")
                .setContentType("application/x-www-form-urlencoded")
                .post("descripcion=&estado=pendiente")
                .get(timeout);
            assertEquals(BAD_REQUEST, response.getStatus());
            String body = response.getBody();
            assertTrue(body.contains(
                "La descripción no puede estar vacía."));
        });
    }

    @Test
    public void testWebApiCreacionTareaMensajeConfirmacion() {
        running(testServer(3333, app), () -> {
            int timeout = 10000;
            WSResponse response = WS.url("http://localhost:3333/usuarios/1/tareas/nueva")
                .setContentType("application/x-www-form-urlencoded")
                .post("descripcion=Entregar práctica 3 de MADS&estado=pendiente")
                .get(timeout);
            assertEquals(OK, response.getStatus());
            String body = response.getBody();
            assertTrue(body.contains(
                "Preparar el trabajo del tema 1 de biología"));
            assertTrue(body.contains(
                "Estudiar el parcial de matemáticas"));
            assertTrue(body.contains(
                "Leer el libro de inglés"));
            assertTrue(body.contains(
                "Entregar práctica 3 de MADS"));
                assertTrue(body.contains(
                    "La tarea se ha grabado correctamente."));
        });
    }

}
