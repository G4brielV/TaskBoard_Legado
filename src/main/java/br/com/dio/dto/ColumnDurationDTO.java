package br.com.dio.dto;

public record ColumnDurationDTO(
        String columnName,
        long durationSeconds
) {}
