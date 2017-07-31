# DBMS
#### Relational Database Management System
|[About](https://github.com/Nickdill/DBMS#about)|[Usage](https://github.com/Nickdill/DBMS#usage)|[Testing](https://github.com/Nickdill/DBMS#testing)|
|---|---|---|
This program is written in Java, and basically allows the user to create and store a database of tables, written as <file name>.db files.

## Usage

#### Installation
This section coming soon, as this program was build on OSX and has yet to be tried on other platforms.

#### Commands
This program operates on a basic query language.

Load a table from a .db file.

    load <table name>;
Store a table to a .db file.

    store <table name>;
Create a new table in the database.

    create table <table name> ( <column name>,+ );
Create a new table in the database from loaded tables.

    create table <table name> as select <column names> from <table names> where <conditions>;
Select rows from an existing table.

    select <column names> from <table names> where <conditions>;
Insert rows into a table.

    insert into <table names> values <column values>;
Print a table.

    print <table name>;


## Testing

#### Makefile
Coming Soon.

#### README Progress

- [ ] Installation
- [x] Commands
- [ ] Makefile
- [x] Completed

## Goodbye
Thanks for checking out my project, and have an awesome day. You deserve it.

Cheers!

==========

Check out my [website](https://nicholasdill.com) if you've got a minute to spare!
