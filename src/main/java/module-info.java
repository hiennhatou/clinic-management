module com.ou.clinicmanagement {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires java.sql;
    requires mysql.connector.j;

    opens com.ou.clinicmanagement to javafx.fxml;
    exports com.ou.clinicmanagement;
    exports com.ou.pojos;
    exports com.ou.services;
    exports com.ou.utils;
}