alter table login_user
    add constraint login_user_pk
        unique (email);
