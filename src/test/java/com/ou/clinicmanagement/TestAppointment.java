package com.ou.clinicmanagement;

import com.ou.pojos.*;
import com.ou.services.AppointmentService;
import com.ou.services.PatientService;
import com.ou.services.TicketService;
import com.ou.services.UserService;
import com.ou.utils.exceptions.ValidatorException;
import org.junit.jupiter.api.*;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestAppointment {
    public static Patient patient;
    public static User user;
    public static Appointment appointment;
    public static Ticket ticket;
    static final PatientService patientService = new PatientService();
    static final AppointmentService appointmentService = new AppointmentService();
    static final TicketService ticketService = new TicketService();
    static final UserService userService = new UserService();

    @BeforeAll
    public static void setUp() throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        patient = new Patient();
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setIdCode("001122334455");
        patient.setMiddleName("D");
        patient.setBirthday(LocalDate.of(2000, Month.AUGUST, 1));
        patientService.insertPatient(patient);

        user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setMiddleName("D");
        user.setUsername("johndoe");
        user.setPassword("H@owDofq13231");
        user.setRole("DOCTOR");
        userService.createUser(user);
    }

    @Test
    @Order(1)
    public void createAppointment() throws SQLException {
        appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setPatientId(patient.getId());
        appointment.setAppointmentDate(LocalDate.now().plusDays(1));
        appointmentService.add(appointment);
        Assertions.assertTrue(appointment.getId() > 0);
    }

    @Test
    @Order(2)
    public void createTheSameDayAppointment() throws SQLException {
        Appointment appointment1 = new Appointment();
        appointment1.setPatient(patient);
        appointment1.setPatientId(patient.getId());
        appointment1.setAppointmentDate(appointment.getAppointmentDate());
        Assertions.assertThrows(ValidatorException.class, () -> appointmentService.add(appointment1), "Bệnh nhân đã có lịch hẹn trong ngày này");
    }

    @Test
    @Order(3)
    public void createAppointmentInPresentDate() throws SQLException {
        Appointment appointment1 = new Appointment();
        appointment1.setPatient(patient);
        appointment1.setPatientId(patient.getId());
        appointment1.setAppointmentDate(LocalDate.now());
        Assertions.assertThrows(ValidatorException.class, () -> appointmentService.add(appointment1), "Ngày đặt lịch phải sau ngày hiện tại");
    }

    @Test
    @Order(4)
    public void receiveAppointmentInTicketNotExist() throws SQLException {
        Assertions.assertThrows(ValidatorException.class, () -> appointmentService.updateStatus(appointment.getId(), true), "Phiên khám của lịch hẹn chưa đuược tạo");
    }

    @Test
    @Order(5)
    public void receiveAppointmentInTicketExist() throws SQLException {
        TestTicket.SetDefaultTimeZoneInTime();
        ticket = ticketService.createTicket(user.getId(), patient.getId(), appointment.getId());
        Assertions.assertDoesNotThrow(() -> appointmentService.updateStatus(appointment.getId(), true));
    }

    @AfterAll
    public static void tearDown() {
        if (ticket != null)
            try (Connection conn = DBUtils.getConnection()) {
                conn.prepareStatement("delete from tickets where id = " + ticket.getId()).executeUpdate();
            } catch (SQLException e) {}

        if (appointment != null)
            try (Connection conn = DBUtils.getConnection()) {
                conn.prepareStatement("delete from appointments where id = " + appointment.getId()).executeUpdate();
            } catch (SQLException e) {}

        if (patient != null)
            try (Connection conn = DBUtils.getConnection()) {
                conn.prepareStatement("delete from patients where id = " + patient.getId()).executeUpdate();
            } catch (SQLException e) {}

        if (user != null)
            try (Connection conn = DBUtils.getConnection()) {
                conn.prepareStatement("delete from users where id = " + user.getId()).executeUpdate();
            } catch (SQLException e) {}
    }
}
