# adentis


This is a test program.

## How to run

This project uses [sbt-pack](https://github.com/xerial/sbt-pack) plugin, in order to run this application you need create a runnable script.


In the terminal just type:

```
sbt pack
```

To run as the challenge asks:

```
./target/bin/orders "2020-01-01 00:00:00" "2022-12-31 23:59:59"
```

## Important!

This application requires a PosgresSQL instance running, to keep it simple there is a [docker-compose](https://docs.docker.com/compose/) already configured to provide it.

In the terminal just type:

```
docker-compose up
```

This application will populate the database with products and orders that took place between 2021 and 2022. It is worth 
to know these data were randomly generate just for testing purpose, there is no guarantee of consistency.


## Bonus

To define a different a list of intervals to print just use a parameter `--report`, bellow there are some examples:

```
./target/bin/orders --report "1-3 4-8 >8" "2020-01-01 00:00:00" "2022-12-31 23:59:59"

./target/bin/orders --report "1-3 4-6 7-9 >10" "2020-01-01 00:00:00" "2022-12-31 23:59:59"

```