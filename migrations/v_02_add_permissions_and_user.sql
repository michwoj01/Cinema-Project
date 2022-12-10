insert into permission(name) values ('modify_users'), ('schedule_movies'), ('modify_schedule_watchers');
insert into role(name, description) values ('admin', 'Administrator'), ('kasjer', 'Sprzedaje bilety'), ('moderator', 'ustala harmonogram seansów');
insert into permission_role(role_id, permission_id) values (1, 1), (1, 2), (1, 3), (2, 2), (2, 3), (3, 3);

insert into login_user(first_name, last_name, email, role_id) values ('jan', 'kowalski', 'admin@admin.pl', 1);