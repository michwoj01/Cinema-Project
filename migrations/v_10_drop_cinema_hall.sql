DROP TABLE CINEMA_HALL;

ALTER TABLE ticket
    add COLUMN date_of_purchase TIMESTAMP NOT NULL default now();

insert into permission(name, should_be_displayed)
values ('send_reports', true);

insert into permission_role(permission_id, role_id)
values (7, 1);

insert into ticket (seat_nr, schedule_id, date_of_purchase)
values (17, 1, to_timestamp('2023-08-06', 'YYYY-MM-dd'));

insert into ticket (seat_nr, schedule_id, date_of_purchase)
values (18, 1, to_timestamp('2023-08-06', 'YYYY-MM-dd'));

insert into ticket (seat_nr, schedule_id, date_of_purchase)
values (16, 1, to_timestamp('2023-08-06', 'YYYY-MM-dd'));

insert into ticket (seat_nr, schedule_id, date_of_purchase)
values (19, 1, to_timestamp('2023-08-05', 'YYYY-MM-dd'));

insert into ticket (seat_nr, schedule_id, date_of_purchase)
values (22, 1, to_timestamp('2023-08-01', 'YYYY-MM-dd'));
insert into ticket (seat_nr, schedule_id, date_of_purchase)
values (23, 1, to_timestamp('2023-08-01', 'YYYY-MM-dd'));

insert into ticket (seat_nr, schedule_id, date_of_purchase)
values (8, 1, to_timestamp('2023-08-10', 'YYYY-MM-dd'));
insert into ticket (seat_nr, schedule_id, date_of_purchase)
values (9, 1, to_timestamp('2023-08-10', 'YYYY-MM-dd'));
insert into ticket (seat_nr, schedule_id, date_of_purchase)
values (10, 1, to_timestamp('2023-08-10', 'YYYY-MM-dd'));

insert into ticket (seat_nr, schedule_id, date_of_purchase)
values (5, 1, to_timestamp('2023-08-07', 'YYYY-MM-dd'));
insert into ticket (seat_nr, schedule_id, date_of_purchase)
values (3, 1, to_timestamp('2023-08-07', 'YYYY-MM-dd'));
insert into ticket (seat_nr, schedule_id, date_of_purchase)
values (4, 1, to_timestamp('2023-08-07', 'YYYY-MM-dd'));

insert into ticket (seat_nr, schedule_id, date_of_purchase)
values (2, 1, to_timestamp('2023-08-09', 'YYYY-MM-dd'));
