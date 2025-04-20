use clinicmanagement;

alter table tickets add column doctor_id bigint not null;
alter table tickets add foreign key (doctor_id) references users(id);
