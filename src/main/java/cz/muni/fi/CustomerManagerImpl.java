package cz.muni.fi;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of CustomerManager.
 */
public class CustomerManagerImpl implements CustomerManager {

    private JdbcTemplate jdbc;
    private TransactionTemplate transaction;

    public CustomerManagerImpl(DataSource dataSource) {
        this.jdbc = new JdbcTemplate(dataSource);
        this.transaction = new TransactionTemplate(new DataSourceTransactionManager(dataSource));
    }

    @Override
    public void createCustomer(Customer c) {
        SimpleJdbcInsert insertCustomer = new SimpleJdbcInsert(jdbc).withTableName("customer").usingGeneratedKeyColumns("id");
        Map<String, Object> parameters = new HashMap<>(2);
        parameters.put("name", c.getName());
        parameters.put("dateOfBirth", c.getDateOfBirth());
        parameters.put("address", c.getAddress());
        parameters.put("email", c.getEmail());
        parameters.put("phoneNumber", c.getPhoneNumber());
        Number id = insertCustomer.executeAndReturnKey(parameters);
        c.setId(id.longValue());
    }

    private RowMapper<Customer> customerMapper = new RowMapper<Customer>() {
        @Override
        public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Customer(rs.getLong("id"), rs.getString("name"), rs.getDate("dateOfBirth").toLocalDate(),
                    rs.getString("address"), rs.getString("email"), rs.getString("phoneNumber"));
        }
    };

    @Override
    public Customer getCustomer(Long id) {
        return jdbc.queryForObject("SELECT * FROM customer WHERE id=?", customerMapper, id);
    }

    @Override
    public void updateCustomer(Customer c) {
        jdbc.update("UPDATE customer set name=?, dateOfBirth=?,address=?,email=?,phoneNumber=? where id=?",
                c.getName(), c.getDateOfBirth(), c.getAddress(), c.getEmail(), c.getPhoneNumber(), c.getId());
    }

    @Override
    public void deleteCustomer(Customer customer) {
        long id = customer.getId();
        jdbc.update("DELETE FROM customer WHERE id=?", id);
    }

    @Override
    public List<Customer> getAllCustomers() {
        return transaction.execute(new TransactionCallback<List<Customer>>() {
            @Override
            public List<Customer> doInTransaction(TransactionStatus status) {
                return jdbc.query("SELECT * FROM customer", customerMapper);
            }
        });
    }

    @Override
    public List<Customer> getCustomerByName(String name) {
        return transaction.execute(new TransactionCallback<List<Customer>>() {
            @Override
            public List<Customer> doInTransaction(TransactionStatus status) {
                return jdbc.query("SELECT * FROM customer where name=?", customerMapper, name);
            }
        });
    }
}
