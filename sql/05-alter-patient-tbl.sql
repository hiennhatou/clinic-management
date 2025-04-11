use clinicmanagement;

alter table patients drop constraint user_id_fk;
alter table patients add column first_name nvarchar(300);
alter table patients add column last_name nvarchar(300);
alter table patients add column middle_name nvarchar(300);

alter table users change column role role enum ('ADMIN', 'DOCTOR', 'PHARMACIST', 'STAFF') not null;
