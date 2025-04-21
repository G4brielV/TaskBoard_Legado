package br.com.dio.dto;

import java.util.List;

public record CardReportDTO(
        long cardId,
        String cardTitle,
        List<ColumnDurationDTO> columnDurations,
        long totalSeconds
) {}