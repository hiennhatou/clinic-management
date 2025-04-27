package com.ou.clinicmanagement;

import com.ou.pojos.DBUtils;
import com.ou.pojos.Patient;
import com.ou.pojos.Ticket;
import com.ou.pojos.User;
import com.ou.services.PatientService;
import com.ou.services.TicketService;
import com.ou.services.UserService;
import com.ou.utils.exceptions.ValidatorException;
import org.junit.jupiter.api.*;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.*;
import java.util.TimeZone;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestTicket {
    public static Patient patient;
    public static User user;
    public static Ticket ticket;

    static final PatientService patientService = new PatientService();
    static final TicketService ticketService = new TicketService();
    static final UserService userService = new UserService();

    public static void SetDefaultTimeZoneInTime() {
        for (int i = -9; i <= 9; i++) {
            TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.ofOffset("UTC", ZoneOffset.of(String.format("%s0%d:00", i < 0 ? "-": "+", Math.abs(i))))));
            if (LocalDateTime.now().getHour() < 16) return;
        }
    }

    public static void SetDefaultTimeZoneOutOfTime() {
        for (int i = -9; i <= 9; i++) {
            TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.ofOffset("UTC", ZoneOffset.of(String.format("%s0%d:00", i < 0 ? "-": "+", Math.abs(i))))));
            if (LocalDateTime.now().getHour() > 16) return;
        }
    }

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
    public void createTicket() throws SQLException {
        TestTicket.SetDefaultTimeZoneInTime();
        ticket = ticketService.createTicket(user.getId(), patient.getId());
        Assertions.assertAll(
            () -> Assertions.assertNotNull(ticket),
            () -> Assertions.assertTrue(ticket.getId() > 0)
        );
    }

    /// Test trường hợp tạo phiên khám khi đã có một phiên khám khác chưa hoàn thành
    @Test
    @Order(2)
    public void createTicketInAlreadyInSession() throws SQLException {
        TestTicket.SetDefaultTimeZoneInTime();
        Assertions.assertThrows(ValidatorException.class, () -> ticketService.createTicket(user.getId(), patient.getId()), "Bệnh nhân đã tồn tại phiên khám");
    }

    @Test
    @Order(3)
    public void createTicketOutOfTime() throws SQLException {
        TestTicket.SetDefaultTimeZoneOutOfTime();
        Assertions.assertThrows(ValidatorException.class, () -> ticketService.createTicket(user.getId(), patient.getId()), "Đã quá giờ tiếp nhận bệnh nhân");
    }

    @AfterAll
    public static void tearDown() throws SQLException {
        if (ticket != null)
            try (Connection connection = DBUtils.getConnection()) {
                PreparedStatement stm = connection.prepareStatement("delete from tickets where id = ?");
                stm.setLong(1, ticket.getId());
                stm.execute();
            }

        if (user != null)
            try (Connection connection = DBUtils.getConnection()) {
                PreparedStatement stm = connection.prepareStatement("delete from users where id = ?");
                stm.setLong(1, user.getId());
                stm.execute();
            }

        if (patient != null)
            try (Connection conn = DBUtils.getConnection()) {
                PreparedStatement stm = conn.prepareStatement("delete from patients where id = ?");
                stm.setLong(1, patient.getId());
                stm.execute();
            }
    }
}
