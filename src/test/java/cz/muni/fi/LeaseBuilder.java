package cz.muni.fi;

import java.time.LocalDate;
import java.time.Month;

/**
 * Builder of movies for tests
 */
public class LeaseBuilder {
    private Long id = 100L;
    private Movie movie = new MovieBuilder().build();
    private Customer customer = new Customer(10L,"Petr", LocalDate.of(2014, Month.JANUARY, 1),
            "PetrStreet1","Petr@mail.null", "111111111");
    private Integer price = 1000;
    private LocalDate dateOfRent = LocalDate.of(2000, Month.JANUARY, 1);
    private LocalDate dateOfReturn = LocalDate.of(2000, Month.JANUARY, 30);
    private LocalDate expectedDateOfReturn = LocalDate.of(2000, Month.JANUARY, 31);

    public Lease build()
    {
        Lease lease =  new Lease();
        lease.setId(id);
        lease.setMovie(movie);
        lease.setCustomer(customer);
        lease.setPrice(price);
        lease.setDateOfRent(dateOfRent);
        lease.setDateOfReturn(dateOfReturn);
        lease.setExpectedDateOfReturn(expectedDateOfReturn);
        return lease;
    }

    public LeaseBuilder withMovie(Movie movie)
    {
        this.movie = movie;
        return this;
    }

    public LeaseBuilder withCustomer(Customer customer)
    {
        this.customer = customer;
        return this;
    }

    public LeaseBuilder withPrice(Integer price)
    {
        this.price = price;
        return this;
    }

    public LeaseBuilder withDateOfRent(LocalDate dateOfRent)
    {
        this.dateOfRent = dateOfRent;
        return this;
    }

    public LeaseBuilder withDateOfReturn(LocalDate dateOfReturn)
    {
        this.dateOfReturn = dateOfReturn;
        return this;
    }

    public LeaseBuilder withExpectedDateOfReturn(LocalDate expectedDateOfReturn) {
        this.expectedDateOfReturn = expectedDateOfReturn;
        return this;
    }

    public LeaseBuilder withId(Long id) {
        this.id = id;
        return this;
    }
}
