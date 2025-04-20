--liquibase formatted sql
--changeset gabriel:202504191504
--comment: moves table create

CREATE TABLE MOVES (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    from_column_id     BIGINT NOT NULL,
    to_column_id       BIGINT NOT NULL,
    current_column_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    moved_at           TIMESTAMP NULL,
    card_id            BIGINT NOT NULL,
    CONSTRAINT cards__moves_fk          FOREIGN KEY (card_id)         REFERENCES CARDS(id),
    CONSTRAINT boardColumnFrom__fk      FOREIGN KEY (from_column_id)  REFERENCES BOARDS_COLUMNS(id),
    CONSTRAINT boardColumnTo__fk        FOREIGN KEY (to_column_id)    REFERENCES BOARDS_COLUMNS(id)
) ENGINE=InnoDB;

--rollback DROP TABLE MOVES