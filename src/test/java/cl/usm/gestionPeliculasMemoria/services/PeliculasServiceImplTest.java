package cl.usm.gestionPeliculasMemoria.services;
import cl.usm.gestionPeliculasMemoria.entities.Pelicula;
import cl.usm.gestionPeliculasMemoria.repositories.PeliculasRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PeliculasServiceImplTest {

    @Mock
    private PeliculasRepository peliculasRepository;
    @InjectMocks
    private PeliculasServiceImpl peliculasService;

     @Test
    void createPelicula_exitoso_debeRetornarPeliculaConToken() {
        Pelicula entrada = new Pelicula("p1", "Avatar", "Cameron", null, null);
        when(peliculasRepository.insert(any(Pelicula.class))).thenReturn(entrada);
        Pelicula resultado = peliculasService.createPelicula(entrada);

        assertNotNull(resultado);
        assertNotNull(resultado.getTokenDescarga());
        verify(peliculasRepository, times(1)).insert(entrada);
    }

    @Test
    void createPelicula_repositorioLanzaExcepcion_debeRetornarNull() {
        Pelicula entrada = new Pelicula("p1", "Avatar", "Cameron", null, null);
        when(peliculasRepository.insert(any(Pelicula.class)))
                .thenThrow(new IllegalArgumentException("ID duplicado"));

        Pelicula resultado = peliculasService.createPelicula(entrada);

        assertNull(resultado);
    }

    @Test
    void createPelicula_debeAsignarTokenDeDescargaAlphanumerico() {
        Pelicula entrada = new Pelicula("p2", "Dune", "Villeneuve", null, null);
        when(peliculasRepository.insert(any(Pelicula.class))).thenAnswer(inv -> inv.getArgument(0));

        Pelicula resultado = peliculasService.createPelicula(entrada);

        assertNotNull(resultado.getTokenDescarga());
        assertEquals(10, resultado.getTokenDescarga().length());
        assertTrue(resultado.getTokenDescarga().matches("[a-zA-Z0-9]+"));
    }

    


    @Test
    void getAll_debeRetornarListaDesdRepositorio() {
        List<Pelicula> peliculas = Arrays.asList(
                new Pelicula("id1", "Pelicula A", "Director A", null, null),
                new Pelicula("id2", "Pelicula B", "Director B", null, null)
        );
        when(peliculasRepository.findAll()).thenReturn(peliculas);

        List<Pelicula> resultado = peliculasService.getAll();
        assertEquals(2, resultado.size());
        verify(peliculasRepository, times(1)).findAll();
    }

    @Test
    void getAll_repositorioVacio_debeRetornarListaVacia() {
        when(peliculasRepository.findAll()).thenReturn(Collections.emptyList());

        List<Pelicula> resultado = peliculasService.getAll();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

   
    @Test
    void findById_idExistente_debeRetornarPelicula() {
        Pelicula pelicula = new Pelicula("id1", "Interstellar", "Nolan", null, null);
        when(peliculasRepository.findById("id1")).thenReturn(pelicula);
        Pelicula resultado = peliculasService.findById("id1");

        assertNotNull(resultado);
        assertEquals("id1", resultado.getId());
        verify(peliculasRepository, times(1)).findById("id1");
    }

    @Test
    void findById_idInexistente_debeRetornarNull() {
        when(peliculasRepository.findById("noExiste")).thenReturn(null);

        Pelicula resultado = peliculasService.findById("noExiste");

        assertNull(resultado);
    }

    // ── filter ───────────────────────────────────────────────────────────────

    @Test
    void filter_porTitulo_debeRetornarPeliculasQueCoinciden() {
        List<Pelicula> todas = Arrays.asList(
                new Pelicula("1", "El Padrino", "Coppola", null, null),
                new Pelicula("2", "Matrix", "Wachowski", null, null),
                new Pelicula("3", "El Padrino 2", "Coppola", null, null)
        );
        when(peliculasRepository.findAll()).thenReturn(todas);

        List<Pelicula> resultado = peliculasService.filter("padrino");

        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(p -> p.getTitulo().toLowerCase().contains("padrino")));
    }

    @Test
    void filter_porId_debeRetornarPeliculasQueCoinciden() {
        List<Pelicula> todas = Arrays.asList(
                new Pelicula("MOVIE-001", "Titulo 1", "Director 1", null, null),
                new Pelicula("SERIE-002", "Titulo 2", "Director 2", null, null)
        );
        when(peliculasRepository.findAll()).thenReturn(todas);

        List<Pelicula> resultado = peliculasService.filter("movie");

        assertEquals(1, resultado.size());
        assertEquals("MOVIE-001", resultado.get(0).getId());
    }

    @Test
    void filter_sinCoincidencias_debeRetornarListaVacia() {
        List<Pelicula> todas = Arrays.asList(
                new Pelicula("id1", "Matrix", "Wachowski", null, null)
        );
        when(peliculasRepository.findAll()).thenReturn(todas);

        List<Pelicula> resultado = peliculasService.filter("avatar");

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void filter_caseInsensitive_debeEncontrarResultados() {
        List<Pelicula> todas = Arrays.asList(
                new Pelicula("id1", "BATMAN", "Nolan", null, null)
        );
        when(peliculasRepository.findAll()).thenReturn(todas);

        List<Pelicula> resultado = peliculasService.filter("batman");

        assertEquals(1, resultado.size());
    }
}
