CREATE TABLE orders(
    id varchar(255) not null primary key,
    created_at timestamp not null
);

CREATE TABLE products(
    id varchar(255) not null primary key,
    name varchar(255) not null,
    created_at timestamp not null
);

CREATE TABLE items(
    id varchar(255) not null primary key,
    price double precision not null,
    quantity integer not null,
    order_id varchar(255) not null references orders(id),
    product_id varchar(255) not null references products(id)
);

CREATE INDEX ON orders(created_at);