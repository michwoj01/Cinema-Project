ALTER TABLE NOTIFICATIONS
    RENAME TO NOTIFICATION;

create table RECOMMENDATION
(
    ID       INT NOT NULL generated always as identity,
    MOVIE_ID INT NOT NULL,
    FOREIGN KEY (MOVIE_ID) REFERENCES MOVIE (ID),
    CONSTRAINT MOVIE_RECOMENDATION_PK PRIMARY KEY (ID),
    UNIQUE (MOVIE_ID)
);


DO
$$
    declare
        permissionId   int;
        declare roleId int;
    BEGIN
        insert into permission(name) values ('RECOMMENDATION') returning id into permissionId;
        select id from role where name = 'admin' into roleId;
        insert into permission_role(permission_id, role_id) values (permissionId, roleId);
    END
$$;

DO
$$
    declare
        permissionId   int;
        declare roleId int;
    BEGIN
        insert into permission(name) values ('edit_notification_message') returning id into permissionId;
        select id from role where name = 'admin' into roleId;
        insert into permission_role(permission_id, role_id) values (permissionId, roleId);
    END
$$;

alter table permission
    add column should_be_displayed boolean default true;

update permission
set should_be_displayed = false
where name in ('RECOMMENDATION');

update notification
set message = '<!DOCTYPE html>
<html>
<head>
<style>
table {
  font-family: arial, sans-serif;
border-collapse: collapse;
width: 100%;
}

td, th {
  border: 1px solid #dddddd;
text-align: left;
padding: 8px;
}

tr:nth-child(even) {
  background-color: #dddddd;
}
</style>
</head>
<body>
<h2>New and improved list of recommended movies</h2>
<table>
  <tr>
    <th>Name</th>
    <th>Poster image</th>
    <th>Description</th>
  </tr>
   ||##||
</table>

</body>
</html>'
where id = 1;


create or replace function recomended_movies_func() returns text
AS
$$
declare
    result text;
begin
    select REPLACE(n.message, '||##||', (select string_agg('<tr><td>' || m.name || '</td><td><img src="' ||
                                                           m.cover_url || '"></td><td>' || m.description ||
                                                           '</td></tr>', '<br>')
                                         from RECOMMENDATION r
                                                  inner join movie m on m.id = r.movie_id))
    into result
    from notification n
    where n.name = 'Cashier Notification';
    return result;
end
$$ LANGUAGE plpgsql;

insert into RECOMMENDATION (MOVIE_ID)
select id from movie limit 5;