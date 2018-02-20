package cz.muni.fi.app;

import cz.muni.fi.jdbc.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Locale;
import java.util.ResourceBundle;


//podmínky, search by name

public class App extends JFrame {
    private final static Logger log = LoggerFactory.getLogger(App.class);
    private DataSource dataSource = Main.createMemoryDatabase();
    private final ResourceBundle bundle;
    private JOptionPane dialog;

    private final MovieManagerImpl movieManager = new MovieManagerImpl(dataSource);
    private final CustomerManagerImpl customerManager = new CustomerManagerImpl(dataSource);
    private final LeaseManagerImpl leaseManager = new LeaseManagerImpl(dataSource);

    private JPanel topPanel;
    private JTabbedPane mainPanel;
    private JPanel customerPanel;
    private JPanel moviePanel;
    private JPanel listOfCustomersPanel;
    private JPanel customerManagementPanel;
    private JLabel customerName;
    private JLabel customerDateOfBirth;
    private JTextField customerNameText;
    private JTextField customerDateOfBirthText;
    private JTextField customerAddressText;
    private JTextField customerEmailText;
    private JTextField customerPhoneNumberText;
    private JButton customerCreate;
    private JButton customerDelete;
    private JButton customerUpdate;
    private JPanel customerManagementButtons;
    private JLabel listOfCustomers;
    private JLabel customerAddress;
    private JLabel customerEmail;
    private JLabel customerPhoneNumber;
    private JScrollPane customersScrollPanel;
    private JTable customersTable;
    private JPanel movieManagementPanel;
    private JPanel listOfMoviesPanel;
    private JPanel moviesManagementButtons;
    private JButton movieCreate;
    private JButton movieUpdate;
    private JButton movieDelete;
    private JLabel listOfMovies;
    private JScrollPane moviesScrollPanel;
    private JTable moviesTable;
    private JLabel movieManagement;
    private JTextField movieNameText;
    private JTextField movieYearText;
    private JTextField movieClassificationText;
    private JTextField movieDescriptionText;
    private JTextField movieLocationText;
    private JLabel movieName;
    private JLabel movieYearOfRelease;
    private JLabel movieClassification;
    private JLabel movieDescription;
    private JLabel movieLocation;
    private JButton movieSearch;
    private JTextField movieSearchText;
    private JTextField customerSearchText;
    private JButton searchCustomer;
    private JLabel customerSearchLabel;
    private JLabel movieSearchLabel;
    private JButton viewAllCustomers;
    private JButton viewAllMovies;
    private JPanel leasePanel;
    private JPanel leaseManagementPanel;
    private JPanel listOfLeasesPanel;
    private JPanel leaseManagementButtons;
    private JLabel leaseManagement;
    private JLabel customer;
    private JTextField dateOfReturnText;
    private JTextField leaseCustomerSearchText;
    private JTextField leaseMovieSearchText;
    private JTable leasesTable;
    private JButton leaseCreate;
    private JButton leaseUpdate;
    private JButton leaseDelete;
    private JButton searchLease;
    private JButton viewAllLeases;
    private JLabel price;
    private JLabel dateOfRent;
    private JLabel expectedDateOfReturn;
    private JLabel dateOfReturn;
    private JLabel leaseCustomer;
    private JLabel leaseMovie;
    private JTextField expectedDateOfReturnText;
    private JTextField dateOfRentText;
    private JTextField priceText;
    private JTextField leaseCustomerText;
    private JTextField leaseMovieText;
    private JTabbedPane tabbedPane1;
    private JPanel leaseLeasePanel;
    private JPanel leaseCustomerPanel;
    private JPanel leaseMoviePanel;
    private JButton addCustomer;
    private JButton addMovie;
    private JButton leaseCustomerSearch;
    private JButton leaseCustomerViewAll;
    private JButton leaseMovieSearch;
    private JButton leaseMovieViewAll;
    private JTable leaseCustomerTable;
    private JTable leaseMovieTable;
    private JButton leaseBack;
    private JButton customerBack;
    private JButton movieBack;
    private JTextField leaseCustomerIdText;
    private JTextField leaseMovieIdText;

