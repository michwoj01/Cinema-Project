create or replace function get_all_months_between(start_date timestamp without time zone,
                                                  end_date timestamp without time zone)
    returns TABLE
            (
                month       text,
                year        text,
                month_value double precision,
                year_value  double precision
            )
    language plpgsql
as
$$
declare
begin
    return query (WITH RECURSIVE date_range AS
                                     (select (to_char(start_date, 'MM'))     as month_,
                                             (to_char(start_date, 'YYYY'))   as year_,
                                             extract(month from start_date)  as month_value,
                                             extract(year from start_date)   as year_value,
                                             start_date + INTERVAL '1 MONTH' as next_month_date
                                      UNION
                                      SELECT (to_char(dr.next_month_date, 'MM'))     as month_,
                                             (to_char(dr.next_month_date, 'YYYY'))   as year_,
                                             extract(month from dr.next_month_date)  as month_value,
                                             extract(year from dr.next_month_date)   as year_value,
                                             dr.next_month_date + INTERVAL '1 MONTH' as next_month_date
                                      FROM date_range dr
                                      where next_month_date <= end_date)
                  select drm.month_, drm.year_, drm.month_value, drm.year_value
                  from date_range drm
                  order by drm.next_month_date asc);
end
$$;

create table ticket
(
    id          serial primary key,
    seat_nr     int not null,
    schedule_id int not null,
    foreign key (schedule_id) references schedule (id)
);


drop function if exists get_sales_between;
create or replace function get_sales_between(start_date timestamp without time zone, end_date timestamp without time zone)
    returns TABLE
            (
                no_of_tickets bigint,
                month_value double precision,
                year_value  double precision
            )
    language plpgsql
as
$$
declare
begin
    return query
        select count(t.id) as tickerNumber, months.month_value, months.year_value
        from get_all_months_between(start_date, end_date) months
                 left join schedule sch on EXTRACT(month from sch.start_date) = months.month_value and
                                           extract(year from sch.start_date) = months.year_value
                 left join ticket t on sch.id = t.schedule_id
        group by months.month_value, months.year_value
        order by months.month_value, months.year_value;
end;
$$;


select * from get_sales_between('2019-01-01', '2019-12-31');