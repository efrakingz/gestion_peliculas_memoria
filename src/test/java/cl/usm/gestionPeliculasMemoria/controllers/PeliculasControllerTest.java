package cl.usm.gestionPeliculasMemoria.controllers;
import cl.usm.gestionPeliculasMemoria.entities.Pelicula;
import cl.usm.gestionPeliculasMemoria.services.PeliculasService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PeliculasControllerTest {

    @Mock
    private PeliculasService peliculasService;

    @InjectMocks
    private PeliculasController peliculasController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(peliculasController)
                .setValidator(validator)
                .build();
    }

   
    @Test
    void getAll_sinFiltro_debeRetornar200ConListaDePeliculas() throws Exception {
        List<Pelicula> peliculas = Arrays.asList(
                new Pelicula("1", "El Padrino", "Coppola", "token1", null),
                new Pelicula("2", "Matrix", "Wachowski", "token2", null)
        );
        when(peliculasService.getAll()).thenReturn(peliculas);

        mockMvc.perform(get("/peliculas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[1].id").value("2"));
    }

    @Test
    void getAll_conFiltroQ_debeUsarServiceFilter() throws Exception {
        List<Pelicula> filtradas = List.of(
                new Pelicula("1", "El Padrino", "Coppola", "token1", null)
        );
        when(peliculasService.filter("padrino")).thenReturn(filtradas);

        mockMvc.perform(get("/peliculas").param("q", "padrino"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].titulo").value("El Padrino"));
    }

    @Test
    void getAll_listaVacia_debeRetornar200ConListaVacia() throws Exception {
        when(peliculasService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/peliculas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getAll_servicioLanzaExcepcion_debeRetornar500() throws Exception {
        when(peliculasService.getAll()).thenThrow(new RuntimeException("Error interno"));

        mockMvc.perform(get("/peliculas"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void createPelicula_peliculaValida_debeRetornar200ConPeliculaCreada() throws Exception {
        Pelicula creada = new Pelicula("p1", "Titanic", "Cameron", "abc1234567", null);
        when(peliculasService.createPelicula(any(Pelicula.class))).thenReturn(creada);

        String body = "{\"id\":\"p1\",\"titulo\":\"Titanic\",\"director\":\"Cameron\"}";

        mockMvc.perform(post("/peliculas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("p1"))
                .andExpect(jsonPath("$.tokenDescarga").value("abc1234567"));
    }

    @Test
    void createPelicula_servicioRetornaNull_debeRetornar500() throws Exception {
        when(peliculasService.createPelicula(any(Pelicula.class))).thenReturn(null);

        String body = "{\"id\":\"p1\",\"titulo\":\"Titanic\",\"director\":\"Cameron\"}";

        mockMvc.perform(post("/peliculas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void createPelicula_campoTituloVacio_debeRetornar400() throws Exception {
        String body = "{\"id\":\"p1\",\"titulo\":\"\",\"director\":\"Cameron\"}";

        mockMvc.perform(post("/peliculas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPelicula_campoDirectorVacio_debeRetornar400() throws Exception {
        String body = "{\"id\":\"p1\",\"titulo\":\"Titanic\",\"director\":\"\"}";

        mockMvc.perform(post("/peliculas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }


    @Test
    void findById_idExistente_debeRetornar200ConPelicula() throws Exception {
        Pelicula pelicula = new Pelicula("id1", "Interstellar", "Nolan", "token99", null);
        when(peliculasService.findById("id1")).thenReturn(pelicula);
        mockMvc.perform(get("/peliculas/id1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("id1"))
                .andExpect(jsonPath("$.titulo").value("Interstellar"));
    }

    @Test
    void findById_idInexistente_debeRetornar404() throws Exception {
        when(peliculasService.findById("noExiste")).thenReturn(null);
        mockMvc.perform(get("/peliculas/noExiste"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findById_servicioLanzaExcepcion_debeRetornar500() throws Exception {
        when(peliculasService.findById(anyString())).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/peliculas/error"))
                .andExpect(status().isInternalServerError());
    }

   
    @Test
    void getComentarios_peliculaExistente_debeRetornar200() throws Exception {
        Pelicula pelicula = new Pelicula("id1", "Avatar", "Cameron", "tok", null);
        when(peliculasService.findById("id1")).thenReturn(pelicula);

        mockMvc.perform(get("/peliculas/id1/comentarios"))
                .andExpect(status().isOk());
    }

    @Test
    void getComentarios_peliculaNoExiste_debeRetornar404() throws Exception {
        when(peliculasService.findById("noExiste")).thenReturn(null);

        mockMvc.perform(get("/peliculas/noExiste/comentarios"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getComentarios_servicioLanzaExcepcion_debeRetornar500() throws Exception {
        when(peliculasService.findById(anyString())).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/peliculas/error/comentarios"))
                .andExpect(status().isInternalServerError());
}
}
