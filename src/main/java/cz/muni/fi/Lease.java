package cz.muni.fi;

import java.time.LocalDate;

/**
 * Class representing lease
 */
public class Lease {
    private Long id;
    private Movie movie;
    private Customer customer;
    private Integer price;
    private LocalDate dateOfRent;
    private LocalDate dateOfReturn;
    private LocalDate expectedDateOfReturn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public LocalDate getDateOfRent() {
        return dateOfRent;
    }

    public void setDateOfRent(LocalDate dateOfRent) {
        this.dateOfRent = dateOfRent;
    }

    public LocalDate getDateOfReturn() {
        return dateOfReturn;
    }

    public void setDateOfReturn(LocalDate dateOfReturn) {
        this.dateOfReturn = dateOfReturn;
    }

    public LocalDate getExpectedDateOfReturn() {
        return expectedDateOfReturn;
    }

    public void setExpectedDateOfReturn(LocalDate expectedDateOfReturn) {
        this.expectedDateOfReturn = expectedDateOfReturn;
    }
}
