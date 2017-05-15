package cz.muni.fi.jdbc;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import java.time.LocalDate;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.Assert.fail;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.DERBY;

/**
 * Test class for LeaseManagerImpl
 */

public class LeaseManagerImplTest {

    private EmbeddedDatabase db;

    private LeaseManagerImpl manager;
    private CustomerManager customerManager;
    private MovieManager movieManager;

    @Before
    public void setUp() throws Exception {
        db = new EmbeddedDatabaseBuilder().setType(DERBY).addScript("schema-lease-javadb.sql").build();
        manager = new LeaseManagerImpl(db);
        movieManager = new MovieManagerImpl(db);
        manager.setMovieManager(movieManager);
        customerManager = new CustomerManagerImpl(db);
        manager.setCustomerManager(customerManager);
    }

    @After
    public void tearDown() throws Exception {
        db.shutdown();
    }

    @Test
    public void createLease() throws Exception {
        Lease lease = new LeaseBuilder().build();
        movieManager.createMovie(lease.getMovie());
        customerManager.createCustomer(lease.getCustomer());
        manager.createLease(lease);

        Long leaseId = lease.getId();
        assertThat(leaseId).isNotNull();
        Lease gotLease1 = manager.getLease(leaseId);
        assertThat(gotLease1).isNotNull();
        assertThat(lease).isEqualTo(gotLease1);
        assertThat(lease).isNotSameAs(gotLease1);

        lease = new LeaseBuilder().build();
        movieManager.createMovie(lease.getMovie());
        customerManager.createCustomer(lease.getCustomer());
        manager.createLease(lease);

        Lease gotLease2 = manager.getLease(lease.getId());
        assertThat(gotLease2).isNotNull();
        assertThat(lease).isEqualTo(gotLease2);
        assertThat(lease).isNotSameAs(gotLease2);

        assertThat(gotLease1).isNotEqualTo(gotLease2);
    }

