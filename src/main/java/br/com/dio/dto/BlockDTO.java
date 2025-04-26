package br.com.dio.dto;

public record BlockDTO(String block_reason,
                       String unblock_reason,
                       long durationSeconds) {
}
