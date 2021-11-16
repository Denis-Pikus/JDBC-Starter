package dmdev.jdbc.starter.runner;

import dmdev.jdbc.starter.util.ConnectionManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * Batch создается для передачи запросов каскадом для экономии ресурсов, но обычно это
 * запросы на создание таблиц и заполнение их контентом
 */
public class BatchRunner {
    public static void main(String[] args) throws SQLException {
        long flightId = 8;
        var deleteFlightSql = "DELETE FROM flight WHERE id = " + flightId;
        var deleteTicketSql = "DELETE FROM ticket WHERE flight_id = " + flightId;

        Connection connection = null;
        Statement statement = null;
        try {
            connection = ConnectionManager.open();
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            statement.addBatch(deleteTicketSql);
            statement.addBatch(deleteFlightSql);
            var batch = statement.executeBatch();
            connection.commit();
        }catch (Exception e){
            if (connection != null){
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null){
                connection.close();
            }
            if (statement != null){
                statement.close();
            }
        }

    }
}
