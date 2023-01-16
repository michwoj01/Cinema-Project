delete
from schedule
where id > 0;

alter table schedule
    add NR_OF_SEATS INT NOT NULL;

insert into schedule(nr_of_seats, currently_available, cinema_hall_id, movie_id, start_date)
values (20, 20, 1, 1, to_date('2023-08-06', 'YYYY-MM-DD'));

insert into schedule(nr_of_seats, currently_available, cinema_hall_id, movie_id, start_date)
values (30, 30, 2, 2, to_date('2023-07-06', 'YYYY-MM-DD'));

insert into schedule(nr_of_seats, currently_available, cinema_hall_id, movie_id, start_date)
values (40, 40, 3, 3, to_date('2023-06-06', 'YYYY-MM-DD'));
