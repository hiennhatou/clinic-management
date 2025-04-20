package com.ou.services;

import com.ou.pojos.DBUtils;
import com.ou.pojos.Patient;
import com.ou.pojos.Ticket;
import com.ou.pojos.User;
import com.ou.utils.exceptions.ValidatorException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketService {
    public Ticket createTicket(long doctorId, long patientId) throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            conn.setAutoCommit(false);
            PreparedStatement stm1 = conn.prepareStatement("select * from tickets where patient_id = ? and status in ('created', 'checked_in')");
            stm1.setLong(1, patientId);
            ResultSet rs = stm1.executeQuery();
            if (rs.next()) {
                String message = String.format("Bệnh nhân đang được khám (ID: %d)", rs.getLong("id"));
                conn.rollback();
                throw new ValidatorException(message, null);
            }
            PreparedStatement stm = conn.prepareStatement("insert into tickets(doctor_id, patient_id) values(?,?)", Statement.RETURN_GENERATED_KEYS);
            stm.setLong(1, doctorId);
            stm.setLong(2, patientId);
            stm.executeUpdate();
            conn.commit();
            ResultSet keys = stm.getGeneratedKeys();
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
}
