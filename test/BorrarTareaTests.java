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

public class BorrarTareaTests {

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
    public void testDeleteTarea() {
        running (app, () -> {
            JPA.withTransaction(() -> {
                Usuario usuario = UsuarioDAO.find(1);
                List<Tarea> tareas = usuario.tareas;
                TareaDAO.delete(tareas.get(1).id);
                tareas = usuario.tareas;
                assertEquals(tareas.size(), 2);
                assertTrue(tareas.contains(
                    new Tarea(usuario, "Preparar el trabajo del tema 1 de biología")));
                assertTrue(tareas.contains(
                    new Tarea(usuario, "Leer el libro de inglés")));
            });
        });
    }

    @Test
    public void testDeleteTareaService() {
        running (app, () -> {
            JPA.withTransaction(() -> {
                Usuario usuario = UsuarioDAO.find(1);
                List<Tarea> tareas = usuario.tareas;
                TareaService.deleteTarea(tareas.get(1).id);
                tareas = usuario.tareas;
                assertEquals(tareas.size(), 2);
                assertTrue(tareas.contains(
                    new Tarea(usuario, "Preparar el trabajo del tema 1 de biología")));
                assertTrue(tareas.contains(
                    new Tarea(usuario, "Leer el libro de inglés")));
            });
        });
    }

    @Test
    public void testWebApiBorraTareaMensajeConfirmacion() {
        running(testServer(3333, app), () -> {
            int timeout = 10000;
            WSResponse response = WS.url("http://localhost:3333/usuarios/1/tareas/2")
                .setContentType("application/x-www-form-urlencoded")
                .delete()
                .get(timeout);
            assertEquals(OK, response.getStatus());
            String body = response.getBody();
            assertTrue(body.contains(
                "Preparar el trabajo del tema 1 de biología"));
            assertTrue(body.contains(
                "Leer el libro de inglés"));
            assertTrue(body.contains(
                "La tarea se ha borrado correctamente."));
        });
    }

    @Test
    public void testWebApiBorraTareaUsuarioNotFound() {
        running(testServer(3333, app), () -> {
            int timeout = 10000;
            WSResponse response = WS.url("http://localhost:3333/usuarios/9999/tareas/2")
                .setContentType("application/x-www-form-urlencoded")
                .delete()
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
    public void testWebApiBorraTareaNotFound() {
        running(testServer(3333, app), () -> {
            int timeout = 10000;
            WSResponse response = WS.url("http://localhost:3333/usuarios/1/tareas/99999")
                .setContentType("application/x-www-form-urlencoded")
                .delete()
                .get(timeout);
            assertEquals(NOT_FOUND, response.getStatus());
            String body = response.getBody();
            assertTrue(body.contains(
                "404"));
            assertTrue(body.contains(
                "recurso no encontrado."));
        });
    }

}
