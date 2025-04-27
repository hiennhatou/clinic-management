module com.ou.clinicmanagement {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires java.sql;
    requires java.prefs;
    requires org.controlsfx.controls;
    requires java.desktop;

    opens com.ou.clinicmanagement to javafx.fxml;
    exports com.ou.clinicmanagement;
    exports com.ou.pojos;
    exports com.ou.services;
    exports com.ou.utils;
    exports com.ou.utils.secure.hash;
    exports com.ou.clinicmanagement.welcome;
    opens com.ou.clinicmanagement.welcome to javafx.fxml;
}