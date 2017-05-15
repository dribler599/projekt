package cz.muni.fi.web;

import cz.muni.fi.jdbc.Customer;
import cz.muni.fi.jdbc.CustomerException;
import cz.muni.fi.jdbc.CustomerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Created by Tomas on 10. 4. 2017.
 */
@WebServlet(CustomerServlet.URL_MAPPING  + "/*")
public class CustomerServlet extends HttpServlet{

    private static final String LIST_JSP = "/list.jsp";
    public static final String URL_MAPPING = "/customers";

    private final static Logger log = LoggerFactory.getLogger(CustomerServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("GET ...");
        showCustomersList(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        String action = request.getPathInfo();
        log.debug("POST ... {}",action);

        switch (action) {
            case "/add":
                String name = request.getParameter("NAME");
                String dateOfBirth = request.getParameter("DATEOFBIRTH");
                String address = request.getParameter("ADDRESS");
                String email = request.getParameter("EMAIL");
                String phoneNumber = request.getParameter("PHONENUMBER");

                if (name == null || name.length() == 0 || address == null || address.length() == 0  ||
                        email == null || email.length() == 0 || phoneNumber == null || phoneNumber.length() == 0 ||
                        phoneNumber == null|| phoneNumber.length() == 0) {
                    request.setAttribute("chyba", "Je nutné vyplnit všechny hodnoty !");
                    showCustomersList(request, response);
                    return;
                }

                if (!(isValidDate (dateOfBirth))){
                    request.setAttribute("chyba", "Zadejte validní datum ve formatu yyyy-MM-dd.");
                    showCustomersList(request, response);
                    return;
                }

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate date = LocalDate.parse(dateOfBirth, formatter);

                if (!(isValidEmailAddress (email))){
                    request.setAttribute("chyba", "Zadejte validní emailovou adresu.");
                    showCustomersList(request, response);
                    return;
                }

                if (!(phoneNumber.matches("[0-9*#+() -]*"))){
                request.setAttribute("chyba", "Zadejte validní telefonní číslo obsahující znaky *#+() - a číslice.");
                showCustomersList(request, response);
                return;
                }

                try {
                    Customer customer = new Customer(null, name, date, address, email, phoneNumber);
                    getCustomerManager().createCustomer(customer);
                    log.debug("created {}",customer);

                    response.sendRedirect(request.getContextPath()+URL_MAPPING);
                    return;
                } catch (CustomerException e) {
                    log.error("Cannot add customer", e);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                    return;
                }

            case "/delete":
                try {
                    Long id = Long.valueOf(request.getParameter("ID"));
                    getCustomerManager().deleteCustomerID(id);
                    log.debug("deleted customer {}",id);
                    response.sendRedirect(request.getContextPath()+URL_MAPPING);
                    return;
                } catch (CustomerException e) {
                    log.error("Cannot delete customer", e);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                    return;
                }
        }
    }

    /**
     * Gets CustomerManager from ServletContext, where it was stored by {@link StartListener}.
     *
     * @return CustomerManager instance
     */
    private CustomerManager getCustomerManager() {
        return (CustomerManager) getServletContext().getAttribute("customerManager");
    }

    /**
     * Stores the list of customers to request attribute "customers" and forwards to the JSP to display it.
     */
    private void showCustomersList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            log.debug("showing table of customers");
            request.setAttribute("CUSTOMER", getCustomerManager().getAllCustomers());
            request.getRequestDispatcher(LIST_JSP).forward(request, response);
        } catch (CustomerException e) {
            log.error("Cannot show customers", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public boolean isValidDate (String date) {
        final String DATE_FORMAT ="yyyy-MM-dd";

        try {
            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
            df.setLenient(false);
            df.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }
}
