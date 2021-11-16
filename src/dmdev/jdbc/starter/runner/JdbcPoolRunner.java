package dmdev.jdbc.starter.runner;

import dmdev.jdbc.starter.util.ConnectionPool;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JdbcPoolRunner {
    public static void main(String[] args) throws SQLException {
//        Long flightId = 2L;
//        var result = getTicketsByFlightId(flightId);
//        final var result = getFlightBetween(LocalDate.of(2020, 01, 1).atStartOfDay(), LocalDateTime.now());
//        System.out.println(result);
        try{
            checkMetaData();
        }finally {
            ConnectionPool.closePool();
        }
    }

    public static void checkMetaData() throws SQLException {
        try (var connection = ConnectionPool.get()) {
            var metaData = connection.getMetaData();
            var catalogs = metaData.getCatalogs();
            while (catalogs.next()){
                final var catalog = catalogs.getString(1);
                System.out.println(catalog);
                var schemas = metaData.getSchemas();
                while (schemas.next()){
                    final var schema = schemas.getString("TABLE_SCHEM");
                    System.out.println(schema);
                    var tables = metaData.getTables(catalog, schema, "%", null);
                    if (schema.equals("public")){
                        while (tables.next()){
                            System.out.println(tables.getString("TABLE_NAME"));
                        }

                    }
                }
            }
        }

    }

    private static List<Long> getTicketsByFlightId(Long flightId) throws SQLException {
        String sql = """
                SELECT id FROM ticket 
                WHERE flight_id = ?
                """;
        List<Long> result = new ArrayList<>();
        try (var connection = ConnectionPool.get();
             var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, flightId);
            var resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                result.add(resultSet.getObject("id", Long.class));
            }
        }
        return result;
    }

    private static List<Long> getFlightBetween(LocalDateTime start, LocalDateTime end) throws SQLException {
        String sql = """
                SELECT  id
                FROM flight
                WHERE departure_date BETWEEN ? AND ?
                """;
        List<Long> result = new ArrayList<>();
        try (var connection = ConnectionPool.get();
             var preparedStatement = connection.prepareStatement(sql)) {
            System.out.println(preparedStatement);
            preparedStatement.setTimestamp(1, Timestamp.valueOf(start));
            System.out.println(preparedStatement);
            preparedStatement.setTimestamp(2, Timestamp.valueOf(end));
            System.out.println(preparedStatement);
            var resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                result.add(resultSet.getLong("id"));
            }
        }
        return result;
    }
}
