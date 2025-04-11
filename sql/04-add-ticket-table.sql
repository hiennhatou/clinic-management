use clinicmanagement;

create table tickets (
    id int primary key auto_increment,
    patient_id bigint not null,
    created_on datetime default current_timestamp(),
    updated_on datetime default current_timestamp() on update current_timestamp(),
    foreign key (patient_id) references patients(id)
);


alter table medical_records add column ticket_id int;
alter table medical_records add foreign key (ticket_id) references tickets(id);