package com.ou.pojos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Prescription {
    private long id;
    private long ticketId;
    private String status = "created";

    private List<PrescriptionMedicine> medicines = new ArrayList<>();
    private Ticket ticket;

    public static String getStatusReadable(String status) {
        return switch (status) {
            case "created" -> "Đang chính sửa đơn thuốc";
            case "required" -> "Đang yêu cầu đơn thuốc";
            case "provided" -> "Đã cung cấp";
            default -> "Không có đơn thuốc";
        };
    }
}
