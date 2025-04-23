create database clinicmanagement charset utf8mb4 collate utf8mb4_vietnamese_ci;

use clinicmanagement;

create table users
(
    id          bigint auto_increment
        primary key,
    first_name  varchar(80) charset utf8mb3                     not null,
    last_name   varchar(80) charset utf8mb3                     not null,
    middle_name varchar(100) charset utf8mb3                    not null,
    username    varchar(80) unique                              not null,
    role        enum ('ADMIN', 'DOCTOR', 'PHARMACIST', 'STAFF') not null,
    password    varchar(255) charset utf8mb3                    not null,
    salt        varchar(255) charset utf8mb3                    not null
);

create table patients
(
    id          bigint auto_increment
        primary key,
    birthday    date                               not null,
    id_code     varchar(12) charset utf8mb3 unique not null,
    first_name  varchar(300) charset utf8mb3       null,
    last_name   varchar(300) charset utf8mb3       null,
    middle_name varchar(300) charset utf8mb3       null
);

create table appointments
(
    id               bigint auto_increment
        primary key,
    patient_id       bigint                    not null,
    period_time      enum ('1', '2', '3', '4') not null,
    appointment_date date                      not null,
    symptom          text                      null,
    isCheckin        tinyint(1) default 0      null,
    constraint FK_APPOINTMENT_PATIENT_ID
        foreign key (patient_id) references patients (id)
);

create table tickets
(
    id         int auto_increment
        primary key,
    patient_id bigint                                                           not null,
    doctor_id  bigint                                                           not null,
    created_on datetime                               default CURRENT_TIMESTAMP null,
    updated_on datetime                               default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    status     enum ('created', 'checked_in', 'done') default 'created'         not null,
    constraint FK_TICKET_PATIENT_ID
        foreign key (patient_id) references patients (id),
    constraint FK_TICKET_DOCTOR_ID
        foreign key (doctor_id) references users (id)
);

create table medical_records
(
    id                    bigint auto_increment
        primary key,
    patient_id            bigint   not null,
    symptom               text     not null,
    conclusion            text     not null,
    treatment_instruction text     null,
    examination_date      datetime null,
    ticket_id             int      null,
    constraint FK_MEDICAL_RECORD_TICKET_ID
        foreign key (ticket_id) references tickets (id),
    constraint FK_MEDICAL_RECORD_PATIENT_ID
        foreign key (patient_id) references patients (id)
);

create table ingredients
(
    id   bigint auto_increment        not null
        primary key,
    name varchar(255) charset utf8mb3 not null
);

create table medicines
(
    id      bigint auto_increment
        primary key,
    name    varchar(255) charset utf8mb3 not null,
    price   bigint                       not null,
    unit    varchar(80) charset utf8mb3  null,
    useness text                         null
);

create table medicine_ingredient
(
    id            bigint auto_increment
        primary key,
    medicine_id   bigint      not null,
    ingredient_id bigint      not null,
    quantity      double      null,
    unit          varchar(80) null,
    constraint UNIQUE_IDX_MEDICINE_ID_INGREDIENT_ID
        unique (medicine_id, ingredient_id),
    constraint FK_MEDICINE_INGREDIENT_INGREDIENT_ID
        foreign key (ingredient_id) references ingredients (id) on delete cascade,
    constraint FK_MEDICINE_INGREDIENT_MEDICINE_ID
        foreign key (medicine_id) references medicines (id) on delete cascade
);

create table allergic_ingredients
(
    id            bigint auto_increment
        primary key,
    patient_id    bigint not null,
    ingredient_id bigint not null,
    constraint UNIQUE_IDX_ALLERGIC_INGREDIENT_IDX_PATIENT_ID_INGREDIENT_ID
        unique (patient_id, ingredient_id),
    constraint FK_ALLERGIC_INGREDIENT_PATIENT_ID
        foreign key (patient_id) references patients (id),
    constraint FK_ALLERGIC_INGREDIENT_INGREDIENTS
        foreign key (ingredient_id) references ingredients (id)
);

create table prescriptions
(
    id          bigint auto_increment
        primary key,
    ticket_id   int        not null,
    is_provided tinyint(1) not null,
    constraint FK_PRESCRIPTION_TICKET_ID
        foreign key (ticket_id) references tickets (id)
);

create table prescription_medicine
(
    id              bigint auto_increment
        primary key,
    prescription_id bigint not null,
    medicine_id     bigint not null,
    quantity        double not null,
    instrument      text   not null,
    constraint UNIQUE_INX_PRESCRIPTION_MEDICINE_PRESCRIPTION_ID_MEDICINE_ID
        unique (prescription_id, medicine_id),
    constraint FK_PRESCRIPTION_MEDICINE_MEDICINE_ID
        foreign key (medicine_id) references medicines (id),
    constraint FK_PRESCRIPTION_MEDICINE_PRESCRIPTION_ID
        foreign key (prescription_id) references prescriptions (id)
);
