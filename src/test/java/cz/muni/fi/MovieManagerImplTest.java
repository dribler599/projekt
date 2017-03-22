package cz.muni.fi;

import org.junit.Before;
import org.junit.Test;
import org.junit.*;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Test class for MovieManagerImpl
 */
public class MovieManagerImplTest {
    private MovieManager manager;

    @Before
    public void setUp() throws Exception {
        manager = new MovieManagerImpl();
    }

    @Test
    public void createMovie() throws Exception {
        Movie movie = new MovieBuilder().build();
        manager.createMovie(movie);

        Long movieId = movie.getId();
        assertThat(movieId).isNotNull();
        Movie gotMovie1 = manager.getMovie(movieId);
        assertThat(gotMovie1).isNotNull();
        assertThat(movie).isEqualTo(gotMovie1);
        assertThat(movie).isNotSameAs(gotMovie1);

        movie = new MovieBuilder().build();
        manager.createMovie(movie);

        Movie gotMovie2 = manager.getMovie(movie.getId());
        assertThat(gotMovie2).isNotNull();
        assertThat(movie).isEqualTo(gotMovie2);
        assertThat(movie).isNotSameAs(gotMovie2);

        assertThat(gotMovie1).isNotEqualTo(gotMovie2);
    }

    @Test
    public void addMovieWithNullMovie() throws Exception {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            manager.createMovie(null);
        });
    }

    @Test
    public void addMovieWithWrongParameters() throws Exception {
        Movie movie = new MovieBuilder().withId(123L).build();
        try {
            manager.createMovie(movie);
            fail();
        } catch (IllegalArgumentException e) {
        }

        movie = new MovieBuilder().withClassification("25").build();
        try {
            manager.createMovie(movie);
            fail();
        } catch (IllegalArgumentException e) {
        }

        movie = new MovieBuilder().withId(-1L).build();
        try {
            manager.createMovie(movie);
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void getMovieWithNullMovie() throws Exception {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            manager.getMovie(null);
        });
    }

    @Test
    public void getMovieWithWrongParameters() throws Exception {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            manager.getMovie(-1L);
        });
    }

    @Test
    public void updateMovie() throws Exception {
        Movie movie = new MovieBuilder().withLocation("doma").build();
        Movie movie2 = new MovieBuilder()
                .withName("Krstný otec")
                .withYear(1972)
                .withClassification("18")
                .withDescription("Nejaký popis, autori, žánre")
                .build();
        manager.createMovie(movie);
        manager.createMovie(movie2);
        Long movieId = movie.getId();
        Long movie2Id = movie2.getId();

        movie = manager.getMovie(movieId);
        movie.setName("Pápež Ján Pavol II");
        movie.setYear(1988);
        movie.setClassification("15");
        movie.setDescription(null);
        manager.updateMovie(movie);

        movie = manager.getMovie(movieId);
        assertThat("Pápež Ján Pavol II.").isEqualTo(movie.getName());
        assertThat(new Integer(1988)).isEqualTo(movie.getYear());
        assertThat("15").isEqualTo(movie.getClassification());
        assertThat(movie.getDescription()).isNull();

        assertThat("doma").isEqualTo(movie.getLocation());

        movie2 = manager.getMovie(movie2Id);
        assertThat("Krstný otec").isEqualTo(movie2.getName());
        assertThat(new Integer(1972)).isEqualTo(movie2.getYear());
        assertThat("18").isEqualTo(movie2.getClassification());
        assertThat("Nejaký popis, autori, žánre").isEqualTo(movie2.getDescription());
    }

    @Test
    public void updateMovieWithNullMovie() throws Exception {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            manager.updateMovie(null);
        });
    }

    @Test
    public void updateMovieWithNullParameters() throws Exception {
        Movie movie = new MovieBuilder().build();
        manager.createMovie(movie);
        Long movieId = movie.getId();

        try {
            movie = manager.getMovie(movieId);
            movie.setId(null);
            manager.updateMovie(movie);
            fail();
        } catch (IllegalArgumentException e) {
        }

        try {
            movie = manager.getMovie(movieId);
            movie.setClassification(null);
            manager.updateMovie(movie);
            fail();
        } catch (IllegalArgumentException e) {
        }

        try {
            movie = manager.getMovie(movieId);
            movie.setName(null);
            manager.updateMovie(movie);
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void updateMovieWithWrongParameters() throws Exception {
        Movie movie = new MovieBuilder().build();
        manager.createMovie(movie);
        Long movieId = movie.getId();

        try {
            movie = manager.getMovie(movieId);
            movie.setId(-1L);
            manager.updateMovie(movie);
            fail();
        } catch (IllegalArgumentException e) {
        }

        try {
            movie = manager.getMovie(movieId);
            movie.setYear(-1500);
            manager.updateMovie(movie);
            fail();
        } catch (IllegalArgumentException e) {
        }

        try {
            movie = manager.getMovie(movieId);
            movie.setClassification("18A");
            manager.updateMovie(movie);
            fail();
        } catch (IllegalArgumentException e) {
        }

        try {
            movie = manager.getMovie(movieId);
            movie.setLocation("&§?");
            manager.updateMovie(movie);
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void deleteMovie() throws Exception {
        Movie movie = new MovieBuilder().build();
        Movie movie2 = new MovieBuilder().build();
        manager.createMovie(movie);
        manager.createMovie(movie2);

        manager.deleteMovie(movie);

        assertThat(manager.getMovie(movie.getId())).isNull();
        assertThat(manager.getMovie(movie2.getId())).isNotNull();
    }

    @Test
    public void deleteMovieWithNullMovie() throws Exception {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            manager.deleteMovie(null);
        });
    }

    @Test
    public void deleteMovieWithWrongParameters() throws Exception {
        Movie movie = new MovieBuilder().build();
        manager.createMovie(movie);

        try {
            movie.setId(null);
            manager.deleteMovie(movie);
            fail();
        } catch (IllegalArgumentException e) {
        }

        try {
            movie.setId(-1L);
            manager.deleteMovie(movie);
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void getAllMovies() throws Exception {
        assertThat(manager.getAllMovies()).isEmpty();

        Movie movie = new MovieBuilder().build();
        Movie movie2 = new MovieBuilder().build();
        manager.createMovie(movie);
        manager.createMovie(movie2);

        assertThat(manager.getAllMovies()).containsOnly(movie, movie2);
    }

    @Test
    public void getMovieByName() throws Exception {
        Movie movie = new MovieBuilder().withName("Sedem").build();
        Movie movie2 = new MovieBuilder().withName("Zelená míľa ").build();
        manager.createMovie(movie);
        manager.createMovie(movie2);

        assertThat(manager.getMovieByName("Sedem")).containsOnly(movie);
    }

    @Test
    public void getMovieByNameWithWrongParameters() throws Exception {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            manager.getMovieByName(null);
        });
    }
}