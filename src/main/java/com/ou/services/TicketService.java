package com.ou.services;

import com.ou.pojos.DBUtils;
import com.ou.pojos.Patient;
import com.ou.pojos.Ticket;
import com.ou.pojos.User;
import com.ou.utils.exceptions.ValidatorException;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TicketService {
    public Ticket createTicket(long doctorId, long patientId) throws SQLException {
        return createTicket(doctorId, patientId, 0);
    }

    public Ticket createTicket(long doctorId, long patientId, long appointmentId) throws SQLException {
        LocalDateTime now = LocalDateTime.now();
        if (now.getHour() > 16 || (now.getHour() == 16 && now.getMinute() > 15))
            throw new ValidatorException("Đã quá giờ tiếp nhận bệnh nhân", null);
        try (Connection conn = DBUtils.getConnection()) {
            conn.setAutoCommit(false);

            if (appointmentId > 0) {
                PreparedStatement checkAppointmentTime = conn.prepareStatement("SELECT COUNT(*) FROM appointments WHERE id = ? AND appointment_date = ?");
                checkAppointmentTime.setLong(1, appointmentId);
                checkAppointmentTime.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
                checkAppointmentTime.executeQuery();
                ResultSet checkAppointmentTimeRS = checkAppointmentTime.getResultSet();
                if (checkAppointmentTimeRS.next() && checkAppointmentTimeRS.getLong(1) < 0) {
                    conn.rollback();
                    throw new ValidatorException("Lịch hẹn không phù hợp", null);
                }
            }

            PreparedStatement checkAvailableDoctorStm = conn.prepareStatement("select count(*) from tickets where doctor_id=? and created_on >=? and created_on < ?");
            checkAvailableDoctorStm.setLong(1, doctorId);
            checkAvailableDoctorStm.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
            checkAvailableDoctorStm.setDate(3, java.sql.Date.valueOf(LocalDate.now().plusDays(1)));
            checkAvailableDoctorStm.executeQuery();
            ResultSet checkAvailableDoctorRs = checkAvailableDoctorStm.getResultSet();
            if (checkAvailableDoctorRs.next() && checkAvailableDoctorRs.getInt(1) >= 20) {
                conn.rollback();
                throw new ValidatorException("Bác sĩ đã tiếp nhận quá số phiên khám quy định", null);
            }

            PreparedStatement checkAvailablePatient = conn.prepareStatement("select count(*) from tickets where patient_id=? and status in ('created', 'checked_in')");
            checkAvailablePatient.setLong(1, patientId);
            checkAvailablePatient.executeQuery();
            ResultSet checkAvailablePatientRs = checkAvailablePatient.getResultSet();
            if (checkAvailablePatientRs.next() && checkAvailablePatientRs.getInt(1) >= 1) {
                conn.rollback();
                throw new ValidatorException("Bệnh nhân đã tồn tại phiên khám", null);
            }

            PreparedStatement insetStm = conn.prepareStatement("insert into tickets (patient_id, doctor_id, appointment_id) values (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            insetStm.setLong(1, patientId);
            insetStm.setLong(2, doctorId);
            if (appointmentId <= 0)
                insetStm.setNull(3, Types.BIGINT);
            else
                insetStm.setLong(3, appointmentId);
            insetStm.executeUpdate();
            conn.commit();

            ResultSet keys = insetStm.getGeneratedKeys();
            if (keys.next()) {
                Ticket ticket = new Ticket();
                ticket.setDoctorId(doctorId);
                ticket.setPatientId(patientId);
                ticket.setId(keys.getLong(1));
                return ticket;
            }
            return null;
        }
    }

    public String updateStatus(long id, long patientId, String status) throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            if (status == null) return null;
            conn.setAutoCommit(false);
            if (status.equals("checked_in") || status.equals("created")) {
                PreparedStatement stm1 = conn.prepareStatement("select id from tickets where patient_id = ? and ((id = ? and status = 'done') or (id <> ? and status in ('checked_in', 'created')))");
                stm1.setLong(1, patientId);
                stm1.setLong(2, id);
                stm1.setLong(3, id);
                ResultSet rs = stm1.executeQuery();
                if (rs.next()) {
                    throw new ValidatorException("Bệnh nhân đang có phiên khám khác hoặc phiên khám này đã kết thúc", null);
                }
            }
            PreparedStatement stm = conn.prepareStatement("update tickets set status = ? where id = ?");
            stm.setString(1, status);
            stm.setLong(2, id);
            stm.executeUpdate();
            conn.commit();
            return status;
        }
    }

    public List<Ticket> getAllTickets() throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            PreparedStatement stm = conn.prepareStatement("select * from tickets t left join users u on u.id = t.doctor_id left join patients p on p.id = t.patient_id");
            ResultSet rs = stm.executeQuery();
            List<Ticket> tickets = new ArrayList<>();
            while (rs.next()) {
                Ticket ticket = new Ticket();
                ticket.setId(rs.getLong("t.id"));
                ticket.setDoctorId(rs.getLong("t.doctor_id"));
                ticket.setPatientId(rs.getLong("t.patient_id"));
                ticket.setCreatedOn(rs.getTimestamp("t.created_on").toLocalDateTime());
                ticket.setStatus(rs.getString("t.status"));

                User doctor = new User();
                doctor.setId(rs.getLong("u.id"));
                doctor.setFirstName(rs.getString("u.first_name"));
                doctor.setLastName(rs.getString("u.last_name"));
                doctor.setMiddleName(rs.getString("u.middle_name"));
                ticket.setDoctor(doctor);

                Patient patient = new Patient();
                patient.setId(rs.getLong("p.id"));
                patient.setFirstName(rs.getString("p.first_name"));
                patient.setLastName(rs.getString("p.last_name"));
                patient.setMiddleName(rs.getString("p.middle_name"));
                patient.setIdCode(rs.getString("p.id_code"));
                ticket.setPatient(patient);

                tickets.add(ticket);
            }
            return tickets;
        }
    }

    public void updateDoctor(long id, long doctorId) throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            conn.setAutoCommit(false);
            PreparedStatement conditionalStm = conn.prepareStatement("select id from tickets where id = ? and status = 'done'");
            conditionalStm.setLong(1, id);
            ResultSet rs = conditionalStm.executeQuery();
            if (rs.next()) {
                throw new ValidatorException("Phiên khám đã hoàn tất", null);
            }
            PreparedStatement stm = conn.prepareStatement("update tickets set doctor_id = ? where id = ?");
            stm.setLong(1, doctorId);
            stm.setLong(2, id);
            stm.executeUpdate();

            conn.commit();
        }
    }

    public List<Ticket> getTicketsByDoctor(long doctorId) throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            PreparedStatement stm = conn.prepareStatement("select t.id, t.doctor_id, t.patient_id, t.created_on, t.status, p.first_name, p.last_name, p.middle_name from tickets t left join patients p on p.id = t.patient_id where t.doctor_id = ? and t.status in ('checked_in', 'created') order by t.created_on desc");
            stm.setLong(1, doctorId);
            ResultSet rs = stm.executeQuery();
            List<Ticket> tickets = new ArrayList<>();
            while (rs.next()) {
                Ticket ticket = new Ticket();
                ticket.setId(rs.getLong("t.id"));
                ticket.setDoctorId(rs.getLong("t.doctor_id"));
                ticket.setPatientId(rs.getLong("t.patient_id"));
                ticket.setCreatedOn(rs.getTimestamp("t.created_on").toLocalDateTime());
                ticket.setStatus(rs.getString("t.status"));

                Patient patient = new Patient();
                patient.setFirstName(rs.getString("p.first_name"));
                patient.setLastName(rs.getString("p.last_name"));
                patient.setMiddleName(rs.getString("p.middle_name"));
                ticket.setPatient(patient);
                tickets.add(ticket);
            }
            return tickets;
        }
    }

    public Ticket getTicketById(long id) throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            PreparedStatement stm = conn.prepareStatement("select * from tickets t left join patients p on p.id = t.patient_id where t.id = ?");
            stm.setLong(1, id);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                Ticket ticket = new Ticket();
                ticket.setId(rs.getLong("t.id"));
                ticket.setDoctorId(rs.getLong("t.doctor_id"));
                ticket.setPatientId(rs.getLong("t.patient_id"));
                ticket.setCreatedOn(rs.getTimestamp("t.created_on").toLocalDateTime());
                ticket.setStatus(rs.getString("t.status"));
                Patient patient = new Patient();
                patient.setFirstName(rs.getString("p.first_name"));
                patient.setLastName(rs.getString("p.last_name"));
                patient.setMiddleName(rs.getString("p.middle_name"));
                patient.setBirthday(rs.getDate("p.birthday").toLocalDate());
                ticket.setPatient(patient);
                return ticket;
            }
            return null;
        }
    }
}
