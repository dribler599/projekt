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
public class MovieTableModel extends AbstractTableModel {

    private final MovieManager movieManager;
    private final LeaseManager leaseManager;
    private final ResourceBundle bundle;
    private List<Movie> movieList = new ArrayList<>();
    private ReadAllSwingWorker readWorker;
    final static org.slf4j.Logger log = LoggerFactory.getLogger(MovieTableModel.class);
    private JOptionPane dialog;


    public MovieTableModel(MovieManager movieManager, LeaseManager leaseManager) {
        this.movieManager = movieManager;
        this.leaseManager = leaseManager;
        bundle = ResourceBundle.getBundle("Bundle", Locale.getDefault());
        readWorker = new ReadAllSwingWorker(movieManager);
        readWorker.execute();
    }

    @Override
    public int getRowCount() {
        return movieList.size();
    }

    @Override
    public int getColumnCount() {
        return 6;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Movie movie = movieList.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return movie.getId();
            case 1:
                return movie.getName();
            case 2:
                return movie.getYear();
            case 3:
                return movie.getClassification();
            case 4:
                return movie.getDescription();
            case 5:
                return movie.getLocation();
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
                return bundle.getString("YEAROFRELEASE");
            case 3:
                return bundle.getString("CLASSIFICATION");
            case 4:
                return bundle.getString("DESCRIPTION");
            case 5:
                return bundle.getString("LOCATION");
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    private class ReadAllSwingWorker extends SwingWorker<List<Movie>,Void> {
        private final MovieManager movieManager;

        public ReadAllSwingWorker(MovieManager manager) {
            movieManager = manager;
        }

        @Override
        protected List<Movie> doInBackground() throws Exception {
            return movieManager.getAllMovies();
        }

        @Override
        protected void done() {
            try {
                movieList = get();
                fireTableDataChanged();
            } catch (InterruptedException | ExecutionException e) {
                log.error("Exception: ", e);
            }
        }
    }

    private class FilterSwingWorker extends SwingWorker<List<Movie>, Void> {

        private final MovieManager movieManager;
        private final LeaseManager leaseManager;
        private Object object;
        private final int filterType;

        public FilterSwingWorker(MovieManager movieManager, LeaseManager leaseManager, Object object, int filterType) {
            this.movieManager = movieManager;
            this.leaseManager = leaseManager;
            this.object = object;
            this.filterType = filterType;
        }

        @Override
        protected List<Movie> doInBackground() throws Exception {
            switch (filterType) {
                case 0:
                    return movieManager.getAllMovies();
                case 1:
                    return movieManager.getMovieByName((String) object);
                default:
                    return null;
            }
        }
        @Override
        protected void done() {
            try {
                movieList = get();
                fireTableDataChanged();
            } catch (InterruptedException | ExecutionException e) {
                log.error("Exception: ", e);
                return;
            }
            log.info("Filtering movies succeed");
        }
    }
    private class AddSwingWorker extends SwingWorker<Void, Void> {

        private final MovieManager movieManager;
        private final Movie movie;

        public AddSwingWorker(MovieManager movieManager, Movie movie) {
            this.movieManager = movieManager;
            this.movie = movie;
        }

        @Override
        protected Void doInBackground() throws MovieException {
            movieManager.createMovie(movie);
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                movieList.add(movie);
                int lastRow = movieList.size() - 1;
                fireTableRowsInserted(lastRow, lastRow);
            } catch (InterruptedException | ExecutionException e) {
                log.error("Adding movie failed: " + e.getCause().getMessage());
                JOptionPane.showMessageDialog(dialog, e.getCause().getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            log.info("Movie added successfully.");
        }
    }

    private class UpdateSwingWorker extends SwingWorker <Void, Void> {

        private final MovieManager movieManager;
        private final Movie movie;
        private final int row;

        public UpdateSwingWorker(MovieManager movieManager, Movie movie, int row) {
            this.movieManager = movieManager;
            this.movie = movie;
            this.row = row;
        }


        @Override
        protected Void doInBackground() throws Exception {
            movieManager.updateMovie(movie);
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                refreshTable();
            } catch (InterruptedException | ExecutionException e) {
                log.error("Updating movie failed: " + e.getCause().getMessage());
                JOptionPane.showMessageDialog(dialog, e.getCause().getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            log.info("Movie updated successfully.");
        }
    }

    private class DeleteSwingWorker extends SwingWorker <Void, Void> {

        private final MovieManager movieManager;
        private final int row;

        public DeleteSwingWorker(MovieManager movieManager, int rowIndex) {
            this.movieManager = movieManager;
            this.row = rowIndex;
        }

        @Override
        protected Void doInBackground() throws Exception {
            movieManager.deleteMovie(movieManager.getMovie((Long) getValueAt(row, 0)));
            return null;
        }

        @Override
        protected void done() {
            movieList.remove(row);
            fireTableRowsDeleted(row, row);
            log.info("Movie deleted successfully.");
        }
    }

    public List<Movie> getList() {
        return movieList;
    }

    public void setList(List<Movie> list) {
        movieList = list;
    }

    private AddSwingWorker addWorker;
    private UpdateSwingWorker updateWorker;
    private DeleteSwingWorker deleteWorker;
    private FilterSwingWorker filterWorker;



    public void addRow(Movie movie) {
        addWorker = new AddSwingWorker(movieManager, movie);
        addWorker.execute();
    }

    public void removeRow(int row) {
        deleteWorker = new DeleteSwingWorker(movieManager, row);
        deleteWorker.execute();
    }


    public void refreshTable() {
        readWorker = new ReadAllSwingWorker(movieManager);
        readWorker.execute();
    }

    public void filterTable(Object object, int filterType) {
        filterWorker = new FilterSwingWorker(movieManager, leaseManager, object, filterType);
        filterWorker.execute();
    }

    public void updateRow(Movie movie, int row) throws MovieException {
        updateWorker = new UpdateSwingWorker(movieManager, movie, row);
        updateWorker.execute();
    }
}
