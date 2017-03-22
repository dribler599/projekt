package cz.muni.fi;

import java.time.LocalDate;
import java.time.Month;

/**
 * Created by Tomas on 21. 3. 2017.
 */
public class CustomerBuilder {

    private Long id;
    private String name = "Honza";
    private LocalDate dateOfBirth = LocalDate.of(2000,Month.OCTOBER,20);
    private String address = "Honza Street 1";
    private String email = "honza@mail.com";
    private String phoneNumber = "666666666";

    public CustomerBuilder id (Long id) {
        this.id = id;
        return this;
    }

    public CustomerBuilder name (String name) {
        this.name = name;
        return this;
    }

    public CustomerBuilder address (String address) {
        this.address = address;
        return this;
    }

    public CustomerBuilder email (String email) {
        this.email = email;
        return this;
    }

    public CustomerBuilder phoneNumber (String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public CustomerBuilder dateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public CustomerBuilder dateOfBirth(int year, Month month, int day) {
        this.dateOfBirth = LocalDate.of(year, month, day);
        return this;
    }

    public Customer build() {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setName(name);
        customer.setDateOfBirth(dateOfBirth);
        customer.setAddress(address);
        customer.setEmail(email);
        customer.setPhoneNumber(phoneNumber);
        return customer;
    }
}
