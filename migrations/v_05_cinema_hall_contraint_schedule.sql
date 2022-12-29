alter table schedule drop constraint schedule_cinema_hall_fk;

insert into schedule(currently_available, cinema_hall_id, movie_id, start_date)
values (10, 1, (select m.id from movie m limit 1), to_date('2023-06-06', 'YYYY-MM-DD'))
