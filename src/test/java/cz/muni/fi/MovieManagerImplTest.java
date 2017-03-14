package cz.muni.fi;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

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
        assertNotNull(movieId);
        Movie gotMovie1 = manager.getMovie(movieId);
        assertNotNull(gotMovie1);
        assertEquals(movie, gotMovie1);
        assertNotSame(movie, gotMovie1);

        movie = new MovieBuilder().withId(321L).build();
        manager.createMovie(movie);

        Movie gotMovie2 = manager.getMovie(movie.getId());
        assertNotNull(gotMovie2);
        assertEquals(movie, gotMovie2);

        assertNotEquals(gotMovie1, gotMovie2);
    }

    @Test
    public void addMovieWithWrongParameters() throws Exception {
        try {
            manager.createMovie(null);
            fail();
        } catch (IllegalArgumentException e) {
        }


        Movie movie = new MovieBuilder().withId(null).build();
        try {
            manager.createMovie(movie);
            fail();
        } catch (IllegalArgumentException e) {
        }

        movie = new MovieBuilder().withName(";").build();
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
    public void updateMovie() throws Exception {
        Movie movie = new MovieBuilder().withId(123L).withLocation("doma").build();
        Movie movie2 = new MovieBuilder()
                .withId(555L)
                .withName("Ján Jakub")
                .withYear(1999)
                .withClassification("18")
                .withDescription("Nejaký popis, autori, žánre")
                .build();
        manager.createMovie(movie);
        manager.createMovie(movie2);
        Long movieId = movie.getId();
        Long movie2Id = movie2.getId();

        movie = manager.getMovie(movieId);
        movie.setName("Peter Pavol");
        movie.setYear(1988);
        movie.setClassification("15");
        movie.setDescription(null);
        manager.updateMovie(movie);

        movie = manager.getMovie(movieId);
        assertEquals("PeterPavol", movie.getName());
        assertEquals(new Integer(1988), movie.getYear());
        assertEquals("15", movie.getClassification());
        assertNull(movie.getDescription());

        assertEquals("doma", movie.getLocation());

        movie2 = manager.getMovie(movie2Id);
        assertEquals("Ján Jakub", movie2.getName());
        assertEquals(new Integer(1999), movie2.getYear());
        assertEquals("18", movie2.getClassification());
        assertEquals("Nejaký popis, autori, žánre", movie2.getDescription());
    }

    @Test
    public void updateMovieWithWrongParameters() throws Exception {
        Movie movie = new MovieBuilder().withId(123L).build();
        manager.createMovie(movie);
        Long movieId = movie.getId();
        Movie movie2 = new MovieBuilder().withId(321L).build();
        manager.createMovie(movie2);
        Long movie2Id = movie2.getId();

        try {
            manager.updateMovie(null);
            fail();
        } catch (IllegalArgumentException e) {
        }

        try {
            movie = manager.getMovie(movieId);
            movie.setId(-1L);
            manager.updateMovie(movie);
            fail();
        } catch (IllegalArgumentException e) {
        }

        try {
            movie = manager.getMovie(movieId);
            movie.setId(movie2Id);
            manager.updateMovie(movie);
            fail();
        } catch (IllegalArgumentException e) {
        }

        try {
            movie = manager.getMovie(movieId);
            movie.setId(null);
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
            movie.setClassification(null);
            manager.updateMovie(movie);
            fail();
        } catch (IllegalArgumentException e) {
        }

        try {
            movie = manager.getMovie(movieId);
            movie.setName("malé začiatočné písmená");
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
        Movie movie = new MovieBuilder().withId(123L).build();
        Movie movie2 = new MovieBuilder().withId(321L).build();
        manager.createMovie(movie);
        manager.createMovie(movie2);

        manager.deleteMovie(movie);

        assertNull(manager.getMovie(movie.getId()));
        assertNotNull(manager.getMovie(movie2.getId()));
    }

    @Test
    public void deleteMovieWithWrongParameters() throws Exception {
        Movie movie = new MovieBuilder().withId(123L).build();
        manager.createMovie(movie);

        try {
            manager.deleteMovie(null);
            fail();
        } catch (IllegalArgumentException e) {
        }

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
}