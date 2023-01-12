delete
from schedule
where id > 0;

alter table schedule
    add NR_OF_SEATS INT NOT NULL;

insert into schedule(nr_of_seats, currently_available, cinema_hall_id, movie_id, start_date)
values (20, 10, 1, (select m.id from movie m limit 1), to_date('2023-06-06', 'YYYY-MM-DD'))
