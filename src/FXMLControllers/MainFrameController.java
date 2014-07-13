package FXMLControllers;

import ConnectionManager.ErrorMsge;
import DataOperator.QueryBuilder;
import DataOperator.XMLBuilder;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MainFrameController implements Initializable {

    @FXML
    private TextField fromDate;
    @FXML
    private TextField tillDate;
    @FXML
    private TextField getDocByID;


    //Stores date selection interval
    public static LinkedList<String> listOfPeriod = new LinkedList<>();
    // Stores selected Documents ID for XML import.
    public static ArrayList<Integer> listOfDocIDByTypes = new ArrayList<>();
    // Stores Documents ID if selected
    public static ArrayList<String> listOfDocsSelectedByID = new ArrayList<>();

    //Temporary values for data transfer
    private ArrayList<String> listOfchoosedDocuments = new ArrayList<>();
    private SimpleDateFormat currentFormat = new SimpleDateFormat("dd/MM/yyyy");
    private String choosedDocName;
    private String unChoosedDocName;

    Connection connection = null;
    //Java FX collection that holds all document names in alphabet order
    ObservableList<String> docNames = FXCollections.observableArrayList();
    //Java FX collection that holds all document names and documents codes with no order
    public static ObservableMap<Integer, String> mapOfData = FXCollections.observableHashMap();


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
    public void getDocTypeListToPreview() {
        try {
            connection = DriverManager.getConnection(ConnectionController.getConnParams);
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
        ArrayList<Document> list = new ArrayList<>();
        for (String docName : docNames) {
            list.add(new Document(false, docName));
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

    //Converts choosed Document names to Documents ID and puts them to listOfDocIDByTypes (data for transfer)
    public void docNameConvertToID() {
        for (Map.Entry<Integer, String> entry : mapOfData.entrySet()) {
            for (String value : listOfchoosedDocuments) {
                String searchingDocName = entry.getValue();
                int searchingDocCode = entry.getKey();

                if (searchingDocName.equals(value)) {
                    int docCode = searchingDocCode;
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
            new ErrorMsge("Внимание! Не корректные даты.\n Введите даты в формате \n День/Месяц/Год (дд/мм/гггг)");
            return false;
        }
    }

    //Checks if field 'getDocByID' is empty or incorrect (transl. -  'Поиск по номеру документа' )
    // If filed has a valid ID's - app add it to search list
    public void checkGetDocIDFiled() {
        if (getDocByID.getText() != "" && getDocByID.getText() != null && getDocByID.getText().length() != 0) {
            List<String> docsID = Arrays.asList(getDocByID.getText().split("\\s*,\\s*"));
            for (int i = 0; i < docsID.size(); i++) {
                String regex = "[0-9]+";
                if (docsID.get(i).matches(regex)) {
                    listOfDocsSelectedByID.add(docsID.get(i));
                }
            }
        }
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
            new ErrorMsge("Проверьте формат даты.");
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
            new ErrorMsge("Проверьте формат даты.");
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
            new ErrorMsge("Проверьте формат даты.");
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
            new ErrorMsge("Проверьте формат даты.");
        }

    }

    //Convert date format in SQL date format (to avoid SQL exception)
    public static String dateConvertSQLFormat(String givenDate) {
        String year = givenDate.substring(6);
        String month = givenDate.substring(3, 5);
        String date = givenDate.substring(0, 2);
        return year + "-" + month + "-" + date;
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

    // Open Saving dialog for user
    public void fileChooser(String content) {
        Group root = new Group();
        Stage stage = new Stage();
        stage.setScene(new Scene(root, 500, 400));
        stage.centerOnScreen();
        FileChooser fileChooser = new FileChooser();


        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            SaveFile(content, file);
        }
    }

    // Saving file method
    private void SaveFile(String content, File file) {
        try {
            FileWriter fileWriter;
            fileWriter = new FileWriter(file);
            fileWriter.write(content);
            fileWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(MainFrameController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Button 'Import to XML' - last step of application.
     * IN TEST MODE NOW!
     *
     * @throws SQLException
     */
    public void importToXML() throws SQLException, IOException {
        listOfPeriod.clear();
        listOfDocIDByTypes.clear();
        listOfDocsSelectedByID.clear();

        try {
            if (checkThatDatesSelected()) {
                XMLBuilder xmlBuilder = new XMLBuilder();
                listOfPeriod.add(dateConvertSQLFormat(fromDate.getText()));
                listOfPeriod.add(dateConvertSQLFormat(tillDate.getText()));
                //check if user selected any docs id
                checkGetDocIDFiled();
                docNameConvertToID();
                //Check if any data available
                fileChooser(xmlBuilder.stringBuilderToXMLFormat());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            new ErrorMsge("В данном периоде нет документов");
        }

        listOfPeriod.clear();
        listOfDocIDByTypes.clear();
        listOfDocsSelectedByID.clear();
    }
}



