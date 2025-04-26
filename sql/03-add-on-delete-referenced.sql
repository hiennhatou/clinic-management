use clinicmanagement;

alter table prescriptions drop constraint FK_PRESCRIPTION_TICKET_ID;
alter table prescriptions add constraint FK_PRESCRIPTION_TICKET_ID foreign key (ticket_id) references tickets (id) on delete cascade ;

alter table prescription_medicine drop constraint FK_PRESCRIPTION_MEDICINE_MEDICINE_ID;
alter table prescription_medicine add constraint FK_PRESCRIPTION_MEDICINE_MEDICINE_ID foreign key (medicine_id) references medicines (id) on delete cascade ;

alter table prescription_medicine drop constraint FK_PRESCRIPTION_MEDICINE_PRESCRIPTION_ID;
alter table prescription_medicine add constraint FK_PRESCRIPTION_MEDICINE_PRESCRIPTION_ID foreign key (prescription_id) references prescriptions (id) on delete cascade ;
