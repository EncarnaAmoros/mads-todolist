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
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            int timeout = 10000;
            WSResponse response = WS.url("http://localhost:3333/saludo?nombre=Encarna").get().get(timeout);
            assertEquals(OK, response.getStatus());
            assertTrue(response.getBody().contains("Hola Encarna, bienvenido a"));
        });
    }

    @Test
    public void testFormularioLogin() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            int timeout = 10000;
            WSResponse response = WS.url("http://localhost:3333/login").get().get(timeout);
            assertEquals(OK, response.getStatus());
            assertTrue(response.getBody().contains("Iniciar sesión"));
        });
    }

    @Test
    public void testdoLoginUsuarioNotFound() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            int timeout = 10000;
            WSResponse response = WS.url("http://localhost:3333/login")
                            .setContentType("application/x-www-form-urlencoded")
                            .post("login=encarna&password=password")
                            .get(timeout);
            assertTrue(response.getBody().contains("Lo sentimos, no se reconoce el usuario introducido"));
        });
    }

    @Test
    public void testdoLoginUsuarioAdmin() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            int timeout = 10000;
            WSResponse response = WS.url("http://localhost:3333/login")
                            .setContentType("application/x-www-form-urlencoded")
                            .post("login=admin&password=admin")
                            .get(timeout);
            assertEquals(OK, response.getStatus());
            assertTrue(response.getBody().contains("Listado de usuarios"));
        });
    }

    @Test
    public void testdoRegistraNuevoUsuario() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            int timeout = 10000;
            WSResponse response = WS.url("http://localhost:3333/usuarios/nuevo?n=1")
                        .setFollowRedirects(true)
                        .setContentType("application/x-www-form-urlencoded")
                        .post("login=Pepe&password=pepe&eMail=pepe@gmail.com")
                        .get(timeout);
            assertEquals(OK, response.getStatus());
            assertTrue(response.getBody().contains("Hola Pepe, bienvenido a"));
        });
    }

    @Test
    public void testdoCreaNuevoUsuario() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            int timeout = 8000;
            WSResponse response = WS.url("http://localhost:3333/usuarios/nuevo?n=0")
                        .setFollowRedirects(true)
                        .setContentType("application/x-www-form-urlencoded")
                        .post("login=Pep&nombre=Pep&apellidos=Guardiola&password=default")
                        .get(timeout);
            assertEquals(OK, response.getStatus());
            assertTrue(response.getBody().contains("Listado de usuarios"));
        });
    }

    @Test
    public void testListadoUsuarios() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            int timeout = 8000;
            WSResponse response = WS.url("http://localhost:3333/usuarios").get().get(timeout);
            assertEquals(OK, response.getStatus());
            assertTrue(response.getBody().contains("Listado de usuarios"));
        });
    }

    @Test
    public void testDetalleUsuarioNotFound() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            int timeout = 8000;
            WSResponse response = WS.url("http://localhost:3333/usuarios/10").get().get(timeout);
            assertEquals(NOT_FOUND, response.getStatus());
            assertTrue(response.getBody().contains("<h1>Error 404</h1>"));
        });
    }

    @Test
    public void testEditarUsuarioNotFound() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            int timeout = 8000;
            WSResponse response = WS.url("http://localhost:3333/usuarios/10/editar").get().get(timeout);
            assertEquals(NOT_FOUND, response.getStatus());
            assertTrue(response.getBody().contains("<h1>Error 404</h1>"));
        });
    }

}
