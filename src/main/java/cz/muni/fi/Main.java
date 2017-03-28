package cz.muni.fi;

/**
 * Created by Tomas on 27. 3. 2017.
 */

import org.apache.commons.dbcp2.BasicDataSource;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class Main {

    public static void main(String[] args) throws MovieException, IOException {

        Properties myconf = new Properties();
        myconf.load(Main.class.getResourceAsStream("/myconf.properties"));

        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(myconf.getProperty("jdbc.url"));
        ds.setUsername(myconf.getProperty("jdbc.user"));
        ds.setPassword(myconf.getProperty("jdbc.password"));

        MovieManager movieManager = new MovieManagerImpl(ds);
        movieManager.createMovie(new Movie(null, "Tomáš", 1980, null, null, null));

        List<Movie> allMovies = movieManager.getAllMovies();
        allMovies.forEach(System.out::println);

    }
}
