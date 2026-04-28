CREATE TABLE Workers (
    id serial primary key,
    firstName varchar(100) not null ,
    lastName varchar(100) not null ,
    rol varchar(8) not null check ( rol in ('Manager', 'Host', 'Waiter') ),
    email varchar(100) not null ,
    password varchar(100) not null
);

CREATE TABLE RestaurantTable (
    id serial primary key,
    status varchar(10) not null check ( status in ('Available', 'Seated', 'Reserved') ) DEFAULT 'Available',
    maxSitting int not null
);

CREATE TABLE MenuItems (
    id serial primary key,
    name varchar(100) not null,
    type varchar(100) not null check ( type in ('Starter', 'Main', 'Dessert', 'Beverage', 'Alcoholic beverage') ),
    price decimal not null,
    isVegetarian boolean not null
);

CREATE TABLE Allergies (
    id serial primary key,
    name varchar(100) not null
);

CREATE TABLE MenuItemsAllergies (
    id serial primary key,
    menuItemId int not null,
    allergyId int not null
);

CREATE TABLE Reservations (
    id serial primary key,
    customer varchar(100) not null,
    date timestamp not null,
    partySize int,
    tableId int not null
);

CREATE TABLE TableOrders (
    id serial primary key,
    tableId int not null,
    waiterId int not null,
    notes varchar(100),
    bill decimal,
    isReservation boolean DEFAULT false,
    isPaid boolean DEFAULT false
);

CREATE TABLE Payments (
    id serial primary key,
    amount decimal not null,
    method varchar(100) not null check ( method in ('Card', 'Cash') ),
    orderId int not null
);

