use clinicmanagement;

# password: TestingAdmin@1234
insert into users (first_name, last_name, middle_name, username, role, password, salt)
values ('ADMIN', '', '', 'sysadmin', 'ADMIN', '39c28bd3f47a115ee7bd3ac2eb7fadf9498971d8191f237fb0bf4313a606cf34', 'b71fd907e39d2888b77a0a2fb1bd0dc0');

# password: Doctor@1234567
insert into users (first_name, last_name, middle_name, username, role, password, salt)
values ('DOCTOR', '', '', 'doctor', 'DOCTOR', 'df291a29c36d45f69fb384a8534109ab45d086fec81289b3860a5b803cd46837', '51abbb963ced29ede2a46b19b0130cd4');

# password: Pharmacist@12345678
insert into users (first_name, last_name, middle_name, username, role, password, salt)
values ('PHARMACIST', '', '', 'pharmacist', 'PHARMACIST', '6c06353616077f856a33b08a5bada07fc7a6ff9d97e857c7cb7fc982b776a159', '7999e2077e63e8b05d345ae17957a8c1');
