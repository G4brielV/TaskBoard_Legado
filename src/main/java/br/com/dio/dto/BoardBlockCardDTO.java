package br.com.dio.dto;

import java.util.List;

public record BoardBlockCardDTO(
        long boardId,
        String boardName,
        List<CardBlockDTO> cards
) {
}
