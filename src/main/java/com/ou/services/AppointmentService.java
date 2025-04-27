package com.ou.services;

import com.ou.pojos.Appointment;
import com.ou.pojos.DBUtils;
import com.ou.pojos.Patient;
import com.ou.utils.exceptions.ValidatorException;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AppointmentService {
    public void add(Appointment appointment) throws SQLException {
        if (!appointment.getAppointmentDate().isAfter(LocalDate.now())) {
            throw new ValidatorException("Ngày đặt lịch phải sau ngày hiện tại", null);
        }
        try (Connection conn = DBUtils.getConnection()) {
            conn.setAutoCommit(false);
            PreparedStatement countAppointmentStm = conn.prepareStatement("select count(*) from appointments where appointment_date=?");
            countAppointmentStm.setDate(1, java.sql.Date.valueOf(appointment.getAppointmentDate()));
            ResultSet countAppointmentRs = countAppointmentStm.executeQuery();
            PreparedStatement countDoctorStm = conn.prepareStatement("select count(*) from users where role='doctor'");
            ResultSet countDoctorRs = countDoctorStm.executeQuery();
            if (!countAppointmentRs.next() || !countDoctorRs.next()) return;
            long appointmentCount = countAppointmentRs.getLong(1);
            long doctorCount = countDoctorRs.getLong(1);
            if (appointmentCount > doctorCount * 8) {
                conn.rollback();
                throw new ValidatorException("Quá giới hạn lịch hẹn trong ngày", null);
            }
            PreparedStatement insertStm = conn.prepareStatement("insert into appointments (patient_id, appointment_date) values (?, ?)", Statement.RETURN_GENERATED_KEYS);
            insertStm.setLong(1, appointment.getPatientId());
            insertStm.setDate(2, java.sql.Date.valueOf(appointment.getAppointmentDate()));
            insertStm.executeUpdate();
            ResultSet rs = insertStm.getGeneratedKeys();
            if (rs.next())
                appointment.setId(rs.getLong(1));
            conn.commit();
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new ValidatorException("Bệnh nhân đã có lịch hẹn trong ngày này", null);
            }
            throw e;
        }
    }

    public List<Appointment> getAppointments() throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            PreparedStatement stm = conn.prepareStatement("select * from appointments a left join patients p on p.id = a.patient_id where appointment_date >= ?");
            stm.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
            stm.executeQuery();
            ResultSet rs = stm.getResultSet();
            List<Appointment> appointments = new ArrayList<>();
            while (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.setId(rs.getLong("a.id"));
                appointment.setPatientId(rs.getLong("a.patient_id"));
                appointment.setAppointmentDate(rs.getDate("a.appointment_date").toLocalDate());
                System.out.println(rs.getBoolean("a.isCheckin"));
                appointment.setCheckin(rs.getBoolean("a.isCheckin"));

                Patient patient = new Patient();
                patient.setId(rs.getLong("p.id"));
                patient.setFirstName(rs.getString("p.first_name"));
                patient.setLastName(rs.getString("p.last_name"));
                patient.setMiddleName(rs.getString("p.middle_name"));
                patient.setIdCode(rs.getString("p.id_code"));
                appointment.setPatient(patient);
                appointments.add(appointment);
            }
            return appointments;
        }
    }

    public void delete(long appointmentId) throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            PreparedStatement stm = conn.prepareStatement("delete from appointments where id = ?");
            stm.setLong(1, appointmentId);
            stm.executeUpdate();
        }
    }

    public void updateStatus(long appointmentId, boolean status) throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            conn.setAutoCommit(false);

            if (status) {
                PreparedStatement checkTicketStm = conn.prepareStatement("select count(*) from tickets where appointment_id = ?");
                checkTicketStm.setLong(1, appointmentId);
                ResultSet rs = checkTicketStm.executeQuery();
                if (!rs.next() || rs.getLong(1) <= 0) {
                    conn.rollback();
                    throw new ValidatorException("Phiên khám của lịch hẹn chưa đuược tạo", null);
                }
            }

            PreparedStatement updateStm = conn.prepareStatement("update appointments set isCheckin = ? where id = ?");
            updateStm.setBoolean(1, status);
            updateStm.setLong(2, appointmentId);
            updateStm.executeUpdate();
            conn.commit();
        }
    }
}