    public App() {


        bundle = ResourceBundle.getBundle("Bundle", Locale.getDefault());

        customersTable.setModel(new CustomerTableModel(customerManager, leaseManager));
        moviesTable.setModel(new MovieTableModel(movieManager, leaseManager));
        leasesTable.setModel(new LeaseTableModel(customerManager, movieManager, leaseManager));
        leaseCustomerTable.setModel(new CustomerTableModel(customerManager, leaseManager));
        leaseMovieTable.setModel(new MovieTableModel(movieManager, leaseManager));

        customerCreate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Customer customer = new Customer();
                if (customerNameText.getText().isEmpty() || customerDateOfBirthText.getText().isEmpty() ||
                        customerPhoneNumberText.getText().isEmpty() || customerAddressText.getText().isEmpty() ||
                        customerEmailText.getText().isEmpty()) {
                    log.error("All text fields should be filled");
                    JOptionPane.showMessageDialog(dialog, bundle.getString("ALLTEXTFIELDSHOULDBEFILLED"), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                customer.setName(customerNameText.getText());

                String dateString = customerDateOfBirthText.getText();
                if (!(isValidDate (dateString))){
                    log.error("Not valid date. Write date in format yyyy-mm-dd.");
                    JOptionPane.showMessageDialog(dialog, bundle.getString("ISNOTVALIDDATE"), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                LocalDate dateLocalDate = LocalDate.parse(dateString);
                customer.setDateOfBirth(dateLocalDate);

                String email = customerEmailText.getText();
                if (!(isValidEmailAddress (email))){
                    log.error("Not valid email.");
                    JOptionPane.showMessageDialog(dialog, bundle.getString("ISNOTVALIDEMAIL"), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                customer.setEmail(email);

                String phoneNumber = customerPhoneNumberText.getText();
                if (!(phoneNumber.matches("[0-9*#+() -]*"))){
                    log.error("Not valid phone number");
                    JOptionPane.showMessageDialog(dialog, bundle.getString("ISNOTVALIDPHONE"), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                customer.setPhoneNumber(phoneNumber);

                customer.setAddress(customerAddressText.getText());

                CustomerTableModel model = (CustomerTableModel) customersTable.getModel();
                model.addRow(customer);

                log.info("Customer created.");
                defaultCustomerSettings();
            }
        });

        customerUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.info("Updating customer...");
                CustomerTableModel model = (CustomerTableModel) customersTable.getModel();
                if (customerNameText.getText().isEmpty() || customerDateOfBirthText.getText().isEmpty() ||
                        customerPhoneNumberText.getText().isEmpty() || customerAddressText.getText().isEmpty() ||
                        customerEmailText.getText().isEmpty()) {
                    log.error("Cannot update customer! All textfields should be filled.");
                    JOptionPane.showMessageDialog(dialog, bundle.getString("ALLTEXTFIELDSHOULDBEFILLED"), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int selectedRow = customersTable.getSelectedRow();
                Customer customer = customerManager.getCustomer(Long.parseLong(model.getValueAt(selectedRow, 0).toString()));

                customer.setName(customerNameText.getText());

                String dateString = customerDateOfBirthText.getText();
                if (!(isValidDate (dateString))){
                    log.error("Not valid date. Write date in format yyyy-mm-dd.");
                    JOptionPane.showMessageDialog(dialog, bundle.getString("ISNOTVALIDDATE"), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                LocalDate dateLocalDate = LocalDate.parse(dateString);
                customer.setDateOfBirth(dateLocalDate);

                String email = customerEmailText.getText();
                if (!(isValidEmailAddress (email))){
                    log.error("Not valid email.");
                    JOptionPane.showMessageDialog(dialog, bundle.getString("ISNOTVALIDEMAIL"), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                customer.setEmail(email);

                String phoneNumber = customerPhoneNumberText.getText();
                if (!(phoneNumber.matches("[0-9*#+() -]*"))){
                    log.error("Not valid phone number");
                    JOptionPane.showMessageDialog(dialog, bundle.getString("ISNOTVALIDPHONE"), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                customer.setPhoneNumber(phoneNumber);

                customer.setAddress(customerAddressText.getText());

                try {
                    model.updateRow(customer, selectedRow);
                } catch (CustomerException e1) {
                    log.info("Cannot update customer...");
                    e1.printStackTrace();
                }

                log.info("Customer updated.");
                defaultCustomerSettings();
            }
        });

        customerDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.info("Deleting customer...");
                CustomerTableModel model = (CustomerTableModel) customersTable.getModel();
                int selectedRow = customersTable.getSelectedRow();

                Customer customer = customerManager.getCustomer(Long.parseLong(model.getValueAt(selectedRow, 0).toString()));

                model.removeRow(customersTable.getSelectedRow());

                log.info("Customer deleted.");
                defaultCustomerSettings();
            }
        });

        customerBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                defaultCustomerSettings();
            }
        });

        searchCustomer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CustomerTableModel model = (CustomerTableModel) customersTable.getModel();
                Object obj = customerSearchText.getText();

                model.filterTable(obj, 1);
                log.info("Filtering complete.");
            }
        });

        viewAllCustomers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CustomerTableModel model = (CustomerTableModel) customersTable.getModel();
                model.refreshTable();
                log.info("Refreshing table has been completed.");
            }
        });

        customersTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = customersTable.rowAtPoint(e.getPoint());
                CustomerTableModel model = (CustomerTableModel) customersTable.getModel();

                customerNameText.setText(model.getValueAt(selectedRow, 1).toString());
                customerDateOfBirthText.setText(model.getValueAt(selectedRow, 2).toString());
                customerAddressText.setText(model.getValueAt(selectedRow, 3).toString());
                customerEmailText.setText(model.getValueAt(selectedRow, 4).toString());
                customerPhoneNumberText.setText(model.getValueAt(selectedRow, 5).toString());

                customerUpdate.setEnabled(true);
                customerDelete.setEnabled(true);
                customerCreate.setEnabled(false);
                customerBack.setEnabled(true);

                super.mouseClicked(e);
            }
        });

        movieCreate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Movie movie = new Movie();
                if (movieNameText.getText().isEmpty() || movieYearText.getText().isEmpty() ||
                        movieClassificationText.getText().isEmpty() || movieLocationText.getText().isEmpty() ||
                        movieDescriptionText.getText().isEmpty()) {
                    log.error("All text fields should be filled");
                    JOptionPane.showMessageDialog(dialog, bundle.getString("ALLTEXTFIELDSHOULDBEFILLED"), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }


                movie.setName(movieNameText.getText());
                String yearString = movieYearText.getText();
                if (!(isParsable(yearString))){
                    log.error("Int overflow. Write int <= 2147483647");
                    JOptionPane.showMessageDialog(dialog, bundle.getString("INTOVERFLOW"), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int yearInt = Integer.parseInt(yearString);
                if (yearInt < 0) {
                    log.error("Year should be positive.");
                    JOptionPane.showMessageDialog(dialog, bundle.getString("INTSHOULDBEPOSITIVE"), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                movie.setYear(yearInt);
                movie.setDescription(movieDescriptionText.getText());
                movie.setClassification(movieClassificationText.getText());
                movie.setLocation(movieLocationText.getText());

                MovieTableModel model = (MovieTableModel) moviesTable.getModel();

                model.addRow(movie);

                log.info("Movie created.");
                defaultMovieSettings();
            }
        });

        movieUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.info("Updating movie...");
                MovieTableModel model = (MovieTableModel) moviesTable.getModel();
                if (movieNameText.getText().isEmpty() || movieYearText.getText().isEmpty() ||
                        movieClassificationText.getText().isEmpty() || movieLocationText.getText().isEmpty() ||
                        movieDescriptionText.getText().isEmpty()) {
                    log.error("Cannot update movie! All text fields should be filled.");
                    JOptionPane.showMessageDialog(dialog, bundle.getString("ALLTEXTFIELDSHOULDBEFILLED"), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int selectedRow = moviesTable.getSelectedRow();

                Movie movie = movieManager.getMovie(Long.parseLong(model.getValueAt(selectedRow, 0).toString()));
                movie.setName(movieNameText.getText());

                String yearString = movieYearText.getText();
                if (!(isParsable(yearString))){
                    log.error("Int overflow. Write int <= 2147483647");
                    JOptionPane.showMessageDialog(dialog, bundle.getString("INTOVERFLOW"), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int yearInt = Integer.parseInt(yearString);
                if (yearInt < 0) {
                    log.error("Year should be positive.");
                    JOptionPane.showMessageDialog(dialog, bundle.getString("INTSHOULDBEPOSITIVE"), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                movie.setYear(yearInt);

                movie.setClassification(movieClassificationText.getText());
                movie.setDescription(movieDescriptionText.getText());
                movie.setLocation(movieLocationText.getText());

                try {
                    model.updateRow(movie, selectedRow);
                } catch (MovieException e1) {
                    log.info("Cannot update movie...");
                    e1.printStackTrace();
                }

                log.info("Movie updated.");
                defaultMovieSettings();
            }
        });

        movieDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.info("Deleting movie...");
                MovieTableModel model = (MovieTableModel) moviesTable.getModel();
                int selectedRow = moviesTable.getSelectedRow();

                Movie movie = movieManager.getMovie(Long.parseLong(model.getValueAt(selectedRow, 0).toString()));

                model.removeRow(moviesTable.getSelectedRow());

                log.info("Movie deleted.");
                defaultMovieSettings();
            }
        });

        movieBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                defaultMovieSettings();
            }
        });

        movieSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MovieTableModel model = (MovieTableModel) moviesTable.getModel();
                Object obj = movieSearchText.getText();

                model.filterTable(obj, 1);
                log.info("Filtering table has been completed.");
            }
        });

        viewAllMovies.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MovieTableModel model = (MovieTableModel) moviesTable.getModel();
                model.refreshTable();
                log.info("Refreshing table has been completed.");
            }
        });

        moviesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = moviesTable.rowAtPoint(e.getPoint());
                MovieTableModel model = (MovieTableModel) moviesTable.getModel();

                movieNameText.setText(model.getValueAt(selectedRow, 1).toString());
                movieYearText.setText(model.getValueAt(selectedRow, 2).toString());
                movieClassificationText.setText(model.getValueAt(selectedRow, 3).toString());
                movieDescriptionText.setText(model.getValueAt(selectedRow, 4).toString());
                movieLocationText.setText(model.getValueAt(selectedRow, 5).toString());

                movieUpdate.setEnabled(true);
                movieDelete.setEnabled(true);
                movieCreate.setEnabled(false);
                movieBack.setEnabled(true);

                super.mouseClicked(e);
            }
        });

        leaseCreate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Lease lease = new Lease();
                if (leaseCustomerIdText.getText().isEmpty() || leaseMovieIdText.getText().isEmpty() ||
                        expectedDateOfReturnText.getText().isEmpty() || dateOfRentText.getText().isEmpty() ||
                        priceText.getText().isEmpty()) {
                    log.error("All text fields beside 'Date of return' should be filled.");
                    JOptionPane.showMessageDialog(dialog, bundle.getString("ALLTEXTFIELDSHOULDBEFILLED"), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                lease.setCustomer(customerManager.getCustomer(Long.parseLong(leaseCustomerIdText.getText())));
                lease.setMovie(movieManager.getMovie(Long.parseLong(leaseMovieIdText.getText())));

                String dateOfRentString = dateOfRentText.getText();
                if (!(isValidDate (dateOfRentString))){
                    log.error("Not valid rent date. Write date in format yyyy-mm-dd.");
                    JOptionPane.showMessageDialog(dialog, bundle.getString("ISNOTVALIDDATE"), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                LocalDate dateOfRentLocalDate = LocalDate.parse(dateOfRentString);
                lease.setDateOfRent(dateOfRentLocalDate);

                String expectedDateOfReturnString = expectedDateOfReturnText.getText();
                if (!(isValidDate (expectedDateOfReturnString))){
                    log.error("Not valid expected return date. Write date in format yyyy-mm-dd.");
                    JOptionPane.showMessageDialog(dialog, bundle.getString("ISNOTVALIDDATE"), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                LocalDate expectedDateOfReturnLocalDate = LocalDate.parse(expectedDateOfReturnString);
                lease.setExpectedDateOfReturn(expectedDateOfReturnLocalDate);

                if(!dateOfReturnText.getText().isEmpty()) {
                    String dateOfReturnString = dateOfRentText.getText();
                    if (!(isValidDate(dateOfReturnString))) {
                        log.error("Not valid return date. Write date in format yyyy-mm-dd.");
                        JOptionPane.showMessageDialog(dialog, bundle.getString("ISNOTVALIDDATE"), "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    LocalDate dateOfReturnLocalDate = LocalDate.parse(dateOfReturnString);
                    lease.setDateOfRent(dateOfReturnLocalDate);
                }

                String price = priceText.getText();
                if (!(isInteger(price) && Integer.parseInt(price) >= 0)) {
                    log.error("Not valid price.");
                    JOptionPane.showMessageDialog(dialog, bundle.getString("ISNOTVALIDPRICE"), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                lease.setPrice(Integer.parseInt(price));

                LeaseTableModel model = (LeaseTableModel) leasesTable.getModel();
                model.addRow(lease);

                log.info("Lease created.");
                defaultLeaseSettings();
            }
        });

        leaseUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.info("Updating lease...");
                LeaseTableModel model = (LeaseTableModel) leasesTable.getModel();
                if (leaseCustomerIdText.getText().isEmpty() || leaseMovieIdText.getText().isEmpty() ||
                        expectedDateOfReturnText.getText().isEmpty() || dateOfRentText.getText().isEmpty() ||
                        priceText.getText().isEmpty()) {
                    log.error("Cannot update lease! All text fields beside 'Date of return' should be filled.");
                    JOptionPane.showMessageDialog(dialog, bundle.getString("ALLTEXTFIELDSHOULDBEFILLED"), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int selectedRow = leasesTable.getSelectedRow();
                Lease lease = leaseManager.getLease(Long.parseLong(model.getValueAt(selectedRow, 0).toString()));

                lease.setMovie(movieManager.getMovie(Long.parseLong(leaseMovieIdText.getText())));
                lease.setCustomer(customerManager.getCustomer(Long.parseLong(leaseCustomerIdText.getText())));

                String dateOfRentString = dateOfRentText.getText();
                if (!(isValidDate (dateOfRentString))){
                    log.error("Not valid rent date. Write date in format yyyy-mm-dd.");
                    JOptionPane.showMessageDialog(dialog, bundle.getString("ISNOTVALIDDATE"), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                LocalDate dateOfRentLocalDate = LocalDate.parse(dateOfRentString);
                lease.setDateOfRent(dateOfRentLocalDate);

                String expectedDateOfReturnString = dateOfReturnText.getText();
                if (!(isValidDate (expectedDateOfReturnString))){
                    log.error("Not valid expected return date. Write date in format yyyy-mm-dd.");
                    JOptionPane.showMessageDialog(dialog, bundle.getString("ISNOTVALIDDATE"), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                LocalDate expectedDateOfReturnLocalDate = LocalDate.parse(expectedDateOfReturnString);
                lease.setDateOfRent(expectedDateOfReturnLocalDate);

                if(!dateOfReturnText.getText().isEmpty()) {
                    String dateOfReturnString = dateOfRentText.getText();
                    if (!(isValidDate(dateOfReturnString))) {
                        log.error("Not valid return date. Write date in format yyyy-mm-dd.");
                        JOptionPane.showMessageDialog(dialog, bundle.getString("ISNOTVALIDDATE"), "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    LocalDate dateOfReturnLocalDate = LocalDate.parse(dateOfReturnString);
                    lease.setDateOfRent(dateOfReturnLocalDate);
                }

                String price = priceText.getText();
                if (!(isInteger(price) && Integer.parseInt(price) >= 0)) {
                    log.error("Not valid price.");
                    JOptionPane.showMessageDialog(dialog, bundle.getString("ISNOTVALIDPRICE"), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                lease.setPrice(Integer.parseInt(price));

                try {
                    model.updateRow(lease, selectedRow);
                } catch (Exception e1) {
                    log.info("Cannot update lease...");
                    e1.printStackTrace();
                }

                log.info("Lease updated.");
                defaultLeaseSettings();
            }
        });

        leaseDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.info("Deleting lease...");
                LeaseTableModel model = (LeaseTableModel) leasesTable.getModel();
                int selectedRow = leasesTable.getSelectedRow();

                Lease lease = leaseManager.getLease(Long.parseLong(model.getValueAt(selectedRow, 0).toString()));

                model.removeRow(leasesTable.getSelectedRow());

                log.info("Lease deleted.");
                defaultLeaseSettings();
            }
        });

        leaseBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                defaultLeaseSettings();
            }
        });

        searchLease.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LeaseTableModel model = (LeaseTableModel) leasesTable.getModel();
                if(leaseCustomerSearchText.getText().isEmpty()) {
                    Object obj = leaseCustomerSearchText.getText();
                    model.filterTable(obj, 1);
                } else {
                    Object obj = leaseMovieSearchText.getText();
                    model.filterTable(obj, 2);
                }
                log.info("Filtering complete.");
            }
        });

        viewAllLeases.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LeaseTableModel model = (LeaseTableModel) leasesTable.getModel();
                model.refreshTable();
                log.info("Refreshing table has been completed.");
            }
        });

        leaseCustomerSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CustomerTableModel model = (CustomerTableModel) leaseCustomerTable.getModel();
                Object obj = customerSearchText.getText();

                model.filterTable(obj, 1);
                log.info("Filtering complete.");
            }
        });

        leaseCustomerViewAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CustomerTableModel model = (CustomerTableModel) leaseCustomerTable.getModel();
                model.refreshTable();
                log.info("Refreshing table has been completed.");
            }
        });

        leaseMovieSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MovieTableModel model = (MovieTableModel) leaseMovieTable.getModel();
                Object obj = movieSearchText.getText();

                model.filterTable(obj, 1);
                log.info("Filtering table has been completed.");
            }
        });

        leaseMovieViewAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MovieTableModel model = (MovieTableModel) leaseMovieTable.getModel();
                model.refreshTable();
                log.info("Refreshing table has been completed.");
            }
        });

        leasesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = leasesTable.rowAtPoint(e.getPoint());
                LeaseTableModel model = (LeaseTableModel) leasesTable.getModel();

                leaseMovieIdText.setText(model.getValueAt(selectedRow, 1).toString());
                leaseCustomerIdText.setText(model.getValueAt(selectedRow, 2).toString());
                leaseMovieText.setText(movieManager.getMovie(Long.parseLong(model.getValueAt(selectedRow, 1).toString())).getName());
                leaseCustomerText.setText(customerManager.getCustomer(Long.parseLong(model.getValueAt(selectedRow, 2).toString())).getName());
                priceText.setText(model.getValueAt(selectedRow, 3).toString());
                dateOfRentText.setText(model.getValueAt(selectedRow, 4).toString());
                expectedDateOfReturnText.setText(model.getValueAt(selectedRow, 5).toString());
                if(!model.getValueAt(selectedRow, 6).toString().isEmpty()) {
                    dateOfReturnText.setText(model.getValueAt(selectedRow, 6).toString());
                }

                leaseUpdate.setEnabled(true);
                leaseDelete.setEnabled(true);
                leaseCreate.setEnabled(false);
                leaseBack.setEnabled(true);

                super.mouseClicked(e);
            }
        });

        leaseCustomerTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = leaseCustomerTable.rowAtPoint(e.getPoint());
                CustomerTableModel model = (CustomerTableModel) leaseCustomerTable.getModel();

                leaseCustomerIdText.setText(model.getValueAt(selectedRow, 0).toString());
                leaseCustomerText.setText(model.getValueAt(selectedRow, 1).toString());
                addCustomer.setEnabled(true);
                super.mouseClicked(e);
            }
        });

        leaseMovieTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = leaseMovieTable.rowAtPoint(e.getPoint());
                MovieTableModel model = (MovieTableModel) moviesTable.getModel();

                leaseMovieIdText.setText(model.getValueAt(selectedRow, 0).toString());
                leaseMovieText.setText(model.getValueAt(selectedRow, 1).toString());
                addMovie.setEnabled(true);
                super.mouseClicked(e);
            }
        });
    }

    private void defaultCustomerSettings() {
        customerCreate.setEnabled(true);
        customerDelete.setEnabled(false);
        customerUpdate.setEnabled(false);
        customerBack.setEnabled(false);

        customerNameText.setText("");
        customerDateOfBirthText.setText("");
        customerAddressText.setText("");
        customerEmailText.setText("");
        customerPhoneNumberText.setText("");
    }

    private void defaultMovieSettings() {
        movieNameText.setText("");
        movieYearText.setText("");
        movieClassificationText.setText("");
        movieLocationText.setText("");
        movieDescriptionText.setText("");

        movieCreate.setEnabled(true);
        movieDelete.setEnabled(false);
        movieUpdate.setEnabled(false);
        movieBack.setEnabled(false);
    }

    private void defaultLeaseSettings() {
        leaseCreate.setEnabled(true);
        leaseDelete.setEnabled(false);
        leaseUpdate.setEnabled(false);
        leaseBack.setEnabled(false);
        addCustomer.setEnabled(false);
        addMovie.setEnabled(false);

        leaseMovieText.setText("");
        leaseCustomerText.setText("");
        priceText.setText("");
        dateOfRentText.setText("");
        expectedDateOfReturnText.setText("");
        dateOfReturnText.setText("");
        leaseCustomerIdText.setText("");
        leaseMovieIdText.setText("");
    }

    public static boolean isParsable(String input){
        boolean parsable = true;
        try{
            Integer.parseInt(input);
        }catch(NumberFormatException e){
            parsable = false;
        }
        return parsable;
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        return true;
    }

    public boolean isValidDate (String date) {
        final String DATE_FORMAT ="yyyy-MM-dd";

        if (date.length() != 10) {
            return false;
        }

        try {
            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
            df.setLenient(false);
            df.parse(date);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("The best app.");
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.setContentPane(new App().topPanel);
                frame.setPreferredSize(new Dimension(800,600));
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
