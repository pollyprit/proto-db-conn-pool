import java.sql.Connection;
import java.sql.SQLException;

public class DBConnectionPool {
    private int poolSize = 10;
    private String url;
    private String user;
    private String password;

    private MyBlockingQueue<Connection> pool;

    DBConnectionPool(String url, String user, String password, int capacity) throws SQLException, ClassNotFoundException {
        this.url = url;
        this.user = user;
        this.password = password;
        this.poolSize = capacity;

        pool = new MyBlockingQueue<Connection>(this.poolSize);
        Class.forName("com.mysql.jdbc.Driver");

        for (int i = 0; i < this.poolSize; ++i)
            pool.add(DBConnectionFactory.createConnection(this.url, this.user, this.password));
    }

    Connection getConnection() {
        return pool.get();
    }

    void returnConnection(Connection connection) {
        pool.add(connection);
    }

    void destroyPool() throws SQLException {
        while (pool.size() > 0) {
            Connection conn = pool.get();
            conn.close();
        }
    }
}
