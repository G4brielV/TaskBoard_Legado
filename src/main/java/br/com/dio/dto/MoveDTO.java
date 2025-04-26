package br.com.dio.dto;

public record MoveDTO(Long cardId,
                      String cardTitle,
                      String columnName,
                      long durationSeconds) {
}
