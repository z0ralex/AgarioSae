module com.example.agarioclient {
    requires javafx.controls;
    requires javafx.fxml;


    opens iut.gon.agarioclient to javafx.fxml;
    exports iut.gon.agarioclient;
    exports iut.gon.agarioclient.controller;
    opens iut.gon.agarioclient.controller to javafx.fxml;
}