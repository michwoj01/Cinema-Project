ALTER TABLE role
    ALTER id add generated always as identity;

ALTER TABLE permission
    ALTER id add generated always as identity;
ALTER TABLE cinema_hall
    ALTER id add generated always as identity;
ALTER TABLE login_user
    ALTER id add generated always as identity;
ALTER TABLE schedule
    ALTER id add generated always as identity;

alter table role
drop column user_id;