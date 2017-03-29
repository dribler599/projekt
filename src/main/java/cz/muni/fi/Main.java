package cz.muni.fi;

import org.apache.commons.dbcp2.BasicDataSource;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Properties;

/**
 * Main class
 */
public class Main {

    public static void main(String[] args) throws MovieException, IOException {

        Properties myconf = new Properties();
        myconf.load(Main.class.getResourceAsStream("/myconf.properties"));

        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(myconf.getProperty("jdbc.url"));
        ds.setUsername(myconf.getProperty("jdbc.user"));
        ds.setPassword(myconf.getProperty("jdbc.password"));

        MovieManager movieManager = new MovieManagerImpl(ds);
        Movie movie = new Movie(null, "Mafia", 1980, null,
                null, null);
        movieManager.createMovie(movie);

        CustomerManager customerManager = new CustomerManagerImpl(ds);
        Customer customer = new Customer(null, "Tomáš", LocalDate.of(2000, Month.JANUARY, 1), null,
                null, null);
        customerManager.createCustomer(customer);

        LeaseManager leaseManager = new LeaseManagerImpl(ds);
        leaseManager.setMovieManager(movieManager);
        leaseManager.setCustomerManager(customerManager);
        Lease lease = new Lease(null, movie, customer, 200,
                LocalDate.of(2000, Month.JANUARY, 1), LocalDate.of(2001, Month.JANUARY, 1),
                LocalDate.of(2000, Month.FEBRUARY, 1));
        leaseManager.createLease(lease);
        System.out.println(leaseManager.getLease(lease.getId()));

        List<Movie> allMovies = movieManager.getAllMovies();
        allMovies.forEach(System.out::println);
        List<Customer> allCustomers = customerManager.getAllCustomers();
        allCustomers.forEach(System.out::println);
        List<Lease> allLeases = leaseManager.getAllLeases();
        allLeases.forEach(System.out::println);

        movieManager.deleteMovie(movie);
        customerManager.deleteCustomer(customer);
    }
}
