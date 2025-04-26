package br.com.dio.persistence.dao;

import br.com.dio.dto.CardBlockDTO;
import br.com.dio.dto.CardDetailsDTO;
import br.com.dio.dto.CardReportDTO;
import br.com.dio.dto.MoveDTO;
import br.com.dio.persistence.entity.CardEntity;
import com.mysql.cj.jdbc.StatementImpl;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import static br.com.dio.persistence.converter.OffsetDateTimeConverter.toOffsetDateTime;
import static java.util.Objects.nonNull;

@AllArgsConstructor
public class CardDAO {

    private Connection connection;

    public CardEntity insert(final CardEntity entity) throws SQLException {
        var sql = "INSERT INTO CARDS (title, description, board_column_id) values (?, ?, ?);";
        try(var statement = connection.prepareStatement(sql)){
            var i = 1;
            statement.setString(i ++, entity.getTitle());
            statement.setString(i ++, entity.getDescription());
            statement.setLong(i, entity.getBoardColumn().getId());
            statement.executeUpdate();
            if (statement instanceof StatementImpl impl){
                entity.setId(impl.getLastInsertID());
            }
        }
        return entity;
    }

    public void moveToColumn(final Long columnId, final Long cardId) throws SQLException{
        var sql = "UPDATE CARDS SET board_column_id = ? WHERE id = ?;";
        try(var statement = connection.prepareStatement(sql)){
            var i = 1;
            statement.setLong(i ++, columnId);
            statement.setLong(i, cardId);
            statement.executeUpdate();
        }
    }

    public Optional<CardDetailsDTO> findById(final Long id) throws SQLException {
        var sql =
                """
                SELECT c.id,
                       c.title,
                       c.description,
                       b.blocked_at,
                       b.block_reason,
                       c.board_column_id,
                       bc.name,
                       (SELECT COUNT(sub_b.id)
                               FROM BLOCKS sub_b
                              WHERE sub_b.card_id = c.id) blocks_amount
                  FROM CARDS c
                  LEFT JOIN BLOCKS b
                    ON c.id = b.card_id
                   AND b.unblocked_at IS NULL
                 INNER JOIN BOARDS_COLUMNS bc
                    ON bc.id = c.board_column_id
                  WHERE c.id = ?;
                """;
        try(var statement = connection.prepareStatement(sql)){
            statement.setLong(1, id);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            if (resultSet.next()){
                var dto = new CardDetailsDTO(
                        resultSet.getLong("c.id"),
                        resultSet.getString("c.title"),
                        resultSet.getString("c.description"),
                        nonNull(resultSet.getString("b.block_reason")),
                        toOffsetDateTime(resultSet.getTimestamp("b.blocked_at")),
                        resultSet.getString("b.block_reason"),
                        resultSet.getInt("blocks_amount"),
                        resultSet.getLong("c.board_column_id"),
                        resultSet.getString("bc.name")
                );
                return Optional.of(dto);
            }
        }
        return Optional.empty();
    }

    public List<MoveDTO> findColumnDurationsByBoardId (final Long boardId) throws SQLException {
        var sql =
                """
                        SELECT
                        	c.id AS card_id,
                        	c.title AS card_title,
                        	bc_from.name AS column_name,
                        	TIMESTAMPDIFF(SECOND, m.current_column_at, m.moved_at) AS duration_seconds
                        FROM moves m
                        	INNER JOIN cards c
                        		ON c.id = m.card_id
                        	INNER JOIN boards_columns bc_from
                        		ON bc_from.id = m.from_column_id
                        WHERE bc_from.board_id = ?
                        and m.moved_at is not null
                        ORDER BY c.id, m.current_column_at
                """;
        List<MoveDTO> report = new ArrayList<>();
        try(var statement = connection.prepareStatement(sql)){
            statement.setLong(1, boardId);
            statement.executeQuery();
            var resultSet = statement.getResultSet();


            while (resultSet.next()) {
                report.add(new MoveDTO(
                        resultSet.getLong("card_id"),
                        resultSet.getString("card_title"),
                        resultSet.getString("column_name"),
                        resultSet.getLong("duration_seconds")
                ));
            }
            return report;
        }

    }



    public List<CardBlockRecord> findBlockedCardByBoardId(final Long boardId) throws SQLException{
        var sql = """
                SELECT
                	c.id AS card_id,
                	c.title AS card_title,
                	blc.block_reason AS block_reason,
                	IFNULL(blc.unblock_reason, "currently locked") AS unblock_reason,
                	IFNULL(TIMESTAMPDIFF(SECOND, blc.blocked_at, blc.unblocked_at), 0) AS duration_seconds
                FROM blocks blc
                	inner join cards c
                		on blc.card_id = c.id
                	inner join boards_columns bc\s
                		on c.board_column_id = bc.id
                WHERE bc.board_id = ?
                ORDER BY c.id
                """;

        List<CardBlockRecord> blocks = new ArrayList<>();
        try(var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, boardId);
            statement.executeQuery();
            var resultSet = statement.getResultSet();

            while (resultSet.next()) {
                blocks.add(new CardBlockRecord(
                        resultSet.getLong("card_id"),
                        resultSet.getString("card_title"),
                        resultSet.getString("block_reason"),
                        resultSet.getString("unblock_reason"),
                        resultSet.getLong("duration_seconds")
                ));
            }
            return blocks;
        }
    }
    public record CardBlockRecord(
            Long cardId,
            String cardTitle,
            String blockReason,
            String unblockReason,
            long durationSeconds
    ) {}


}
