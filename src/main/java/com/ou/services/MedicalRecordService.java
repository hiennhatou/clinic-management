package com.ou.services;

import com.ou.pojos.DBUtils;
import com.ou.pojos.MedicalRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicalRecordService {
    public MedicalRecord addMedicalRecord(MedicalRecord medicalRecord) throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            PreparedStatement stm = conn.prepareStatement("insert into medical_records (patient_id, ticket_id, symptom, conclusion, treatment_instruction) values (?, ?, ?, ?, ?) on duplicate key update symptom = ?, conclusion = ?, treatment_instruction = ?", Statement.RETURN_GENERATED_KEYS);
            stm.setLong(1, medicalRecord.getPatientId());
            stm.setLong(2, medicalRecord.getTicketId());
            stm.setString(3, medicalRecord.getSymptom());
            stm.setString(4, medicalRecord.getConclusion());
            stm.setString(5, medicalRecord.getTreatmentInstruction());
            stm.setString(6, medicalRecord.getSymptom());
            stm.setString(7, medicalRecord.getConclusion());
            stm.setString(8, medicalRecord.getTreatmentInstruction());
            stm.executeUpdate();
            ResultSet rs = stm.getGeneratedKeys();
            if (rs.next()) {
                medicalRecord.setId(rs.getLong(1));
                return medicalRecord;
            }
            return null;
        }
    }

    public MedicalRecord getMedicalRecordByTicketId(long ticketId) throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            PreparedStatement stm = conn.prepareStatement("select * from medical_records where ticket_id = ?");
            stm.setLong(1, ticketId);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                MedicalRecord medicalRecord = new MedicalRecord();
                medicalRecord.setId(rs.getLong("id"));
                medicalRecord.setPatientId(rs.getLong("patient_id"));
                medicalRecord.setTicketId(rs.getLong("ticket_id"));
                medicalRecord.setSymptom(rs.getString("symptom"));
                medicalRecord.setConclusion(rs.getString("conclusion"));
                medicalRecord.setTreatmentInstruction(rs.getString("treatment_instruction"));
                medicalRecord.setExaminationDate(rs.getTimestamp("examination_date").toLocalDateTime());
                return medicalRecord;
            }
            return null;
        }
    }

    public List<MedicalRecord> getMedicalRecordByPatientId(long patientId, long exceptTicketId) throws SQLException {
        try(Connection conn = DBUtils.getConnection()) {
            PreparedStatement stm = conn.prepareStatement("select * from medical_records where patient_id = ? and ticket_id <> ?");
            stm.setLong(1, patientId);
            stm.setLong(2, exceptTicketId);
            ResultSet rs = stm.executeQuery();
            List<MedicalRecord> medicalRecords = new ArrayList<>();
            while (rs.next()) {
                MedicalRecord medicalRecord = new MedicalRecord();
                medicalRecord.setId(rs.getLong("id"));
                medicalRecord.setPatientId(rs.getLong("patient_id"));
                medicalRecord.setTicketId(rs.getLong("ticket_id"));
                medicalRecord.setSymptom(rs.getString("symptom"));
                medicalRecord.setConclusion(rs.getString("conclusion"));
                medicalRecord.setTreatmentInstruction(rs.getString("treatment_instruction"));
                medicalRecord.setExaminationDate(rs.getTimestamp("examination_date").toLocalDateTime());
                medicalRecords.add(medicalRecord);
            }
            return medicalRecords;
        }
    }
}
