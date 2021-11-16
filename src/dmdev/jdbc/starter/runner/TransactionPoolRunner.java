package dmdev.jdbc.starter.runner;

import dmdev.jdbc.starter.util.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TransactionPoolRunner {
    public static void main(String[] args) throws SQLException {
        var deleteFlightSql = "DELETE FROM flight WHERE id = ?";
        var deleteTicketSql = "DELETE FROM ticket WHERE flight_id = ?";
        long flightId = 8;
        Connection connection = null;
        PreparedStatement deleteFlightStatement = null;
        PreparedStatement deleteTicketsStatement = null;

        try {
            connection = ConnectionPool.get();
            deleteFlightStatement = connection.prepareStatement(deleteFlightSql);
            deleteTicketsStatement = connection.prepareStatement(deleteTicketSql);

            connection.setAutoCommit(false);
            deleteTicketsStatement.setLong(1, flightId);
            deleteFlightStatement.setLong(1, flightId);
            deleteTicketsStatement.executeUpdate();
            if (true){
                throw  new RuntimeException("OOOps");
            }
            deleteFlightStatement.executeUpdate();
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
            if (deleteFlightStatement != null){
                deleteFlightStatement.close();
            }
            if (deleteTicketsStatement != null){
                deleteTicketsStatement.close();
            }
        }

    }
}
