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

    //Para cuando se modifica un usuario, mire si hay un login igual que no sea él mismo
    //Pues él mismo si puede repetir su login al modificar, pero no repetir login de otro usuario
    @Test
    public void testfindUsuarioByLoginNotIdDevuelveNull() {
        running (fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                //Primer usuario
                Usuario usuario = new Usuario();
                usuario.login = "pepe";
                usuario.password = "pass";
                UsuarioService.grabaUsuario(usuario);
                //Segundo usuario
                usuario = new Usuario();
                usuario.login = "paco";
                usuario.password = "pass";
                UsuarioService.grabaUsuario(usuario);
                //Busca un usuario (que no sea él mismo) que tenga el login que le envía
                Usuario usuario_encontrado = UsuarioService.findUsuarioByLoginNotId("paco", "2");
                assertEquals(null, usuario_encontrado);
            });
        });
    }

    @Test
    public void testdoModificarUsuario() {
        running (fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                Usuario usuario = new Usuario();
                usuario.login = "pepe";
                usuario.password = "pass";
                UsuarioService.grabaUsuario(usuario);
                usuario.eMail="pepe@gmail.com";
                UsuarioService.modificarUsuario(usuario);
                usuario = UsuarioService.findUsuario("1");
                assertEquals(usuario.eMail, "pepe@gmail.com");
            });
        });
    }

    @Test
    public void testdoDeleteUsuario() {
        running (fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                Usuario usuario = new Usuario();
                usuario.login = "pepe";
                usuario.password = "pepe";
                UsuarioService.grabaUsuario(usuario);
                UsuarioService.deleteUsuario("1");
                List<Usuario> listaUsuarios = UsuarioService.findAllUsuarios();
                assertTrue(listaUsuarios.size() == 0);
            });
        });
    }

}
