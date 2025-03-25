module com.example.agarioclient {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.agarioclient to javafx.fxml;
    exports com.example.agarioclient;
}