package FXMLControllers;

import ConnectionManager.*;
import DataOperator.QueryBuilder;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class MainFrameController implements Initializable {

    @FXML
    private TextField fromDate;
    @FXML
    private TextField tillDate;
    @FXML
    private TextField getDocByID;


    //Stores date selection interval
    public static LinkedList<String> listOfPeriod = new LinkedList<String>();
    // Stores selected Documents ID for XML import.
    public static ArrayList<Integer> listOfDocIDByTypes = new ArrayList<Integer>();
    // Stores Documents ID if selected
    public static ArrayList<Integer> listOfDocsSelectedByID = new ArrayList<Integer>();

    //Temporary values for data transfer
    private ArrayList<String> listOfchoosedDocuments = new ArrayList<String>();
    private SimpleDateFormat currentFormat = new SimpleDateFormat("dd/MM/yyyy");
    private String choosedDocName;
    private String unChoosedDocName;
    private int docCode;

    Connection connection = null;
    //Java FX collection that holds all document names in alphabet order
    ObservableList<String> docNames = FXCollections.observableArrayList();
    //Java FX collection that holds all document names and documents codes with no order
    static ObservableMap<Integer, String> mapOfData = FXCollections.observableHashMap();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fromDate.setPromptText("дд/мм/гггг");
        tillDate.setPromptText("дд/мм/гггг");
        getDocByID.setPromptText("Напр: 11012,11013...");
    }

    /**
     * 1. Selecting and holding in 'docNames' (Temporary storage) - all types of documents from GMS Database and
     * sorting them by alphabet order.
     * 2. Load them into list ("Выбирите тип документа:") and awaiting for user choice.
     * 3. Load user choice to 'listOfchoosedDocuments'.
     */
    public void getDocTypeList() {
        try {
            connection = DriverManager.getConnection(ConnectionManager.getConnParams);
            mapOfData = QueryBuilder.getDocsCatalog(connection);

            for (String vales : mapOfData.values()) {
                docNames.add(vales);
            }
            Collections.sort(docNames, String.CASE_INSENSITIVE_ORDER);


        } catch (SQLException e) {
            e.printStackTrace();
        }
        listOfchoosedDocuments.clear();
        Stage stage = new Stage();
        Group root = new Group();
        Scene scene = new Scene(root);
        ArrayList<Document> list = new ArrayList<Document>();
        for (int i = 0; i < docNames.size(); i++) {
            list.add(new Document(false, docNames.get(i)));
        }
        final ObservableList<Document> data = FXCollections.observableArrayList(list);

        TableColumn selectedCol = new TableColumn<Document, Boolean>();
        selectedCol.setText("Выбрать");
        selectedCol.setMinWidth(50);
        selectedCol.setCellValueFactory(new PropertyValueFactory("selected"));
        selectedCol.setCellFactory(new Callback<TableColumn<Document, Boolean>, TableCell<Document, Boolean>>() {

            public TableCell<Document, Boolean> call(TableColumn<Document, Boolean> p) {
                return new CheckBoxTableCell<Document, Boolean>();
            }
        });
        //"DocName" column
        TableColumn docNameCol = new TableColumn();
        docNameCol.setText("Тип документа");
        docNameCol.setCellValueFactory(new PropertyValueFactory("docName"));

        TableView tableView = new TableView();
        tableView.setItems(data);
        tableView.getColumns().addAll(selectedCol, docNameCol);
        root.getChildren().add(tableView);
        stage.setScene(scene);
        stage.show();
        docNames.clear();
    }
   //Converts choosed Document names to Documents ID for Query builder.
    public void docNameConvertToID () {
        for (Map.Entry<Integer, String> entry : mapOfData.entrySet()) {
            for (String value : listOfchoosedDocuments) {
                String searchingDocName = entry.getValue();
                int searchingDocCode = entry.getKey();

                if (searchingDocName.equals(value)) {
                    docCode = searchingDocCode;
                    listOfDocIDByTypes.add(docCode);
                }
            }
        }
    }

    //Date validator
    public boolean isThisDateValid(String dateToValidate, String dateFromat) {

        if (dateToValidate == null) {
            return false;
        }

        try {
            //if not valid, it will throw ParseException
            Date date = currentFormat.parse(dateToValidate);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //Checks that user fill 'fromDate' and 'tillDate' fields correctly.
    public boolean checkThatDatesSelected() {
        String checkFromDate = this.fromDate.getText();
        String checkTillDate = this.tillDate.getText();
        Date frDate = null;
        Date tiDate = null;
        try {
            tiDate = currentFormat.parse(checkTillDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            frDate = currentFormat.parse(checkFromDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (((this.fromDate.getText() != "" || this.fromDate.getText() != null) && isThisDateValid(checkFromDate, "dd/MM/yyyy")) &&
                (this.tillDate.getText() != "" || this.tillDate.getText() != null) && isThisDateValid(checkFromDate, "dd/MM/yyyy") &&
                (frDate.compareTo(tiDate) < 1)) {
            return true;
        } else {
            new ErrorMsg("Внимание! Не корректные даты.\n Введите даты в формате \n День/Месяц/Год (дд/мм/гггг)");
            return false;
        }
    }

    //Checks if field 'getDocByID' is empty or incorrect (transl. -  'Поиск по номеру документа' )
    private boolean checkGetDocIDFiled() {
        if (getDocByID.getText() != "" && getDocByID.getText() != null && getDocByID.getText().length() != 0)
            //&& getDocByID.getText().matches("[0-9,]"))
            return true;
        else
            getDocByID.setText("");
        return false;
    }

    //Selects current date
    public void daySelected() {
        listOfPeriod.clear();
        try {
            Calendar calStart = Calendar.getInstance();
            Calendar calEnd = Calendar.getInstance();
            String startDate = currentFormat.format(calStart.getTime());
            String endDate = currentFormat.format(calEnd.getTime());
            fromDate.setText(startDate);
            tillDate.setText(endDate);
        } catch (Exception e) {
            e.printStackTrace();
            new ErrorMsg("Проверьте формат даты.");
        }
    }

    //Selects current week
    public void weekSelected() {
        listOfPeriod.clear();
        String selectedDate = fromDate.getText();
        tillDate.setText(selectedDate);
        try {
            Calendar calStart = Calendar.getInstance();
            Calendar calEnd = Calendar.getInstance();
            calStart.set(Calendar.DAY_OF_WEEK, Calendar.getInstance().getFirstDayOfWeek());
            calEnd.set(Calendar.DAY_OF_WEEK, Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_WEEK - 1));
            String startDate = currentFormat.format(calStart.getTime());
            String endDate = currentFormat.format(calEnd.getTime());
            fromDate.setText(startDate);
            tillDate.setText(endDate);
        } catch (Exception e) {
            e.printStackTrace();
            new ErrorMsg("Проверьте формат даты.");
        }
    }

    //Selects current month
    public void monthSelected() {
        listOfPeriod.clear();
        String selectedDate = fromDate.getText();
        tillDate.setText(selectedDate);
        try {
            Calendar calStart = Calendar.getInstance();
            Calendar calEnd = Calendar.getInstance();
            calStart.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().getActualMinimum(Calendar.DAY_OF_MONTH));
            calEnd.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().getMaximum(Calendar.DAY_OF_MONTH));
            String startDate = currentFormat.format(calStart.getTime());
            String endDate = currentFormat.format(calEnd.getTime());
            fromDate.setText(startDate);
            tillDate.setText(endDate);
        } catch (Exception e) {
            e.printStackTrace();
            new ErrorMsg("Проверьте формат даты.");
        }
    }

    //Select current year
    public void yearSelected() {
        String selectedDate = fromDate.getText();
        tillDate.setText(selectedDate);
        try {
            Calendar calStart = Calendar.getInstance();
            Calendar calEnd = Calendar.getInstance();
            calStart.set(Calendar.DAY_OF_YEAR, Calendar.getInstance().getActualMinimum(Calendar.DAY_OF_YEAR));
            calEnd.set(Calendar.DAY_OF_YEAR, Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_YEAR));
            String startDate = currentFormat.format(calStart.getTime());
            String endDate = currentFormat.format(calEnd.getTime());
            fromDate.setText(startDate);
            tillDate.setText(endDate);
        } catch (Exception e) {
            e.printStackTrace();
            new ErrorMsg("Проверьте формат даты.");
        }

    }


    //Document object model
    public class Document {
        private BooleanProperty selected;
        private StringProperty docName;

        private Document(boolean selected, String docName) {
            this.selected = new SimpleBooleanProperty(selected);
            this.docName = new SimpleStringProperty(docName);
            this.selected = new SimpleBooleanProperty(selected);

            this.selected.addListener(new ChangeListener<Boolean>() {
                public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
                    if (t1) {
                        choosedDocName = docNameProperty().get();
                        listOfchoosedDocuments.add(choosedDocName);
                    }

                    if (t1 == false) {
                        unChoosedDocName = docNameProperty().get();
                        for (int i = 0; i < listOfchoosedDocuments.size(); i++) {

                            if (unChoosedDocName.equals(listOfchoosedDocuments.get(i))) {
                                listOfchoosedDocuments.remove(i);
                            }
                        }
                    }
                }
            });
        }

        public BooleanProperty selectedProperty() {
            return selected;
        }

        public StringProperty docNameProperty() {
            return docName;
        }


    }

    //CheckBoxTableCell for creating a CheckBox in a table cell
    public static class CheckBoxTableCell<S, T> extends TableCell<S, T> {
        private final CheckBox checkBox;
        private ObservableValue<T> ov;

        public CheckBoxTableCell() {
            this.checkBox = new CheckBox();
            this.checkBox.setAlignment(Pos.CENTER);

            setAlignment(Pos.CENTER);
            setGraphic(checkBox);
        }

        @Override
        public void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                setGraphic(checkBox);
                if (ov instanceof BooleanProperty) {
                    checkBox.selectedProperty().unbindBidirectional((BooleanProperty) ov);
                }
                ov = getTableColumn().getCellObservableValue(getIndex());
                if (ov instanceof BooleanProperty) {
                    checkBox.selectedProperty().bindBidirectional((BooleanProperty) ov);
                }
            }
        }
    }


    // Just for test now.
    public void importToXML() {
        if (checkThatDatesSelected()) {
            listOfPeriod.add(fromDate.getText());
            listOfPeriod.add(tillDate.getText());
        }
        if (checkGetDocIDFiled()) {
            try {
                String[] docs = getDocByID.getText().split(",");
                for (int i = 0; i < docs.length; i++) {
                    listOfDocsSelectedByID.add(Integer.valueOf(docs[i]));
                }
                docs=new String[0];
            } catch (Exception e) {
                new ErrorMsg("Некоректный номер документа");
            }
        }
        docNameConvertToID();

        /**
         * TEST
         */
        for (Integer value   : listOfDocIDByTypes)

        {
            System.out.println(value);
        }

        for (String value: listOfPeriod)

        {
            System.out.println(value);
        }

        for (String value : listOfchoosedDocuments)

        {
            System.out.println(value);
        }

        for (Integer value   : listOfDocsSelectedByID)

        {
            System.out.println(value);
        }

        listOfDocsSelectedByID.clear();
        listOfPeriod.clear();
        listOfchoosedDocuments.clear();
        listOfDocIDByTypes.clear();
    }

}



