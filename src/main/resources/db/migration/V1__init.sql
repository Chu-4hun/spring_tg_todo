create table users
(
    id      bigserial primary key,
    chat_id int unique
);
create table tasks(
    id bigserial primary key,
    is_done bool,
    text text,

    user_Id bigint references users(chat_id)
);
