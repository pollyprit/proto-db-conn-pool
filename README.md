## Database connection pool
Database connection pool for not to overload the database with too many connections. Connections are kept hot, improving the performance.
Pool is implemented with a blocking queue. It could also be made elastic to increase or decrease the connections based on load.
