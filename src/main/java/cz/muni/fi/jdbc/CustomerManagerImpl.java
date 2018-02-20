package cz.muni.fi.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;
import java.time.LocalDate;

/**
 * Implementation of CustomerManager.
 */
public class CustomerManagerImpl implements CustomerManager {

    final static Logger log = LoggerFactory.getLogger(CustomerManagerImpl.class);
    private final DataSource dataSource;

    public CustomerManagerImpl (DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void createCustomer(Customer customer){

        if (customer == null) {
            throw new IllegalArgumentException("customer s null");
        }

        String sql = "INSERT INTO CUSTOMER (NAME, DATEOFBIRTH, ADDRESS, EMAIL, PHONENUMBER) VALUES (?,?,?,?,?)";
        Connection conn = null;

        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS) ;
            ps.setString(1, customer.getName());
            ps.setDate(2, Date.valueOf(customer.getDateOfBirth()));
            ps.setString(3, customer.getAddress());
            ps.setString(4,customer.getEmail());
            ps.setString(5,customer.getPhoneNumber());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            System.out.println(keys);
            if (keys.next()) {
                customer.setId(keys.getLong(1));
            }
            log.debug("created book {}",customer);
            ps.close();

        } catch (SQLException e) {
            log.error("cannot insert customer", e);
            throw new RuntimeException(e);

        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {}
            }
        }
    }

    @Override
    public Customer getCustomer(Long id) {

        if ((id < 0) || (id == null)){
            throw new IllegalArgumentException("wrong id");
        }

        String sql = "SELECT * FROM CUSTOMER WHERE ID = ?";
        Connection conn = null;

        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, id);
            Customer customer = null;
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                customer = new Customer(
                        rs.getLong("ID"),
                        rs.getString("NAME"),
                        rs.getDate("DATEOFBIRTH").toLocalDate(),
                        rs.getString("ADDRESS"),
                        rs.getString("EMAIL"),
                        rs.getString("PHONENUMBER")
                );
            }
            rs.close();
            ps.close();
            return customer;
        } catch (SQLException e) {
            log.error("cannot get customer", e);
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {}
            }
        }
    }

    @Override
    public void updateCustomer(Customer customer) {

        if (customer == null) {
            throw new IllegalArgumentException("customer s null");
        }

        String sql = "UPDATE CUSTOMER SET NAME = ?, DATEOFBIRTH = ?," +
                " ADDRESS = ?, EMAIL =?, PHONENUMBER = ? WHERE ID = ?";
        Connection conn = null;

        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, customer.getName());
            ps.setDate(2, Date.valueOf(customer.getDateOfBirth()));
            ps.setString(3, customer.getAddress());
            ps.setString(4,customer.getEmail());
            ps.setString(5,customer.getPhoneNumber());
            ps.setLong(6, customer.getId());
            int n = ps.executeUpdate();
            if (n == 0) {
                throw new RuntimeException("not updated customer with id " + customer.getId(), null);
            }
            if (n > 1) {
                throw new RuntimeException("more than 1 customer with id " + customer.getId(), null);
            }
            log.debug("updated customer {}", customer);

        } catch (SQLException e) {
            log.error("cannot update customer", e);
            throw new RuntimeException(e);

        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {}
            }
        }
    }

    @Override
    public void deleteCustomer (Customer customer) {

        if (customer == null) {
            throw new IllegalArgumentException("customer s null");
        }else {
            Long id = customer.getId();
            deleteCustomerID(id);
        }
    }

    public void deleteCustomerID (Long id) {

        String sql = "DElETE FROM CUSTOMER WHERE ID = ?";
        Connection conn = null;

        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, id);
            int n = ps.executeUpdate();
            if (n == 0) {
                throw new RuntimeException("not deleted customer with id " + id, null);
            }
            log.debug("deleted customer {}", id);

        } catch (SQLException e) {
            log.error("cannot delete customer", e);
            throw new RuntimeException(e);

        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {}
            }
        }
    }

    @Override
    public List<Customer> getAllCustomers() {
        String sql = "SELECT * FROM CUSTOMER";
        Connection conn = null;

        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            List<Customer> customers = new ArrayList<>();
            while (rs.next()) {
                Long id = rs.getLong("ID");
                String name = rs.getString("NAME");
                LocalDate dateOfBirth = rs.getDate("DATEOFBIRTH").toLocalDate();
                String address = rs.getString("ADDRESS");
                String email = rs.getString("EMAIL");
                String phoneNumber = rs.getString("PHONENUMBER");

                customers.add(new Customer(id, name, dateOfBirth, address, email, phoneNumber));
            }
            log.debug("getting all {} customers",customers.size());
            return customers;

        } catch (SQLException e) {
            log.error("cannot select customers", e);
            throw new RuntimeException(e);

        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {}
            }
        }
    }

    @Override
    public List<Customer> getCustomerByName(String n) {

        if (n == null){
            throw new IllegalArgumentException("name is null");
        }

        String sql = "SELECT * FROM CUSTOMER WHERE NAME = ?";
        Connection conn = null;

        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, n);
            ResultSet rs = ps.executeQuery();
            List<Customer> customers = new ArrayList<>();
            while (rs.next()) {
                Long id = rs.getLong("ID");
                String email = rs.getString("EMAIL");
                String phoneNumber = rs.getString("PHONENUMBER");
                String name = rs.getString("NAME");
                LocalDate dateOfBirth = rs.getDate("DATEOFBIRTH").toLocalDate();
                String address = rs.getString("ADDRESS");

                customers.add(new Customer(id, name, dateOfBirth, address, email, phoneNumber));
            }
            log.debug("getting all {} customers",customers.size());
            return customers;

        } catch (SQLException e) {
            log.error("cannot select customers", e);
            throw new RuntimeException(e);

        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {}
            }
        }
    }
}
