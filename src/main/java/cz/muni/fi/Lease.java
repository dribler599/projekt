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
    private LocalDate expectedDateOfReturn;
    private LocalDate dateOfReturn;

    public Lease() {
    }

    public Lease(Long id, Movie movie, Customer customer, Integer price, LocalDate dateOfRent,
                 LocalDate expectedDateOfReturn, LocalDate dateOfReturn) {
        this.id = id;
        this.movie = movie;
        this.customer = customer;
        this.price = price;
        this.dateOfRent = dateOfRent;
        this.expectedDateOfReturn = expectedDateOfReturn;
        this.dateOfReturn = dateOfReturn;
    }

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

    @Override
    public String toString() {
        return "Lease{" +
                "id=" + id +
                ", movie=" + movie.getName() +
                ", customer=" + customer.getName() +
                ", price=" + price +
                ", dateofrent=" + dateOfRent +
                ", expecteddateofreturn=" + expectedDateOfReturn +
                ", date of return=" + dateOfReturn +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Lease lease = (Lease) o;

        return id != null && id.equals(lease.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
