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
}
