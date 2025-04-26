package br.com.dio.service;

import br.com.dio.dto.*;
import br.com.dio.persistence.dao.BoardColumnDAO;
import br.com.dio.persistence.dao.BoardDAO;
import br.com.dio.persistence.dao.CardDAO;
import br.com.dio.persistence.entity.BoardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

@AllArgsConstructor
public class BoardQueryService {

    private final Connection connection;

    public Optional<BoardEntity> findById(final Long id) throws SQLException {
        var dao = new BoardDAO(connection);
        var boardColumnDAO = new BoardColumnDAO(connection);
        var optional = dao.findById(id);
        if (optional.isPresent()){
            var entity = optional.get();
            entity.setBoardColumns(boardColumnDAO.findByBoardId(entity.getId()));
            return Optional.of(entity);
        }
        return Optional.empty();
    }

    public Optional<BoardDetailsDTO> showBoardDetails(final Long id) throws SQLException {
        var dao = new BoardDAO(connection);
        var boardColumnDAO = new BoardColumnDAO(connection);
        var optional = dao.findById(id);
        if (optional.isPresent()){
            var entity = optional.get();
            var columns = boardColumnDAO.findByBoardIdWithDetails(entity.getId());
            var dto = new BoardDetailsDTO(entity.getId(), entity.getName(), columns);
            return Optional.of(dto);
        }
        return Optional.empty();
    }

    public Optional<BoardReportDTO> getBoardReport(final Long boardId) throws SQLException{
        var dao = new BoardDAO(connection);
        var cardDAO = new CardDAO(connection);
        var optional = dao.findById(boardId);
        if (optional.isPresent()){
            var entity = optional.get();
            List<MoveDTO> report = cardDAO.findColumnDurationsByBoardId(entity.getId());

            Map<Long, List<ColumnDurationDTO>> durationsByCard = new LinkedHashMap<>();
            Map<Long, String> titleByCard = new HashMap<>();

            for (var rec : report) {
                titleByCard.putIfAbsent(rec.cardId(), rec.cardTitle());
                durationsByCard
                        .computeIfAbsent(rec.cardId(), id -> new ArrayList<>())
                        .add(new ColumnDurationDTO(rec.columnName(), rec.durationSeconds()));
            }
            List<CardReportDTO> cards = new ArrayList<>();
            for (var entry : durationsByCard.entrySet()) {
                Long cardId = entry.getKey();
                List<ColumnDurationDTO> cols = entry.getValue();
                long total = cols.stream()
                        .mapToLong(ColumnDurationDTO::durationSeconds)
                        .sum();

                cards.add(new CardReportDTO(
                        cardId,
                        titleByCard.get(cardId),
                        cols,
                        total
                ));
            }
            var dto = new BoardReportDTO(boardId, entity.getName(), cards);
            return Optional.of(dto);
        }
        return Optional.empty();
    }

    public Optional<BoardBlockCardDTO> getBoardCardsBlocked(final Long boardId) throws SQLException {
        var dao = new BoardDAO(connection);
        var cardDAO = new CardDAO(connection);
        var optional = dao.findById(boardId);
        if (optional.isPresent()) {
            var entity = optional.get();
            List<CardDAO.CardBlockRecord> blocks = cardDAO.findBlockedCardByBoardId(entity.getId());

            Map<Long, List<BlockDTO>> blocksByCard = new LinkedHashMap<>();
            Map<Long, String> cardTitles = new HashMap<>();

            for (var blc : blocks) {
                cardTitles.putIfAbsent(blc.cardId(), blc.cardTitle());
                blocksByCard
                        .computeIfAbsent(blc.cardId(), id -> new ArrayList<>())
                        .add(new BlockDTO(blc.blockReason(), blc.unblockReason(), blc.durationSeconds()));
            }

            List<CardBlockDTO> cardBlockDTOs = new ArrayList<>();
            for (var entry : blocksByCard.entrySet()) {
                Long cardId = entry.getKey();
                String cardTitle = cardTitles.get(cardId);
                List<BlockDTO> blockList = entry.getValue();

                cardBlockDTOs.add(new CardBlockDTO(cardId, cardTitle, blockList));
            }

            var dto = new BoardBlockCardDTO(boardId, entity.getName(), cardBlockDTOs);
            return Optional.of(dto);
        }
        return Optional.empty();
    }
}
