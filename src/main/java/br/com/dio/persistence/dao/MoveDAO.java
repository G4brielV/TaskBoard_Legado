package br.com.dio.persistence.dao;

import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.OffsetDateTime;

import static br.com.dio.persistence.converter.OffsetDateTimeConverter.toTimestamp;

@AllArgsConstructor
public class MoveDAO {

    private final Connection connection;

    public void startMove(final Long boardColumnFromId, final Long boardColumnToId, final Long cardId) throws SQLException {
        var sql = "INSERT INTO MOVES (from_column_id, to_column_id, current_column_at, card_id) VALUES (?, ?, ?, ?);";
        try(var statement = connection.prepareStatement(sql)){
            var i = 1;
            statement.setLong(i++, boardColumnFromId);
            statement.setLong(i++, boardColumnToId);
            statement.setTimestamp(i ++, toTimestamp(OffsetDateTime.now()));
            statement.setLong(i, cardId);
            statement.executeUpdate();
        }
    }


    public void finishMove(final Long cardId) throws SQLException{
        var sql = "UPDATE MOVES SET moved_at = ? WHERE card_id = ? AND moved_at IS NULL;";
        try(var statement = connection.prepareStatement(sql)){
            var i = 1;
            statement.setTimestamp(i ++, toTimestamp(OffsetDateTime.now()));
            statement.setLong(i, cardId);
            statement.executeUpdate();
        }
    }


}
