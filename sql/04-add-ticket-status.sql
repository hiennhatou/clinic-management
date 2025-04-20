use clinicmanagement;

alter table tickets add column status enum('created', 'checked_in', 'done') default 'created' not null ;
