package cz.muni.fi.app;

import cz.muni.fi.jdbc.*;

import javax.swing.*;
import org.slf4j.LoggerFactory;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

/**
 * Created by Tomas on 13. 5. 2017.
 */
public class CustomerTableModel extends AbstractTableModel {

    private final CustomerManager customerManager;
    private final LeaseManager leaseManager;
    private final ResourceBundle bundle;
    private List<Customer> customerList = new ArrayList<>();
    private ReadAllSwingWorker readWorker;
    final static org.slf4j.Logger log = LoggerFactory.getLogger(CustomerTableModel.class);
    private JOptionPane dialog;


    public CustomerTableModel(CustomerManager customerManager, LeaseManager leaseManager) {
        this.customerManager = customerManager;
        this.leaseManager = leaseManager;
        bundle = ResourceBundle.getBundle("Bundle", Locale.getDefault());
        readWorker = new ReadAllSwingWorker(customerManager);
        readWorker.execute();
    }

    @Override
    public int getRowCount() {
        return customerList.size();
    }

    @Override
    public int getColumnCount() {
        return 6;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Customer customer = customerList.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return customer.getId();
            case 1:
                return customer.getName();
            case 2:
                return customer.getDateOfBirth();
            case 3:
                return customer.getAddress();
            case 4:
                return customer.getEmail();
            case 5:
                return customer.getPhoneNumber();
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public String getColumnName(int column) {

        switch (column) {
            case 0:
                return bundle.getString("ID");
            case 1:
                return bundle.getString("NAME");
            case 2:
                return bundle.getString("DATEOFBIRTH");
            case 3:
                return bundle.getString("ADDRESS");
            case 4:
                return bundle.getString("EMAIL");
            case 5:
                return bundle.getString("PHONENUMBER");
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    private class ReadAllSwingWorker extends SwingWorker<List<Customer>,Void> {
        private final CustomerManager customerManager;

        public ReadAllSwingWorker(CustomerManager manager) {
            customerManager = manager;
        }

        @Override
        protected List<Customer> doInBackground() throws Exception {
            return customerManager.getAllCustomers();
        }

        @Override
        protected void done() {
            try {
                customerList = get();
                fireTableDataChanged();
            } catch (InterruptedException | ExecutionException e) {
                log.error("Exception: ", e);
            }
        }
    }

    private class FilterSwingWorker extends SwingWorker<List<Customer>, Void> {

        private final CustomerManager customerManager;
        private final LeaseManager leaseManager;
        private Object object;
        private final int filterType;

        public FilterSwingWorker(CustomerManager customerManager, LeaseManager leaseManager, Object object, int filterType) {
            this.customerManager = customerManager;
            this.leaseManager = leaseManager;
            this.object = object;
            this.filterType = filterType;
        }

        @Override
        protected List<Customer> doInBackground() throws Exception {
            switch (filterType) {
                case 0:
                    return customerManager.getAllCustomers();
                case 1:
                    return customerManager.getCustomerByName((String) object);
                default:
                    return null;
            }
        }
        @Override
        protected void done() {
            try {
                customerList = get();
                fireTableDataChanged();
            } catch (InterruptedException | ExecutionException e) {
                log.error("Exception: ", e);
                return;
            }
            log.info("Filtering customers succeed");
        }
    }
    private class AddSwingWorker extends SwingWorker<Void, Void> {

        private final CustomerManager customerManager;
        private final Customer customer;

        public AddSwingWorker(CustomerManager customerManager, Customer customer) {
            this.customerManager = customerManager;
            this.customer = customer;
        }

        @Override
        protected Void doInBackground() throws CustomerException {
            customerManager.createCustomer(customer);
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                customerList.add(customer);
                int lastRow = customerList.size() - 1;
                fireTableRowsInserted(lastRow, lastRow);
            } catch (InterruptedException | ExecutionException e) {
                log.error("Adding customer failed: " + e.getCause().getMessage());
                JOptionPane.showMessageDialog(dialog, e.getCause().getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            log.info("Customer added successfully.");
        }
    }

    private class UpdateSwingWorker extends SwingWorker <Void, Void> {

        private final CustomerManager customerManager;
        private final Customer customer;
        private final int row;

        public UpdateSwingWorker(CustomerManager customerManager, Customer customer, int row) {
            this.customerManager = customerManager;
            this.customer = customer;
            this.row = row;
        }


        @Override
        protected Void doInBackground() throws Exception {
            customerManager.updateCustomer(customer);
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                refreshTable();
            } catch (InterruptedException | ExecutionException e) {
                log.error("Updating customer failed: " + e.getCause().getMessage());
                JOptionPane.showMessageDialog(dialog, e.getCause().getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            log.info("Customer updated successfully.");
        }
    }

    private class DeleteSwingWorker extends SwingWorker <Void, Void> {

        private final CustomerManager customerManager;
        private final int row;

        public DeleteSwingWorker(CustomerManager customerManager, int rowIndex) {
            this.customerManager = customerManager;
            this.row = rowIndex;
        }

        @Override
        protected Void doInBackground() throws Exception {
            customerManager.deleteCustomer(customerManager.getCustomer((Long) getValueAt(row, 0)));
            return null;
        }

        @Override
        protected void done() {
            customerList.remove(row);
            fireTableRowsDeleted(row, row);
            log.info("Customer deleted successfully.");
        }
    }

    public List<Customer> getList() {
        return customerList;
    }

    public void setList(List<Customer> list) {
        customerList = list;
    }

    private AddSwingWorker addWorker;
    private UpdateSwingWorker updateWorker;
    private DeleteSwingWorker deleteWorker;
    private FilterSwingWorker filterWorker;



    public void addRow(Customer customer) {
        addWorker = new AddSwingWorker(customerManager, customer);
        addWorker.execute();
    }

    public void removeRow(int row) {
        deleteWorker = new DeleteSwingWorker(customerManager, row);
        deleteWorker.execute();
    }


    public void refreshTable() {
        readWorker = new ReadAllSwingWorker(customerManager);
        readWorker.execute();
    }

    public void filterTable(Object object, int filterType) {
        filterWorker = new FilterSwingWorker(customerManager, leaseManager, object, filterType);
        filterWorker.execute();
    }

    public void updateRow(Customer customer, int row) throws CustomerException {
        updateWorker = new UpdateSwingWorker(customerManager, customer, row);
        updateWorker.execute();
    }
}
