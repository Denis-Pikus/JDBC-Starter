package dmdev.jdbc.starter.runner;

import dmdev.jdbc.starter.util.ConnectionPool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;

/**
 * Blob (bytea в postgres) используется для внесения в базу картинок, видео, текстовых файлов и т.п. контента в виде байт
 * Clob (TEXT в postgres) используется для внесения в базу данных в виде символов
 * но на практике никто не хранит большие файлы в БД
 * они хранятся в облачных хранилищах, а в БД записывается их адрес
 */
public class BlobPoolRunner {
    public static void main(String[] args) throws SQLException, IOException {
        var sql = """
                UPDATE aircraft 
                SET image = ?
                WHERE id = 1
                """;
       saveImage(sql);
        getImage();
    }

    private static void saveImage(String sql) throws SQLException, IOException {
        try (var connection = ConnectionPool.get();
             final var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setBytes(1, Files.readAllBytes(Path.of("resources", "boing.jpg")));
            preparedStatement.executeUpdate();
        }
    }

    public static void getImage() throws SQLException, IOException {
        var sql = """
                SELECT image
                FROM aircraft
                WHERE id = ?
                """;
        try (var connection = ConnectionPool.get();
             var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, 1);
            final var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                final var image = resultSet.getBytes("image");
                Files.write(Path.of("resources", "image1.jpg"), image, StandardOpenOption.CREATE);
            }
        }
    }

    // Для БД типа Oracle
//    private static void saveImage(String sql) throws SQLException, IOException {
//        try (var connection = ConnectionPool.get();
//             final var preparedStatement = connection.prepareStatement(sql)) {
//            connection.setAutoCommit(false);
//            final var blob = connection.createBlob();
//            blob.setBytes(1, Files.readAllBytes(Path.of("resources", "boing.jpg")));
//            preparedStatement.setBlob(1, blob);
//            preparedStatement.executeUpdate();
//            connection.commit();
//        }
//    }
}
