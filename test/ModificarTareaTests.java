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
                List<Tarea> tareas = usuario.tareas;
                Integer pos_tarea = tareas.size()-2;
                tareas.get(pos_tarea).descripcion = "Preparar el parcial de MADS";
                TareaDAO.update(tareas.get(pos_tarea));
                tareas = usuario.tareas;
                assertEquals(tareas.get(pos_tarea).descripcion, "Preparar el parcial de MADS");
            });
        });
    }

    @Test
    public void testModificarTarea() {
        running (app, () -> {
            JPA.withTransaction(() -> {
                Usuario usuario = UsuarioDAO.find(1);
                List<Tarea> tareas = usuario.tareas;
                Integer pos_tarea = tareas.size()-2;
                tareas.get(pos_tarea).descripcion = "Preparar el super parcial de MADS";
                TareaService.modificarTarea(tareas.get(pos_tarea));
                tareas = usuario.tareas;
                assertEquals(tareas.get(pos_tarea).descripcion, "Preparar el super parcial de MADS");
            });
        });
    }

}
