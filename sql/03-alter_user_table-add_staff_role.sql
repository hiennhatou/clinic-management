use clinicmanagement;

alter table users change column role role enum ('ADMIN', 'DOCTOR', 'PHARMACIST', 'PATIENT', 'STAFF') not null;

# Password: Staff@1234
insert into users (first_name, last_name, middle_name, username, role, password, salt)
values ('STAFF', '', '', 'staff', 'STAFF', '012a85294087767d4b1189663ef6a3f946ae1e3552cf24feb2ea6c15fa1f46c8', 'eb7190ba10562cc4df89db40325d7ccd');

