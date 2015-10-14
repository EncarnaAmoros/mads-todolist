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

    @Test
    public void testdoLoginUsuarioNotFound() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            int timeout = 4000;
            WSResponse response = WS.url("http://localhost:3333/login")
                            .setContentType("application/x-www-form-urlencoded")
                            .post("login=encarna&password=password")
                            .get(timeout);
            assertTrue(response.getBody().contains("Lo sentimos, no se reconoce el usuario introducido"));
        });
    }
}
