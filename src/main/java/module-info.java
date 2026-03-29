module orchester.orchester {
    requires javafx.controls;
    requires javafx.fxml;


    opens orchester.controller to javafx.fxml;
    exports orchester.controller;
    exports orchester;
    opens orchester to javafx.fxml;
}