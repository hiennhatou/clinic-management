use clinicmanagement;
alter table appointments drop column period_time;
alter table appointments drop column symptom;
alter table appointments add constraint unique IDX_PATIENT_ID_DATE_APPOINTMENT (patient_id, appointment_date);
alter table tickets add column appointment_id bigint unique null;
alter table tickets add constraint FK_APPOINTMENT_ID_TICKET foreign key (appointment_id) references appointments(id) on delete set null ;