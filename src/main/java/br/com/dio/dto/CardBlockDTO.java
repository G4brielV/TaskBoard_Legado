package br.com.dio.dto;

import java.util.List;

public record CardBlockDTO (Long cardId,
                            String cardTitle,
                            List<BlockDTO> blocks){ }
