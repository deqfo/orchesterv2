package orchester.controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import orchester.models.Nastroj;
import orchester.models.SlacikovyNastroj;
import orchester.models.StrunovyNastroj;
import orchester.service.Service;

import java.io.File;
import java.io.IOException;

public class MainController {

    private static String ZAKLADNY = "Zakladny";
    private static String SLACIKOVY = "Slacikovy";
    private static String STRUNOVY = "Strunovy";

    private Service service = new Service();

    @FXML
    private TableView<Nastroj> nastrojeTable;
    @FXML
    private TableColumn<Nastroj, String> typColumn;
    @FXML
    private TableColumn<Nastroj, String> druhColumn;
    @FXML
    private TableColumn<Nastroj, Number> cenaColumn;
    @FXML
    private TableColumn<Nastroj, String> zvukColumn;
    @FXML
    private TableColumn<Nastroj, Number> pocetColumn;
    @FXML
    private TableColumn<Nastroj, String> detailColumn;

    @FXML
    private ComboBox<String> typComboBox;
    @FXML
    private TextField druhField;
    @FXML
    private TextField cenaField;
    @FXML
    private TextField zvukField;
    @FXML
    private TextField pocetField;
    @FXML
    private Label extraField1Label;
    @FXML
    private TextField extraField1;
    @FXML
    private Label extraField2Label;
    @FXML
    private TextField extraField2;
    @FXML
    private Label statusLabel;
    @FXML
    private TextArea vystupArea;

    @FXML
    public void initialize() {
        typComboBox.getItems().addAll(ZAKLADNY, SLACIKOVY, STRUNOVY);
        typComboBox.setValue(ZAKLADNY);

        nastavTabulku();

        typComboBox.setOnAction(event -> updateExtraFields());
        nastrojeTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> fillForm(newValue));

        updateExtraFields();

