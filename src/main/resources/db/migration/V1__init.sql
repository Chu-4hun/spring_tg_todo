create table users(
    id bigserial primary key
);

create table tasks(
    id bigserial primary key,
    is_done bool,
    text text,

    user_Id bigint references users(id)
);
