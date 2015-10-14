import org.junit.*;

import play.mvc.*;
import play.test.*;
import play.libs.F.*;

import play.libs.ws.*;
import play.Logger;

import static play.test.Helpers.*;
import static org.junit.Assert.*;

public class WebServiceTest {
    @Test
    public void testSaludo() {
        running(testServer(9000, fakeApplication(inMemoryDatabase())), () -> {
            int timeout = 4000;
            WSResponse response = WS.url("http://localhost:9000/saludo?nombre=Encarna").get().get(timeout);
            assertEquals(OK, response.getStatus());
            assertTrue(response.getBody().contains("Hola Encarna, bienvenido a"));
        });
    }

    @Test
    public void testFormularioLogin() {
        running(testServer(9000, fakeApplication(inMemoryDatabase())), () -> {
            int timeout = 4000;
            WSResponse response = WS.url("http://localhost:9000/login").get().get(timeout);
            assertEquals(OK, response.getStatus());
            assertTrue(response.getBody().contains("Iniciar sesión"));
        });
    }
}