        try {
            service.loadFromResource("/orchester/data.txt");
            refreshTable();
            statusLabel.setText("Nacitane predvolene data.");
        } catch (IOException e) {
            statusLabel.setText("Predvolene data sa nepodarilo nacitat.");
        }
    }

    @FXML
    private void handleAdd() {
        try {
            Nastroj nastroj = createFromForm();
            service.addNastroj(nastroj);
            refreshTable();
            clearForm();
            statusLabel.setText("Nastroj bol pridany.");
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        int index = nastrojeTable.getSelectionModel().getSelectedIndex();
        if (index == -1) {
            showError("Najprv vyber nastroj v tabulke.");
            return;
        }

        try {
            Nastroj nastroj = createFromForm();
            service.updateNastroj(index, nastroj);
            refreshTable();
            clearForm();
            statusLabel.setText("Nastroj bol upraveny.");
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        Nastroj selected = nastrojeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Najprv vyber nastroj v tabulke.");
            return;
        }

        service.removeNastroj(selected);
        refreshTable();
        clearForm();
        statusLabel.setText("Nastroj bol zmazany.");
    }

    @FXML
    private void handleLoadDefault() {
        try {
            service.loadFromResource("/orchester/data.txt");
            refreshTable();
            clearForm();
            statusLabel.setText("Data boli nacitane z resources.");
        } catch (Exception e) {
            showError("Nacitavanie zlyhalo: " + e.getMessage());
        }
    }

    @FXML
    private void handleLoadFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Vyber subor");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*.txt"));

        File file = fileChooser.showOpenDialog(nastrojeTable.getScene().getWindow());
        if (file == null) {
            return;
        }

        try {
            service.loadFromFile(file.toPath());
            refreshTable();
            clearForm();
            statusLabel.setText("Data boli nacitane zo suboru: " + file.getName());
        } catch (Exception e) {
            showError("Nacitavanie zo suboru zlyhalo: " + e.getMessage());
        }
    }

    @FXML
    private void handleSaveFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Uloz subor");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*.txt"));
        fileChooser.setInitialFileName("data.txt");

        File file = fileChooser.showSaveDialog(nastrojeTable.getScene().getWindow());
        if (file == null) {
            return;
        }

        try {
            service.saveToFile(file.toPath());
            statusLabel.setText("Data boli ulozene do suboru: " + file.getName());
        } catch (Exception e) {
            showError("Ukladanie zlyhalo: " + e.getMessage());
        }
    }

    @FXML
    private void handleVypisSkladu() {
        showPopupWindow("Vypis skladu", service.vytvorVypisSkladu());
        statusLabel.setText("Zobrazeny vypis skladu.");
    }

    @FXML
    private void handleCenaVystupenia() {
        vystupArea.setText(service.vytvorCenuVystupenia());
        statusLabel.setText("Vypocitana cena vystupenia.");
    }

    @FXML
    private void handleSkladHraj() {
        vystupArea.setText(service.vytvorSkladHraj());
        statusLabel.setText("Zobrazeny vystup sklad hraj.");
    }

    private Nastroj createFromForm() {
        String typ = typComboBox.getValue();
        String druh = druhField.getText().trim();
        String zvuk = zvukField.getText().trim();

        if (druh.isEmpty()) {
            throw new IllegalArgumentException("Druh nesmie byt prazdny.");
        }
        if (zvuk.isEmpty()) {
            throw new IllegalArgumentException("Zvuk nesmie byt prazdny.");
        }

        double cena = nacitajCenu();
        int pocet = nacitajPocet();

        if (ZAKLADNY.equals(typ)) {
            return new Nastroj(druh, cena, zvuk, pocet);
        }

        if (SLACIKOVY.equals(typ)) {
            String sekcia = extraField1.getText().trim();
            if (sekcia.isEmpty()) {
                throw new IllegalArgumentException("Sekcia nesmie byt prazdna.");
            }
            return new SlacikovyNastroj(druh, cena, zvuk, pocet, sekcia);
        }

        int pocetStrun = nacitajPocetStrun();
        String ladenie = extraField2.getText().trim();
        if (ladenie.isEmpty()) {
            throw new IllegalArgumentException("Ladenie nesmie byt prazdne.");
        }

        return new StrunovyNastroj(druh, cena, zvuk, pocet, pocetStrun, ladenie);
    }

    private void fillForm(Nastroj nastroj) {
        if (nastroj == null) {
            return;
        }

        druhField.setText(nastroj.getDruh());
        cenaField.setText(String.valueOf(nastroj.getCena()));
        zvukField.setText(nastroj.getZvuk());
        pocetField.setText(String.valueOf(nastroj.getPocet()));

        if (nastroj instanceof SlacikovyNastroj) {
            SlacikovyNastroj s = (SlacikovyNastroj) nastroj;
            typComboBox.setValue(SLACIKOVY);
            updateExtraFields();
            extraField1.setText(s.getSekcia());
            extraField2.clear();
        } else if (nastroj instanceof StrunovyNastroj) {
            StrunovyNastroj s = (StrunovyNastroj) nastroj;
            typComboBox.setValue(STRUNOVY);
            updateExtraFields();
            extraField1.setText(String.valueOf(s.getPocetStrun()));
            extraField2.setText(s.getLadenie());
        } else {
            typComboBox.setValue(ZAKLADNY);
            updateExtraFields();
            extraField1.clear();
            extraField2.clear();
        }
    }

    private void refreshTable() {
        nastrojeTable.getItems().setAll(service.getNastroje());
    }

    private void clearForm() {
        nastrojeTable.getSelectionModel().clearSelection();
        typComboBox.setValue(ZAKLADNY);
        druhField.clear();
        cenaField.clear();
        zvukField.clear();
        pocetField.clear();
        extraField1.clear();
        extraField2.clear();
        updateExtraFields();
    }

    private void updateExtraFields() {
        String typ = typComboBox.getValue();

        if (ZAKLADNY.equals(typ)) {
            nastavViditelnostExtraPoloziek(false, false);
            extraField1.clear();
            extraField2.clear();
            return;
        }

        if (SLACIKOVY.equals(typ)) {
            extraField1Label.setText("Sekcia");
            nastavViditelnostExtraPoloziek(true, false);
            extraField2.clear();
            return;
        }

        extraField1Label.setText("Struny");
        extraField2Label.setText("Ladenie");
        nastavViditelnostExtraPoloziek(true, true);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Chyba");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showPopupWindow(String title, String content) {
        TextArea textArea = new TextArea(content);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        VBox root = new VBox(textArea);
        root.setPadding(new Insets(12));

        Stage popupStage = new Stage();
        popupStage.initOwner(nastrojeTable.getScene().getWindow());
        popupStage.initModality(Modality.NONE);
        popupStage.setTitle(title);
        popupStage.setScene(new Scene(root, 520, 360));
        popupStage.show();
    }

    private void nastavTabulku() {
        typColumn.setCellValueFactory(data -> new SimpleStringProperty(service.getTyp(data.getValue())));
        druhColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDruh()));
        cenaColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getCena()));
        zvukColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getZvuk()));
        pocetColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getPocet()));
        detailColumn.setCellValueFactory(data -> new SimpleStringProperty(service.getDetail(data.getValue())));
    }

    private double nacitajCenu() {
        try {
            return Double.parseDouble(cenaField.getText().trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("Cena musi byt cislo.");
        }
    }

    private int nacitajPocet() {
        try {
            return Integer.parseInt(pocetField.getText().trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("Pocet musi byt cele cislo.");
        }
    }

    private int nacitajPocetStrun() {
        try {
            return Integer.parseInt(extraField1.getText().trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("Pocet strun musi byt cele cislo.");
        }
    }

    private void nastavViditelnostExtraPoloziek(boolean prveViditelne, boolean druheViditelne) {
        extraField1Label.setVisible(prveViditelne);
        extraField1.setVisible(prveViditelne);
        extraField1Label.setManaged(prveViditelne);
        extraField1.setManaged(prveViditelne);

        extraField2Label.setVisible(druheViditelne);
        extraField2.setVisible(druheViditelne);
        extraField2Label.setManaged(druheViditelne);
        extraField2.setManaged(druheViditelne);
    }
}
