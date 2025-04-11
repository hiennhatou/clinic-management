create database clinicmanagement charset utf8mb4 collate utf8mb4_vietnamese_ci;

use clinicmanagement;

CREATE TABLE medicine_ingredient
(
    id            bigint auto_increment NOT NULL PRIMARY KEY,
    medicine_id   bigint                NOT NULL,
    ingredient_id bigint                NOT NULL,
    quantity      double,
    unit          varchar(10)
);

CREATE UNIQUE INDEX index_1 ON medicine_ingredient (medicine_id, ingredient_id);

CREATE TABLE users
(
    id          bigint auto_increment                             NOT NULL PRIMARY KEY,
    first_name  nvarchar(80)                                      NOT NULL,
    last_name   nvarchar(80)                                      NOT NULL,
    middle_name nvarchar(100)                                     NOT NULL,
    username    varchar(80)                                       NOT NULL UNIQUE,
    role        enum ('ADMIN', 'DOCTOR', 'PHARMACIST', 'PATIENT') NOT NULL,
    password    nvarchar(255)                                     NOT NULL,
    salt        nvarchar(255)                                     NOT NULL
);

CREATE TABLE patients
(
    id       bigint       NOT NULL PRIMARY KEY,
    birthday date         NOT NULL,
    id_code  nvarchar(12) NOT NULL UNIQUE
);

CREATE INDEX index_1 ON patients (id, id_code);

CREATE TABLE allergic_ingredients
(
    id            bigint auto_increment NOT NULL PRIMARY KEY,
    patient_id    bigint                NOT NULL,
    ingredient_id bigint                NOT NULL
);

CREATE UNIQUE INDEX unique_patient_ingredient ON allergic_ingredients (patient_id, ingredient_id);

CREATE TABLE prescriptions
(
    id                bigint auto_increment NOT NULL PRIMARY KEY,
    medical_record_id bigint                NOT NULL,
    is_provided       boolean               NOT NULL
);

CREATE INDEX index_1 ON prescriptions (medical_record_id);

CREATE TABLE medicines
(
    id      bigint auto_increment NOT NULL PRIMARY KEY,
    name    nvarchar(255)         NOT NULL,
    price   bigint                NOT NULL,
    unit    nvarchar(80),
    useness text
);


CREATE TABLE medical_records
(
    id                    bigint auto_increment NOT NULL PRIMARY KEY,
    patient_id            bigint                NOT NULL,
    symptom               text                  NOT NULL,
    conclusion            text                  NOT NULL,
    treatment_instruction text,
    examination_date      datetime
);

CREATE INDEX index_1 ON medical_records (patient_id);

CREATE TABLE prescription_medicine
(
    id              bigint auto_increment NOT NULL PRIMARY KEY,
    prescription_id bigint                NOT NULL,
    medicine_id     bigint                NOT NULL,
    quantity        double                NOT NULL,
    instrument      text                  NOT NULL
);

CREATE UNIQUE INDEX index_1 ON prescription_medicine (prescription_id, medicine_id);

CREATE TABLE appointments
(
    id               bigint auto_increment     NOT NULL PRIMARY KEY,
    patient_id       bigint                    NOT NULL,
    period_time      enum ('1', '2', '3', '4') NOT NULL,
    appointment_date date                      NOT NULL,
    symptom          text,
    isMeet           boolean default false
);

CREATE INDEX index_1 ON appointments (patient_id);
CREATE INDEX index_2 ON appointments (appointment_date, period_time);

CREATE TABLE ingredients
(
    id   bigint        NOT NULL PRIMARY KEY,
    name nvarchar(255) NOT NULL
);


ALTER TABLE appointments
    ADD CONSTRAINT appointments_patient_id_fk FOREIGN KEY (patient_id) REFERENCES patients (id);
ALTER TABLE medical_records
    ADD CONSTRAINT medical_records_patient_id_fk FOREIGN KEY (patient_id) REFERENCES patients (id);
ALTER TABLE medicine_ingredient
    ADD CONSTRAINT medicine_ingredient_ingredient_id_fk FOREIGN KEY (ingredient_id) REFERENCES ingredients (id);
ALTER TABLE medicine_ingredient
    ADD CONSTRAINT medicine_ingredient_medicine_id_fk FOREIGN KEY (medicine_id) REFERENCES medicines (id);
ALTER TABLE prescription_medicine
    ADD CONSTRAINT medicines_id_fk FOREIGN KEY (medicine_id) REFERENCES medicines (id);
ALTER TABLE prescription_medicine
    ADD CONSTRAINT prescription_medicine_prescription_id_fk FOREIGN KEY (prescription_id) REFERENCES prescriptions (id);
ALTER TABLE prescriptions
    ADD CONSTRAINT prescriptions_medical_record_id_fk FOREIGN KEY (medical_record_id) REFERENCES medical_records (id);
ALTER TABLE patients
    ADD CONSTRAINT user_id_fk FOREIGN KEY (id) REFERENCES users (id) ON DELETE CASCADE;
ALTER TABLE allergic_ingredients
    ADD CONSTRAINT patient_id_fk FOREIGN KEY (patient_id) REFERENCES patients (id);
ALTER TABLE allergic_ingredients
    ADD CONSTRAINT ingredient_id_fk FOREIGN KEY (ingredient_id) REFERENCES ingredients (id);
