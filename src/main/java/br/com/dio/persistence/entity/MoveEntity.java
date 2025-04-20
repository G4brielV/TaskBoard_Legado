package br.com.dio.persistence.entity;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class MoveEntity {

    private Long id;
    private Long from_column_id ;
    private Long to_column_id;
    private OffsetDateTime current_column_at;
    private OffsetDateTime moved_at;
}