    @Test
    public void addLeaseWithNullLease() throws Exception {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            manager.createLease(null);
        });
    }

    @Test
    public void addLeaseWithNullParameters() throws Exception {
        //Lease lease = new LeaseBuilder().withId(1L).build();

        Lease lease = new LeaseBuilder().withPrice(null).build();
        movieManager.createMovie(lease.getMovie());
        customerManager.createCustomer(lease.getCustomer());
        try {
            manager.createLease(lease);
            fail();
        } catch (IllegalArgumentException e) {
        }

        lease = new LeaseBuilder().withDateOfRent(null).build();
        movieManager.createMovie(lease.getMovie());
        customerManager.createCustomer(lease.getCustomer());
        try {
            manager.createLease(lease);
            fail();
        } catch (IllegalArgumentException e) {
        }

        lease = new LeaseBuilder().withExpectedDateOfReturn(null).build();
        movieManager.createMovie(lease.getMovie());
        customerManager.createCustomer(lease.getCustomer());
        try {
            manager.createLease(lease);
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void addLeaseWithWrongParameters() throws Exception {
        Lease lease = new LeaseBuilder().withId(1L).build();
        movieManager.createMovie(lease.getMovie());
        customerManager.createCustomer(lease.getCustomer());
        try {
            manager.createLease(lease);
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void getLeaseWithWrongParameters() throws Exception {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            manager.getLease(null);
        });

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            manager.getLease(-1L);
        });
    }

    @Test
    public void updateLease() throws Exception {
        Lease lease = new LeaseBuilder().withPrice(200).build();
        Lease lease2 = new LeaseBuilder()
                .withPrice(150)
                .withDateOfRent(LocalDate.of(2001, Month.JANUARY, 1))
                .withDateOfReturn(LocalDate.of(2001, Month.JANUARY, 30))
                .withExpectedDateOfReturn(LocalDate.of(2001, Month.JANUARY, 31))
                .build();
        movieManager.createMovie(lease.getMovie());
        customerManager.createCustomer(lease.getCustomer());
        movieManager.createMovie(lease2.getMovie());
        customerManager.createCustomer(lease2.getCustomer());
        manager.createLease(lease);
        manager.createLease(lease2);
        Long leaseId = lease.getId();
        Long lease2Id = lease2.getId();

        lease = manager.getLease(leaseId);
        lease.setDateOfRent(LocalDate.of(2005, Month.FEBRUARY, 10));
        lease.setExpectedDateOfReturn(LocalDate.of(2005, Month.FEBRUARY, 28));
        lease.setDateOfReturn(null);
        manager.updateLease(lease);

        lease = manager.getLease(leaseId);
        assertThat(LocalDate.of(2005, Month.FEBRUARY, 10)).isEqualTo(lease.getDateOfRent());
        assertThat(LocalDate.of(2005, Month.FEBRUARY, 28)).isEqualTo(lease.getExpectedDateOfReturn());
        assertThat(lease.getDateOfReturn()).isNull();

        assertThat(new Integer(200)).isEqualTo(lease.getPrice());

        lease2 = manager.getLease(lease2Id);
        assertThat(new Integer(150)).isEqualTo(lease2.getPrice());
        assertThat(LocalDate.of(2001, Month.JANUARY, 1)).isEqualTo(lease2.getDateOfRent());
        assertThat(LocalDate.of(2001, Month.JANUARY, 30)).isEqualTo(lease2.getDateOfReturn());
        assertThat(LocalDate.of(2001, Month.JANUARY, 31)).isEqualTo(lease2.getExpectedDateOfReturn());
    }

    @Test
    public void updateLeaseWithNullLease() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            manager.updateLease(null);
        });
    }

    @Test
    public void updateLeaseWithNullParameters() throws Exception{
        Lease lease = new LeaseBuilder().build();
        movieManager.createMovie(lease.getMovie());
        customerManager.createCustomer(lease.getCustomer());
        manager.createLease(lease);
        Long leaseId = lease.getId();

        try {
            lease = manager.getLease(leaseId);
            lease.setId(null);
            manager.updateLease(lease);
            fail();
        } catch (IllegalArgumentException e) {
        }

        try {
            lease = manager.getLease(leaseId);
            lease.setMovie(null);
            manager.updateLease(lease);
            fail();
        } catch (IllegalArgumentException e) {
        }

        try {
            lease = manager.getLease(leaseId);
            lease.setCustomer(null);
            manager.updateLease(lease);
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void updateLeaseWithWrongParameters() throws Exception{
        Lease lease = new LeaseBuilder().build();
        movieManager.createMovie(lease.getMovie());
        customerManager.createCustomer(lease.getCustomer());
        manager.createLease(lease);
        Long leaseId = lease.getId();

        try {
            lease = manager.getLease(leaseId);
            lease.setId(-1L);
            manager.updateLease(lease);
            fail();
        } catch (IllegalArgumentException e) {
        }

        try {
            lease = manager.getLease(leaseId);
            lease.setDateOfRent(LocalDate.of(2000, Month.JANUARY, 2));
            lease.setDateOfReturn(LocalDate.of(2000, Month.JANUARY, 1));
            manager.updateLease(lease);
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void deleteLease() throws Exception {
        Lease lease = new LeaseBuilder().build();
        Lease lease2 = new LeaseBuilder().build();
        movieManager.createMovie(lease.getMovie());
        customerManager.createCustomer(lease.getCustomer());
        movieManager.createMovie(lease2.getMovie());
        customerManager.createCustomer(lease2.getCustomer());
        manager.createLease(lease);
        manager.createLease(lease2);

        Long id = lease.getId();
        manager.deleteLease(lease);

        assertThat(manager.getLease(id)).isNull();
        assertThat(manager.getLease(lease2.getId())).isNotNull();
    }

    @Test
    public void deleteLeaseWithNullLease() throws Exception {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            manager.deleteLease(null);
        });
    }

    @Test
    public void deleteLeaseWithWrongParameters() throws Exception {
        Lease lease = new LeaseBuilder().build();
        movieManager.createMovie(lease.getMovie());
        customerManager.createCustomer(lease.getCustomer());
        manager.createLease(lease);

        try {
            lease.setId(null);
            manager.deleteLease(lease);
            fail();
        } catch (IllegalArgumentException e) {
        }

        try {
            lease.setId(-1L);
            manager.deleteLease(lease);
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void getAllLeases() throws Exception {
        assertThat(manager.getAllLeases()).isEmpty();

        Lease lease = new LeaseBuilder().build();
        Lease lease2 = new LeaseBuilder().build();
        movieManager.createMovie(lease.getMovie());
        customerManager.createCustomer(lease.getCustomer());
        movieManager.createMovie(lease2.getMovie());
        customerManager.createCustomer(lease2.getCustomer());
        manager.createLease(lease);
        manager.createLease(lease2);

        assertThat(manager.getAllLeases()).containsOnly(lease, lease2);
    }

    @Test
    public void findLeaseByCustomer() throws Exception {
        Customer customer1 = new CustomerBuilder().name("Pepa").build();
        Customer customer2 = new CustomerBuilder().name("Honza").build();
        customerManager.createCustomer(customer1);
        customerManager.createCustomer(customer2);

        assertThat(manager.findLeaseByCustomer(customer1))
                .isEmpty();

        Lease lease = new LeaseBuilder().withCustomer(customer1).build();
        Lease lease2 = new LeaseBuilder().withCustomer(customer2).build();
        movieManager.createMovie(lease.getMovie());
        customerManager.createCustomer(lease.getCustomer());
        movieManager.createMovie(lease2.getMovie());
        customerManager.createCustomer(lease2.getCustomer());
        manager.createLease(lease);
        manager.createLease(lease2);

        assertThat(manager.findLeaseByCustomer(customer1))
                .containsOnly(lease);
    }

    @Test
    public void findLeaseByMovie() throws Exception {

        Movie movie1 = new MovieBuilder().withName("Best").build();
        Movie movie2 = new MovieBuilder().withName("Movie").build();
        movieManager.createMovie(movie1);
        movieManager.createMovie(movie2);

        assertThat(manager.findLeaseByMovie(movie1))
                .isEmpty();

        Lease lease = new LeaseBuilder().withMovie(movie1).build();
        Lease lease2 = new LeaseBuilder().withMovie(movie2).build();
        movieManager.createMovie(lease.getMovie());
        customerManager.createCustomer(lease.getCustomer());
        movieManager.createMovie(lease2.getMovie());
        customerManager.createCustomer(lease2.getCustomer());
        manager.createLease(lease);
        manager.createLease(lease2);

        assertThat(manager.findLeaseByMovie(movie1))
                .containsOnly(lease);

    }

}