# 📝 Board Project 
Este projeto é um fork do repositório [digitalinnovationone/board](https://github.com/digitalinnovationone/santander-dev-week-2023-api), focado em simular um sistema de gerenciamento de tarefas utilizando um fluxo de board estilo Kanban.

O projeto foi expandido para incluir os [requisitos opcionais](https://github.com/digitalinnovationone/exercicios-java-basico/blob/main/projetos/4%20-%20T%C3%A9cnicas%20Avan%C3%A7adas%2C%20Padr%C3%B5es%20e%20Persist%C3%AAncia%20(Literalmente).md), preservando a proposta de utilização de Java legado — ou seja, sem frameworks como Spring, Hibernate ou JPA — utilizando JDBC puro para comunicação com o banco de dados.

---
## 🛠 Funcionalidades Implementadas
### ✅ Funcionalidades Originais
- Criação de Boards, Colunas e Cards
- Movimentação de cards entre colunas
- Bloqueio e desbloqueio de cards
- Cancelamento de cards

### ✨ Melhorias Adicionadas
1. Armazenamento de Movimentação dos Cards
Cada card agora registra:
  - A data/hora em que entrou em uma coluna.
  - A data/hora em que saiu da coluna para a próxima.

2. Relatório de Tempo por Card
Geração de relatório para o Board selecionado:
- Exibe todos os cards.
- Mostra o tempo (em segundos) que cada card permaneceu em cada coluna.
- Mostra o tempo total para a conclusão de cada card.

3. Relatório de Bloqueios dos Cards
Geração de relatório de bloqueios para o Board selecionado:
- Lista todos os cards bloqueados.
- Para cada bloqueio, exibe:
  - Motivo do bloqueio
  - Motivo do desbloqueio (ou indica se ainda está bloqueado)
  - Duração do bloqueio em segundos

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
