package br.com.dio.dto;

import java.util.List;

public record BoardReportDTO(
        long boardId,
        String boardName,
        List<CardReportDTO> cards
) {}
