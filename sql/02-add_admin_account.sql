use clinicmanagement;

### password: Admin@1234
insert into users (first_name, last_name, middle_name, username, role, password, salt)
values ('ADMIN', '', '', 'sysadmin', 'ADMIN', 'd77664ed097344f9526eedd6a8a359f1ee06638bf76083fded0bdb04b92f4e9d', '17675fe49a8d997b1c50f557f270ff7d');

### password: Hello1234@
insert into users (first_name, last_name, middle_name, username, role, password, salt)
values ('DOCTOR', '', '', 'doctor', 'DOCTOR', 'df291a29c36d45f69fb384a8534109ab45d086fec81289b3860a5b803cd46837', '51abbb963ced29ede2a46b19b0130cd4');

### password: Pharmacist@1234
insert into users (first_name, last_name, middle_name, username, role, password, salt)
values ('PHARMACIST', '', '', 'pharmacist', 'PHARMACIST', '29540006ce3716badb315753a8add073fa998dc128be1fb0afb586113af92e26', '0774a59967ccfa45a0bb4a04785d6acb');
