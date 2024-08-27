module com.example.prog3proj1 {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires mpj;

    opens com.example.prog3proj1 to javafx.fxml;
    exports com.example.prog3proj1;
}