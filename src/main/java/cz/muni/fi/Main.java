package cz.muni.fi;

/**
 * Created by Tomas on 27. 3. 2017.
 */

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.derby.jdbc.EmbeddedDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.util.List;

public class Main {

    final static Logger log = LoggerFactory.getLogger(Main.class);

    public static DataSource createMemoryDatabase() {
        BasicDataSource bds = new BasicDataSource();

        bds.setDriverClassName(EmbeddedDriver.class.getName());
        bds.setUrl("jdbc:derby:memory:booksDB;create=true");

        new ResourceDatabasePopulator(
                new ClassPathResource("schema-javadb.sql"),
                new ClassPathResource("test-data.sql"))
                .execute(bds);
        return bds;
    }

    public static void main(String[] args) throws MovieException {

        log.info("Start");

        DataSource dataSource = createMemoryDatabase();
        MovieManager bookManager = new MovieManagerImpl(dataSource);

        List<Movie> allBooks = bookManager.getAllMovies();
        System.out.println("allBooks = " + allBooks);

    }

}
