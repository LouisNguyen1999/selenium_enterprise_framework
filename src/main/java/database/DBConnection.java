package database;

public class DBConnection {
    public String getConnectionString() {
        return "jdbc:h2:mem:testdb";
    }
}
