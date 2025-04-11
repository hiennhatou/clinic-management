module com.ou.clinicmanagement {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires java.sql;
    requires java.prefs;

    opens com.ou.clinicmanagement to javafx.fxml;
    exports com.ou.clinicmanagement;
    exports com.ou.pojos;
    exports com.ou.services;
    exports com.ou.utils;
    exports com.ou.utils.userbuilder;
    exports com.ou.utils.secure.hash;
}