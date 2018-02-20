package cz.muni.fi.app;

import cz.muni.fi.jdbc.*;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

/**
 * Created by Lukas on 16.5.2017.
 */
public class LeaseTableModel extends AbstractTableModel {

    private final MovieManager movieManager;
    private final CustomerManager customerManager;
    private final LeaseManager leaseManager;
    private final ResourceBundle bundle;
    private List<Lease> leaseList = new ArrayList<>();
    private ReadAllSwingWorker readWorker;
    final static org.slf4j.Logger log = LoggerFactory.getLogger(LeaseTableModel.class);
    private JOptionPane dialog;

    public LeaseTableModel(CustomerManager customerManager, MovieManager movieManager, LeaseManager leaseManager) {
        this.customerManager = customerManager;
        this.movieManager = movieManager;
        this.leaseManager = leaseManager;
        ((LeaseManagerImpl)this.leaseManager).setMovieManager(movieManager);
        ((LeaseManagerImpl)this.leaseManager).setCustomerManager(customerManager);
        bundle = ResourceBundle.getBundle("Bundle", Locale.getDefault());
        readWorker = new LeaseTableModel.ReadAllSwingWorker(leaseManager);
        readWorker.execute();
    }

    @Override
    public int getRowCount() {
        return leaseList.size();
    }

    @Override
    public int getColumnCount() {
        return 7;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Lease lease = leaseList.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return lease.getId();
            case 1:
                return lease.getMovie().getId();
            case 2:
                return lease.getCustomer().getId();
            case 3:
                return lease.getPrice();
            case 4:
                return lease.getDateOfRent();
            case 5:
                return lease.getExpectedDateOfReturn();
            case 6:
                return lease.getDateOfReturn();
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
                return bundle.getString("MOVIE");
            case 2:
                return bundle.getString("CUSTOMER");
            case 3:
                return bundle.getString("PRICE");
            case 4:
                return bundle.getString("DATEOFRENT");
            case 5:
                return bundle.getString("EXPECTEDDATEOFRETURN");
            case 6:
                return bundle.getString("DATEOFRETURN");
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    private class ReadAllSwingWorker extends SwingWorker<List<Lease>,Void> {
        private final LeaseManager leaseManager;

        public ReadAllSwingWorker(LeaseManager manager) {
            leaseManager = manager;
        }

        @Override
        protected List<Lease> doInBackground() throws Exception {
            return leaseManager.getAllLeases();
        }

        @Override
        protected void done() {
            try {
                leaseList = get();
                fireTableDataChanged();
            } catch (InterruptedException | ExecutionException e) {
                log.error("Exception: ", e);
            }
        }
    }

    private class FilterSwingWorker extends SwingWorker<List<Lease>, Void> {

        private final CustomerManager customerManager;
        private final MovieManager movieManager;
        private final LeaseManager leaseManager;
        private Object object;
        private final int filterType;

        public FilterSwingWorker(CustomerManager customerManager, MovieManager movieManager, LeaseManager leaseManager, Object object, int filterType) {
            this.customerManager = customerManager;
            this.movieManager = movieManager;
            this.leaseManager = leaseManager;
            this.object = object;
            this.filterType = filterType;
        }

        @Override
        protected List<Lease> doInBackground() throws Exception {
            switch (filterType) {
                case 0:
                    return leaseManager.getAllLeases();
                case 1:
                    return leaseManager.findLeaseByCustomer((Customer) object);
                case 2:
                    return leaseManager.findLeaseByMovie((Movie) object);
                default:
                    return null;
            }
        }
        @Override
        protected void done() {
            try {
                leaseList = get();
                fireTableDataChanged();
            } catch (InterruptedException | ExecutionException e) {
                log.error("Exception: ", e);
                return;
            }
            log.info("Filtering leases succeed");
        }
    }
    private class AddSwingWorker extends SwingWorker<Void, Void> {

        private final LeaseManager leaseManager;
        private final Lease lease;

        public AddSwingWorker(LeaseManager leaseManager, Lease lease) {
            this.leaseManager = leaseManager;
            this.lease = lease;
        }

        @Override
        protected Void doInBackground() throws CustomerException {
            leaseManager.createLease(lease);
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                leaseList.add(lease);
                int lastRow = leaseList.size() - 1;
                fireTableRowsInserted(lastRow, lastRow);
            } catch (InterruptedException | ExecutionException e) {
                log.error("Adding lease failed: " + e.getCause().getMessage());
                JOptionPane.showMessageDialog(dialog, e.getCause().getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            log.info("Lease added successfully.");
        }
    }

    private class UpdateSwingWorker extends SwingWorker <Void, Void> {

        private final LeaseManager leaseManager;
        private final Lease lease;
        private final int row;

        public UpdateSwingWorker(LeaseManager leaseManager, Lease lease, int row) {
            this.leaseManager = leaseManager;
            this.lease = lease;
            this.row = row;
        }


        @Override
        protected Void doInBackground() throws Exception {
            leaseManager.updateLease(lease);
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                refreshTable();
            } catch (InterruptedException | ExecutionException e) {
                log.error("Updating lease failed: " + e.getCause().getMessage());
                JOptionPane.showMessageDialog(dialog, e.getCause().getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            log.info("Lease updated successfully.");
        }
    }

    private class DeleteSwingWorker extends SwingWorker <Void, Void> {

        private final LeaseManager leaseManager;
        private final int row;

        public DeleteSwingWorker(LeaseManager leaseManager, int rowIndex) {
            this.leaseManager = leaseManager;
            this.row = rowIndex;
        }

        @Override
        protected Void doInBackground() throws Exception {
            leaseManager.deleteLease(leaseManager.getLease((Long) getValueAt(row, 0)));
            return null;
        }

        @Override
        protected void done() {
            leaseList.remove(row);
            fireTableRowsDeleted(row, row);
            log.info("Lease deleted successfully.");
        }
    }

    public List<Lease> getList() {
        return leaseList;
    }

    public void setList(List<Lease> list) {
        leaseList = list;
    }

    private AddSwingWorker addWorker;
    private UpdateSwingWorker updateWorker;
    private DeleteSwingWorker deleteWorker;
    private FilterSwingWorker filterWorker;



    public void addRow(Lease lease) {
        addWorker = new AddSwingWorker(leaseManager, lease);
        addWorker.execute();
    }

    public void removeRow(int row) {
        deleteWorker = new DeleteSwingWorker(leaseManager, row);
        deleteWorker.execute();
    }


    public void refreshTable() {
        readWorker = new ReadAllSwingWorker(leaseManager);
        readWorker.execute();
    }

    public void filterTable(Object object, int filterType) {
        filterWorker = new FilterSwingWorker(customerManager, movieManager, leaseManager, object, filterType);
        filterWorker.execute();
    }

    public void updateRow(Lease lease, int row) throws IllegalArgumentException {
        updateWorker = new UpdateSwingWorker(leaseManager, lease, row);
        updateWorker.execute();
    }
}
