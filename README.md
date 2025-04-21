## Diagrama de Classes
```mermaid
classDiagram
    class Board {
        Long id
        String name
    }

    class BoardColumn {
        Long id
        String name
        String kind
        Integer order
        Long boardId (FK) 
    }

    class Card {
        Long id
        String title
        String description
        Long boardColumnId (FK) 
    }

    class Block {
        Long id
        Timestamp blockedAt   
        String blockReason
        Timestamp unblockedAt
        String unblockReason
        Long cardId (FK) 
    }

    class Move {
        Long id
        Long from_column_id (FK) 
        Long to_column_id (FK)  
        Timestamp current_column_at
        Timestamp moved_at
        Long cardId (FK) 
    }

    Board "1" o-- "1..*" BoardColumn
    BoardColumn "1" o-- "1..*" Card
    Card "1" o-- "*" Block
    Card "1" o-- "*" Move
