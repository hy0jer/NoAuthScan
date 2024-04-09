package example.scanchecks.model;

import burp.api.montoya.http.message.HttpRequestResponse;

import javax.swing.table.AbstractTableModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class View_model extends AbstractTableModel {
    public static class ListTree {
        public final HttpRequestResponse responseReceived;
        public final String url;
        public final String status;
        public final String length;
        public final String time;


        public ListTree(HttpRequestResponse responseReceived) {
            this.responseReceived = responseReceived;
            this.url = responseReceived.request().url();
            this.status = String.valueOf(responseReceived.response().statusCode());
            this.length = String.valueOf(responseReceived.response().bodyToString().length());

            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss dd MMMM yyyy");
            Date currentDate = new Date();
            this.time = dateFormat.format(currentDate);
        }
    }

    private final List<ListTree> log;

    public View_model() {
        this.log = new ArrayList<>();
    }

    @Override
    public synchronized int getRowCount() {
        return log.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public String getColumnName(int column) {
        return switch (column) {
            case 0 -> "Host";
            case 1 -> "Status code";
            case 2 -> "Length";
            case 3 -> "Time";

            default -> "";
        };
    }

    @Override
    public synchronized Object getValueAt(int rowIndex, int columnIndex) {
        ListTree listTree = log.get(rowIndex);


        return switch (columnIndex) {
            case 0 -> listTree.url;
            case 1 -> listTree.status;
            case 2 -> listTree.length;
            case 3 -> listTree.time;
            default -> "";
        };
    }

    public synchronized void add(ListTree listTree) {
        int index = log.size();

        log.add(listTree);
        fireTableRowsInserted(index, index);
    }

    public synchronized ListTree get(int rowIndex) {
        return log.get(rowIndex);
    }
}
