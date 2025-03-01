import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// Use a Database connection pool for not to overload the database with too many connections. 
// Also the connections are kept hot, improving the performance. It could also be made elastic to 
// increase or decrease the connections based on load.
public class Main {
    private static String url = System.getenv("IMDB_DB_URL");
    private static String user = System.getenv("IMDB_DB_USERNAME");
    private static String password = System.getenv("IMDB_DB_PASSWORD");
    private static int poolSize = 5;

    public static void main(String[] args) throws InterruptedException, SQLException, ClassNotFoundException {
        DBConnectionPool pool = new DBConnectionPool(url, user, password, poolSize);
        Executor[] executors;
        String query = "SELECT * FROM MOVIES limit 1 offset ";

        for (int i = 100; i < 150; ++i) {
            Thread t = new Thread(new Executor(query + i, i, pool));
            t.start();
        }
    }

    static class Executor extends Thread {
        private String query;
        private int id;
        private DBConnectionPool pool;

        Executor(String query, int id, DBConnectionPool pool) {
            this.query = query;
            this.id = id;
            this.pool = pool;
        }

        @Override
        public void run() {
            String out = id + ") " + query + ": ";
            Connection conn = pool.getConnection();

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            try {
                Statement statement = conn.createStatement();
                ResultSet resultSet =  statement.executeQuery(query);
                while (resultSet.next()) {
                    out += resultSet.getString("name") + " ";
                    out += resultSet.getInt("year") ;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            System.out.println(out);
            pool.returnConnection(conn);
        }
    }
}