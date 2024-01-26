# adentis


This is a test program.

## How to run

This project is using the sbt plugin called `sbt-pack`, in order to run thi application you need create a runnable script.


In the terminal just type:

```
sbt pack
```

To run as the challenge ask:

```
./target/bin/orders "2020-01-01 00:00:00" "2022-12-31 23:59:59"
```

## Important!

This application requires a PosgresSQL instance running, to keep it simple there is a docker-compose already configured to provide it.

In the terminal just type:

```
docker-compose up
```

This application will populate the database with products and orders that took place between 2021 and 2022.

## Bonus

To define a different a list of intervals to print just use a parameter `--report`, bellow there are some examples:

```
./target/bin/orders --report "1-3 3-7 >8" "2020-01-01 00:00:00" "2022-12-31 23:59:59"

./target/bin/orders --report "1-3 3-6 7-9 >15" "2020-01-01 00:00:00" "2022-12-31 23:59:59"

```