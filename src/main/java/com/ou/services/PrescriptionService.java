package com.ou.services;

import com.ou.pojos.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionService {
    public Prescription getPrescriptionById(long id) throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            PreparedStatement stm = conn.prepareStatement("select * from prescriptions p left join prescription_medicine pm on pm.prescription_id = p.id left join medicines m on m.id = pm.medicine_id where p.id = ?");
            stm.setLong(1, id);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                Prescription prescription = new Prescription();
                prescription.setId(rs.getLong("p.id"));
                prescription.setTicketId(rs.getLong("p.ticket_id"));
                prescription.setStatus(rs.getString("p.status"));

                do {
                    if (rs.getLong("pm.id") != 0) {
                        Medicine medicine = new Medicine();
                        medicine.setId(rs.getLong("m.id"));
                        medicine.setName(rs.getString("m.name"));
                        medicine.setPrice(rs.getLong("m.price"));
                        medicine.setUnit(rs.getString("m.unit"));
                        medicine.setUseness(rs.getString("m.useness"));

                        PrescriptionMedicine prescriptionMedicine = new PrescriptionMedicine();
                        prescriptionMedicine.setId(rs.getLong("pm.id"));
                        prescriptionMedicine.setMedicineId(rs.getLong("pm.medicine_id"));
                        prescriptionMedicine.setPrescriptionId(rs.getLong("pm.prescription_id"));
                        prescriptionMedicine.setQuantity(rs.getDouble("pm.quantity"));
                        prescriptionMedicine.setInstrument(rs.getString("pm.instrument"));
                        prescriptionMedicine.setMedicine(medicine);
                        prescription.getMedicines().add(prescriptionMedicine);
                    }
                } while (rs.next());
                return prescription;
            }
            return null;
        }
    }

    public List<Prescription> getRequiredPrescriptions() throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            PreparedStatement stm = conn.prepareStatement("select p.*, pt.* from prescriptions p left join tickets t on t.id = p.ticket_id left join patients pt on pt.id = t.patient_id where p.status = 'required'");
            ResultSet rs = stm.executeQuery();
            List<Prescription> prescriptions = new ArrayList<>();
            while (rs.next()) {
                Prescription prescription = new Prescription();
                prescription.setId(rs.getLong("p.id"));
                prescription.setTicketId(rs.getLong("p.ticket_id"));
                prescription.setStatus(rs.getString("p.status"));

                Ticket ticket = new Ticket();
                prescription.setTicket(ticket);

                Patient patient = new Patient();
                ticket.setPatient(patient);

                patient.setId(rs.getLong("pt.id"));
                patient.setFirstName(rs.getString("pt.first_name"));
                patient.setLastName(rs.getString("pt.last_name"));
                patient.setMiddleName(rs.getString("pt.middle_name"));

                prescriptions.add(prescription);
            }
            return prescriptions;
        }
    }

    public Prescription getPrescriptionByTicketId(long ticketId) throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            PreparedStatement stm = conn.prepareStatement("select * from prescriptions p left join prescription_medicine pm on pm.prescription_id = p.id left join medicines m on m.id = pm.medicine_id where p.ticket_id = ?");
            stm.setLong(1, ticketId);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                Prescription prescription = new Prescription();
                prescription.setId(rs.getLong("p.id"));
                prescription.setTicketId(rs.getLong("p.ticket_id"));
                prescription.setStatus(rs.getString("p.status"));

                do {
                    if (rs.getLong("pm.id") != 0) {
                        Medicine medicine = new Medicine();
                        medicine.setId(rs.getLong("m.id"));
                        medicine.setName(rs.getString("m.name"));
                        medicine.setPrice(rs.getLong("m.price"));
                        medicine.setUnit(rs.getString("m.unit"));
                        medicine.setUseness(rs.getString("m.useness"));

                        PrescriptionMedicine prescriptionMedicine = new PrescriptionMedicine();
                        prescriptionMedicine.setId(rs.getLong("pm.id"));
                        prescriptionMedicine.setMedicineId(rs.getLong("pm.medicine_id"));
                        prescriptionMedicine.setPrescriptionId(rs.getLong("pm.prescription_id"));
                        prescriptionMedicine.setQuantity(rs.getDouble("pm.quantity"));
                        prescriptionMedicine.setInstrument(rs.getString("pm.instrument"));
                        prescriptionMedicine.setMedicine(medicine);
                        prescription.getMedicines().add(prescriptionMedicine);
                    }
                } while (rs.next());
                return prescription;
            }
            return null;
        }
    }

    public Prescription insertPrescription(long ticketId) throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            PreparedStatement stm = conn.prepareStatement("insert into prescriptions (ticket_id) values (?)", Statement.RETURN_GENERATED_KEYS);
            stm.setLong(1, ticketId);
            stm.executeUpdate();
            ResultSet rs = stm.getGeneratedKeys();
            if (rs.next()) {
                Prescription prescription = new Prescription();
                prescription.setTicketId(ticketId);
                prescription.setId(rs.getLong(1));
                return prescription;
            }
            return null;
        }
    }

    public String updatePrescriptionStatus(String status, long id) throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            PreparedStatement stm = conn.prepareStatement("update prescriptions set status = ? where id = ?");
            stm.setString(1, status);
            stm.setLong(2, id);
            stm.executeUpdate();
            return status;
        }
    }

    public PrescriptionMedicine addPrescriptionMedicine(PrescriptionMedicine prescriptionMedicine) throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            PreparedStatement stm = conn.prepareStatement("insert into prescription_medicine (prescription_id, medicine_id, quantity, instrument) values (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            stm.setLong(1, prescriptionMedicine.getPrescriptionId());
            stm.setLong(2, prescriptionMedicine.getMedicineId());
            stm.setDouble(3, prescriptionMedicine.getQuantity());
            stm.setString(4, prescriptionMedicine.getInstrument());
            stm.executeUpdate();
            ResultSet rs = stm.getGeneratedKeys();
            if (rs.next()) {
                prescriptionMedicine.setId(rs.getLong(1));
                return prescriptionMedicine;
            }
            return null;
        }
    }

    public void removePrescriptionMedicine(long id) throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            PreparedStatement stm = conn.prepareStatement("delete from prescription_medicine where id = ?");
            stm.setLong(1, id);
            stm.executeUpdate();
        }
    }

    public void removePrescription(long id) throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            PreparedStatement stm = conn.prepareStatement("delete from prescriptions where id = ?");
            stm.setLong(1, id);
            stm.executeUpdate();
        }
    }

    public void updateQuantityPrescriptionMedicine(long id, double quantity) throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            PreparedStatement stm  = conn.prepareStatement("update prescription_medicine set quantity = ? where id = ?");
            stm.setDouble(1, quantity);
            stm.setLong(2, id);
            stm.executeUpdate();
        }
    }

    public void updateInstrumentPrescriptionMedicine(long id, String instrument) throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            PreparedStatement stm = conn.prepareStatement("update prescription_medicine set instrument = ? where id = ?");
            stm.setString(1, instrument);
            stm.setLong(2, id);
            stm.executeUpdate();
        }
    }
}
