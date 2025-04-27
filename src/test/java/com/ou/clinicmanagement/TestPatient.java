package com.ou.clinicmanagement;

import com.ou.pojos.DBUtils;
import com.ou.pojos.Patient;
import com.ou.services.PatientService;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Test Patient")
public class TestPatient {
    private final PatientService patientService = new PatientService();
    static private long id;

    @Test
    @Order(1)
    public void testCreatePatient() throws SQLException {
        Patient patient = new Patient();
        patient.setFirstName("TestFirstName");
        patient.setLastName("TestLastName");
        patient.setMiddleName("TestMiddleName");
        patient.setBirthday(LocalDate.of(2000, Month.FEBRUARY, 1));
        patient.setIdCode("012344556677");
        patientService.insertPatient(patient);
        id = patient.getId();
        Assertions.assertTrue(patient.getId() > 0);
    }

    @Test
    @Order(2)
    public void checkPatientInform() throws SQLException {
        Patient patient = patientService.getPatientByIdCode("012344556677");
        Assertions.assertAll(
            () -> Assertions.assertNotNull(patient),
            () -> Assertions.assertEquals("TestFirstName", patient.getFirstName()),
            () -> Assertions.assertEquals("TestLastName", patient.getLastName()),
            () -> Assertions.assertEquals("TestMiddleName", patient.getMiddleName()),
            () -> Assertions.assertEquals(LocalDate.of(2000, Month.FEBRUARY, 1), patient.getBirthday())
        );
    }

    @Test
    @Order(3)
    public void testDuplicateIdCode() throws SQLException {
        Patient patient = new Patient();
        patient.setFirstName("TestFirstName");
        patient.setLastName("TestLastName");
        patient.setMiddleName("TestMiddleName");
        patient.setBirthday(LocalDate.of(2000, Month.FEBRUARY, 1));
        patient.setIdCode("012344556677");
        Assertions.assertThrows(SQLException.class, () -> patientService.insertPatient(patient));
    }

    @Test
    @Order(4)
    public void updateFirstName() throws SQLException {
        patientService.updateFirstName("Hien", id);
        Patient patient = patientService.getPatientByIdCode("012344556677");
        Assertions.assertEquals("Hien", patient.getFirstName());
    }

    @Test
    @Order(5)
    public void testUpdateLastName() throws SQLException {
        patientService.updateLastName("Nguyen", id);
        Patient patient = patientService.getPatientByIdCode("012344556677");
        Assertions.assertEquals("Nguyen", patient.getLastName());
    }

    @Test
    @Order(6)
    public void testUpdateBirthday() throws SQLException {
        patientService.updateBirthday(LocalDate.of(1999, Month.FEBRUARY, 1), id);
        Patient patient = patientService.getPatientByIdCode("012344556677");
        Assertions.assertEquals(LocalDate.of(1999, Month.FEBRUARY, 1), patient.getBirthday());
    }

    @Test
    @Order(7)
    public void updateMiddleName() throws SQLException {
        patientService.updateMiddleName("Nhat", id);
        Patient patient = patientService.getPatientByIdCode("012344556677");
        Assertions.assertEquals("Nhat", patient.getMiddleName());
    }

    @AfterAll
    public static void tearDown() throws SQLException {
        try (Connection connection = DBUtils.getConnection()) {
            connection.prepareStatement("delete from patients where id_code = '012344556677'").executeUpdate();
        }
    }
}
