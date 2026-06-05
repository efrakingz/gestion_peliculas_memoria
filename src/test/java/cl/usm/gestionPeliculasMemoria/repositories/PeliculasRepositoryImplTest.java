package cl.usm.gestionPeliculasMemoria.repositories;
import cl.usm.gestionPeliculasMemoria.entities.Pelicula;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PeliculasRepositoryImplTest {

    private PeliculasRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new PeliculasRepositoryImpl();
    }

       @Test
    void insert_debeRetornarPeliculaInsertada() {
        Pelicula p = new Pelicula("p1", "El Señor de los Anillos", "Peter Jackson", null, null);
        Pelicula resultado = repository.insert(p);

        assertNotNull(resultado);
        assertEquals("p1", resultado.getId());
        assertEquals("El Señor de los Anillos", resultado.getTitulo());
    }

    @Test
    void insert_debeAgregarPeliculaAlStorage() {
        Pelicula p = new Pelicula("p2", "Matrix", "Wachowski", null, null);
        repository.insert(p);

        List<Pelicula> todas = repository.findAll();
        assertEquals(1, todas.size());
        assertEquals("p2", todas.get(0).getId());
    }

    @Test
    void insert_idNulo_debeLanzarIllegalArgumentException() {
        Pelicula p = new Pelicula(null, "Sin ID", "Nadie", null, null);

        assertThrows(IllegalArgumentException.class, () -> repository.insert(p));
    }

    @Test
    void insert_idDuplicado_debeLanzarIllegalArgumentException() {
        Pelicula p1 = new Pelicula("p3", "Inception", "Nolan", null, null);
        repository.insert(p1);
        Pelicula p2 = new Pelicula("p3", "Inception 2", "Nolan", null, null);
        assertThrows(IllegalArgumentException.class, () -> repository.insert(p2));
    }

    @Test
    void insert_idDuplicadoCaseInsensitive_debeLanzarExcepcion() {
        repository.insert(new Pelicula("ABC", "Titulo", "Director", null, null));

        assertThrows(IllegalArgumentException.class,
                () -> repository.insert(new Pelicula("abc", "Titulo 2", "Director 2", null, null)));
    }

    @Test
    void findAll_repositorioVacio_debeRetornarListaVacia() {
        List<Pelicula> resultado = repository.findAll();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void findAll_conPeliculas_debeRetornarTodasLasPeliculas() {
        repository.insert(new Pelicula("id1", "Pelicula 1", "Director 1", null, null));
        repository.insert(new Pelicula("id2", "Pelicula 2", "Director 2", null, null));
        List<Pelicula> resultado = repository.findAll();
        assertEquals(2, resultado.size());
    }

    @Test
    void findAll_debeRetornarCopiaIndependiente() {
        repository.insert(new Pelicula("id1", "Pelicula 1", "Director 1", null, null));

        List<Pelicula> lista1 = repository.findAll();
        List<Pelicula> lista2 = repository.findAll();

        assertNotSame(lista1, lista2);
    }

    @Test
    void findById_idExistente_debeRetornarPelicula() {
        repository.insert(new Pelicula("id10", "Titanic", "James Cameron", null, null));

        Pelicula resultado = repository.findById("id10");

        assertNotNull(resultado);
        assertEquals("id10", resultado.getId());
        assertEquals("Titanic", resultado.getTitulo());
    }

    @Test
    void findById_idInexistente_debeRetornarNull() {
        Pelicula resultado = repository.findById("noExiste");
        assertNull(resultado);
    }

    @Test
    void findById_idNulo_debeRetornarNull() {
        Pelicula resultado = repository.findById(null);

        assertNull(resultado);
    }

    @Test
    void findById_caseInsensitive_debeEncontrarPelicula() {
        repository.insert(new Pelicula("MYID", "Pelicula", "Director", null, null));

        Pelicula resultado = repository.findById("myid");

        assertNotNull(resultado);
        assertEquals("MYID", resultado.getId());
    }
}
