import org.junit.*;
import play.test.*;
import play.Application;
import play.mvc.*;
import static play.test.Helpers.*;
import static org.junit.Assert.*;

import play.db.jpa.*;
import java.util.List;
import models.*;

public class WithFakeApplicationTest {

    @Test
    public void testfindAllUsuariosDevuelveListaVacia() {
        running (fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                List<Usuario> listaUsuarios = UsuarioService.findAllUsuarios();
                assertTrue(listaUsuarios.isEmpty());
            });
        });
    }

    @Test
    public void testfindAllUsuariosDevuelveUnUsuario() {
        running (fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                Usuario usuario = new Usuario();
                usuario.login = "pepe";
                usuario.password = "pepe";
                UsuarioService.grabaUsuario(usuario);
                List<Usuario> listaUsuarios = UsuarioService.findAllUsuarios();
                assertTrue(listaUsuarios.size() == 1);
            });
        });
    }

    @Test
    public void testfindUsuarioDevuelveUnUsuario() {
        running (fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                Usuario usuario = new Usuario();
                usuario.login = "pepe";
                usuario.password = "pepe";
                UsuarioService.grabaUsuario(usuario);
                Usuario usuario_encontrado = UsuarioService.findUsuario("1");
                assertEquals(usuario.login, usuario_encontrado.login);
            });
        });
    }

    @Test
    public void testfindUsuarioByLoginPasswordDevuelveUnUsuario() {
        running (fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                Usuario usuario = new Usuario();
                usuario.login = "pepe";
                usuario.password = "pass";
                UsuarioService.grabaUsuario(usuario);
                Usuario usuario_encontrado = UsuarioService.findUsuarioByLoginPassword("pepe","pass");
                assertEquals(usuario.login, usuario_encontrado.login);
            });
        });
    }

    @Test
    public void testfindUsuarioByLoginDevuelveUnUsuario() {
        running (fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                Usuario usuario = new Usuario();
                usuario.login = "pepe";
                usuario.password = "pass";
                UsuarioService.grabaUsuario(usuario);
                Usuario usuario_encontrado = UsuarioService.findUsuarioByLogin("pepe");
                assertEquals(usuario.login, usuario_encontrado.login);
            });
        });
    }


}
