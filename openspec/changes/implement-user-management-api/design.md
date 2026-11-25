# Design

## Context

Consulte `proposal.md` para motivação e escopo. O repositório não contém aplicação Java nem capacidades OpenSpec persistentes; o PDF do desafio é a fonte dos critérios. As especificações deste change descrevem o comportamento externo; este documento registra as decisões de implementação e os entregáveis necessários.

## Goals / Non-Goals

**Goals:**

- Manter domínio e casos de uso independentes de Spring MVC, Spring Security, JPA e MySQL.
- Aplicar autorização explícita por tipo e titularidade.
- Definir o esquema com todos os campos obrigatórios do desafio e tornar reproduzível a avaliação.
- Tornar cada entregável e critério do PDF verificável por testes, arquivos ou evidência documental.

**Non-Goals:**

- Desenvolver funcionalidades de restaurantes, cardápios, pedidos, avaliações ou pagamentos nesta fase.
- Implementar tokens de atualização, revogação imediata de JWT ou provedor de identidade externo.
- Dividir o sistema em microsserviços.

## Decisions

### Estrutura da aplicação e arquitetura

- Usar Java 21, Spring Boot 3.x, Maven Wrapper e monólito modular organizado por funcionalidade. Dentro de cada funcionalidade, separar domínio, aplicação/casos de uso, adaptadores web de entrada e adaptadores de saída para JPA/segurança.
- Manter DTOs HTTP e entidades JPA fora do domínio. Usar injeção por construtor, transações na camada de casos de uso e portas somente onde os casos de uso precisarem de abstrações.
- Usar Spring MVC, Bean Validation e DTOs imutáveis (records) para a API síncrona. Cobrir domínio e serviços com JUnit e Mockito, além de testes de integração.
- A estrutura evita dependências do domínio em relação ao framework sem o custo operacional de microsserviços. As responsabilidades devem seguir SOLID e ser justificadas no relatório técnico.

### Uso obrigatório das skills pelo agente

Antes de executar uma tarefa, o agente SHALL carregar e seguir a skill correspondente ao escopo abaixo. Quando uma tarefa cruzar mais de um escopo, SHALL aplicar todas as skills pertinentes; não deve carregar uma skill sem relação com a tarefa.

| Skill                     | Quando seu uso é obrigatório                                                                                 |
| ------------------------- | ------------------------------------------------------------------------------------------------------------ |
| `/clean-architecture`     | Definir ou alterar camadas, limites, direção das dependências e portas/adaptadores                           |
| `/solid-principles`       | Projetar ou revisar responsabilidades de módulos, serviços, interfaces e extensibilidade                     |
| `/java-springboot`        | Criar ou alterar qualquer código/configuração Spring Boot, persistência, validação, segurança ou testes Java |
| `/mysql`                  | Definir ou alterar esquema, migrações, tipos, restrições, consultas ou testes MySQL                          |
| `/multi-stage-dockerfile` | Criar ou alterar Dockerfile, imagem de execução ou etapas de build em contêiner                              |
| `/swagger-doc-creator`    | Criar ou alterar endpoints, contratos HTTP ou documentação Swagger/OpenAPI                                   |
| `/conventional-commits`   | Preparar, revisar ou criar mensagens de commit                                                               |
| `/bash-linux`             | Executar verificações em shell, operar o GitHub CLI (`gh`) e automatizar validações de entrega               |
| `/testing-patterns`       | Projetar ou revisar estratégia de testes, pirâmide de testes, mocking e cobertura                            |
| `/verify-changes`         | Verificar que código e entregáveis funcionam por execução; tarefas de conferência e checklists finais        |

O agente SHALL verificar as orientações pertinentes antes da implementação e validar a entrega contra elas; se a skill não estiver disponível, deve informar a limitação e não alegar que a utilizou.

### Comentários no código

- Não adicionar comentários de linha, bloco, multilinha ou Javadoc ao código de produção, testes, scripts ou migrações.
- Em arquivos de configuração, comentários só são permitidos quando estritamente necessários para explicar uma configuração não óbvia.
- Preferir nomes claros, estrutura simples e documentação externa quando uma explicação for necessária.

### Estrutura sugerida do repositório

Adotar a estrutura mínima indicada pelo desafio e detalhar `src/` para separar código principal e testes:

```text
.
├── src/
│   ├── main/
│   │   └── java/...
│   └── test/
│       └── java/...
├── postman/
│   ├── collection.json
│   └── environment.json
├── docs/
│   └── relatorio-tecnico.pdf
├── Dockerfile
├── docker-compose.yml
├── .env.example
├── .gitignore
├── pom.xml
├── mvnw
└── README.md
```

Manter no README as instruções, pré-requisitos, descrição, tecnologias, URL Swagger, integrantes e credenciais de avaliação exigidas. O relatório deve conter link para o repositório público. A cópia do relatório em `docs/` complementa, mas não substitui, sua submissão oficial em PDF na plataforma FIAP e dentro do prazo.

### Modelo e esquema MySQL

- Usar MySQL 8.4 LTS/InnoDB como banco relacional, Spring Data JPA atrás de porta da aplicação e migrações Flyway. Incluir MySQL Connector/J e o suporte Flyway para MySQL; configurar Hibernate para validar o esquema.
- Persistir cada usuário em `app_user`. O endereço será um objeto de valor, não uma entidade/tabela independente: atende ao endereço estruturado sugerido no PDF e evita relacionamento sem ciclo de vida próprio.
- Configurar banco e tabela com `utf8mb4` e collation `utf8mb4_0900_ai_ci`. Definir comprimentos `VARCHAR` compatíveis com as validações da API antes de criar índices únicos; manter a normalização de e-mail/login também na aplicação. A collation é case-insensitive e accent-insensitive, portanto sua semântica de unicidade e busca deve ser explicitada e coberta por testes.
- Esquema planejado:

| Coluna             | Tipo MySQL                       | Restrições/semântica                                                                               |
| ------------------ | -------------------------------- | -------------------------------------------------------------------------------------------------- |
| `id`               | `BIGINT UNSIGNED AUTO_INCREMENT` | `NOT NULL PRIMARY KEY`; chave técnica monotônica                                                   |
| `name`             | `VARCHAR(150)`                   | `NOT NULL`; API valida o mesmo limite                                                              |
| `email`            | `VARCHAR(254)`                   | `NOT NULL UNIQUE`; normalizado; API valida o mesmo limite                                          |
| `login`            | `VARCHAR(100)`                   | `NOT NULL UNIQUE`; normalizado; API valida o mesmo limite                                          |
| `password_hash`    | `VARCHAR(255)`                   | `NOT NULL`; compatível com o codificador de senha; nunca armazenar senha em texto puro             |
| `user_type`        | `VARCHAR(32)`                    | `NOT NULL CHECK`; somente `DONO_RESTAURANTE` ou `CLIENTE`, sem tipo ENUM nativo                    |
| `last_modified_at` | `DATETIME(6)`                    | `NOT NULL`; armazenar em UTC, inicializar na criação e atualizar em toda alteração de perfil/senha |
| `address`          | `JSON`                           | `NOT NULL`; objeto com `street`, `number`, `city` e `postal_code`                                  |

- A coluna `address` SHALL vir imediatamente após `last_modified_at` na definição da tabela e conter um objeto JSON com o formato:

    ```json
    {
    	"street": "Rua Exemplo",
    	"number": "123",
    	"city": "São Paulo",
    	"postal_code": "01000-000"
    }
    ```

- `address` SHALL ser `NOT NULL`. O tipo nativo MySQL `JSON` SHALL rejeitar documentos JSON malformados; uma restrição `CHECK` no MySQL 8.4 SHALL exigir objeto JSON, as quatro chaves obrigatórias e valores string não vazios. A validação da aplicação também SHALL rejeitar endereço incompleto. O campo `number` é string para preservar complementos e identificadores alfanuméricos; `postal_code` é string para preservar zeros à esquerda e formatos locais.
- No domínio Java, representar o tipo como `UserType` enum; persistir seu valor em coluna textual com restrição `CHECK`, sem usar `ENUM` nativo, para evitar acoplamento de migrações à lista de tipos. Representar o endereço como objeto/record de valor e persistir o objeto inteiro em `address JSON`, sem criar tabela separada.
- O MySQL não indexa diretamente caminhos JSON. Como o endereço não participa dos filtros ou ordenações desta fase, não criar índices JSON gerados agora; caso consultas por caminho sejam introduzidas, avaliar coluna gerada tipada e índice correspondente.
- Normalizar e-mail e login antes de persistir e reforçar unicidade com restrições do banco, inclusive para concorrência. Traduzir para `409` somente violações reconhecidas das restrições correspondentes.
- Usar busca por nome parcial sem diferenciar caixa, paginação limitada (com limite máximo `size=50`, rejeitando valores superiores ou inválidos com `400 Bad Request`) e ordenação estável, validando a compatibilidade das consultas com collation e índices escolhidos.
- O relatório deve apresentar campos, tipos, restrições, modelo/relacionamentos e justificar o enum no domínio, `VARCHAR` com `CHECK` no banco e endereço como objeto JSON, conforme exigido pelo desafio.
- Usar Testcontainers com MySQL 8.4 para testar migrações, CRUD, unicidade, atualização de data/hora, persistência/validação JSON e restrições de esquema (limites de comprimento, collation `utf8mb4_0900_ai_ci` e cláusulas `CHECK`) no mecanismo real.

### API, autenticação e autorização

- Seguir exatamente os contratos de rota e status descritos em `specs/api-contracts/spec.md`, usando `/api/v1`, incluindo o header `Location: /api/v1/users/{id}` no `201 Created` de `POST /api/v1/users`.
- Usar Spring Security resource server JWT com Nimbus para validar token e emitir token no login; não implementar análise/verificação criptográfica manual. BCrypt será usado para senha.
- Assinar JWTs de acesso com HMAC e chave externa à aplicação de pelo menos 256 bits, validade planejada de 15 minutos, identificador no `subject` e tipo em claim de autoridade. Validar configuração na inicialização. A chave de `.env.example` será apenas ilustrativa e de avaliação, nunca para produção.
- Separar claramente regra de domínio e enforcement HTTP: os casos de uso recebem o identificador e o tipo do solicitante autenticado como entrada para validar regras de negócio e titularidade sem depender de Spring Security, enquanto o `SecurityFilterChain` e os adaptadores web aplicam autenticação (`401`) e autorização (`403`) na borda HTTP.
- `DONO_RESTAURANTE` pode criar, buscar, consultar, atualizar perfil e excluir qualquer conta. `CLIENTE` pode consultar, atualizar perfil e excluir somente a própria conta. Ambos podem alterar somente a própria senha e devem informar senha atual. O tipo só pode ser atribuído por dono na criação; cadastro público e alteração de tipo pelo perfil não existem.
- Criar uma conta `DONO_RESTAURANTE` idempotente por variáveis externas somente em perfil de desenvolvimento/avaliação (Condição A do item 10 do enunciado). Armazenar senha em hash e não redefinir conta existente em reinicialização. Desabilitar seed na produção. Documentar login/senha de avaliação em texto claro no README e relatório, como exige o item 10, e exemplificar login/token na coleção Postman, Swagger/OpenAPI e exemplos `curl`.
- Configurar CORS explicitamente por perfil: no ambiente de avaliação/desenvolvimento, permitir origens necessárias ao Swagger UI e ao Postman; em produção, restringir a origens explicitamente autorizadas sem wildcard.
- Manter Swagger UI acessível no ambiente de avaliação e desabilitado ou protegido em produção. Padronizar erros com `ProblemDetail` e não expor dados internos.

### Docker e configuração

- O Dockerfile deve ser obrigatoriamente multi-stage, com estágios de build JDK/Maven e execução com JRE mínimo compatível, imagem fixada, usuário não root e `.dockerignore`.
- Criar exatamente `docker-compose.yml` para iniciar aplicação e MySQL 8.4 com imagem fixada e health check. Externalizar configurações. Incluir `.env.example` sem credenciais reais; variáveis de exemplo devem ser explicitamente não produtivas.
- Validar o fluxo a partir de ambiente limpo por `docker compose up --build` e confirmar que aplicação e MySQL estão prontos, login seed funciona e endpoints protegidos podem ser avaliados.

### Documentação, testes e histórico de desenvolvimento

- Antes de trabalhar em cada área, o agente SHALL aplicar as skills obrigatórias mapeadas na seção "Uso obrigatório das skills pelo agente". Em particular, mudanças no modelo e nas migrações exigem `/mysql`; persistência/configuração Spring exige `/java-springboot`; limites e responsabilidades exigem `/clean-architecture` e `/solid-principles`; rotas e documentação exigem `/swagger-doc-creator`; empacotamento exige `/multi-stage-dockerfile`; commits exigem `/conventional-commits`; operações shell/`gh` exigem `/bash-linux`; estratégia de testes exige `/testing-patterns`; validações e conferências finais exigem `/verify-changes`.
- OpenAPI/Swagger deve documentar todas as rotas, autorização, exemplos de sucesso e erros `400`, `401`, `403`, `404`, `409` e falhas internas seguras. A coleção JSON e o environment Postman cobrem todos os cenários do item 5.
- README deve conter descrição do projeto, tecnologias, pré-requisitos, passo a passo Docker Compose, URL Swagger, integrantes, repositório, credenciais do usuário de avaliação quando JWT estiver habilitado e seção de exemplos `curl` cobrindo login e chamadas protegidas.
- O relatório PDF deve conter link público do repositório, arquitetura detalhada, modelo e relacionamentos, justificativa explícita da escolha da Condição A do item 10 (seed idempotente sem cadastro público) em vez da Condição B e da restrição da busca por nome a `DONO_RESTAURANTE`, endpoints com exemplos, evidências Swagger e Postman, tabelas/colunas, execução Docker e variáveis de ambiente, `git log --oneline --graph`, `git shortlog -sn` e credenciais de avaliação. Submeter o PDF na plataforma FIAP dentro do prazo divulgado.
- Usar JUnit e Mockito para testes unitários, no mínimo na camada de serviço/casos de uso, além de testes de integração/API, testes de restrições MySQL e testes de contrato alinhados ao schema OpenAPI.

### Estratégia individual de branches, commits e integração

- O trabalho será executado por uma única pessoa. Configurar `user.name` e `user.email` do Git com a identidade real dessa pessoa; não criar autores artificiais. `git shortlog -sn` deve refletir honestamente um único autor.
- Manter `main` como branch estável. Após preparar na `main` uma base mínima que compile e tenha `.gitignore` adequado, criar a branch de integração `feature/tech-challenge` a partir de `main`. O desenvolvimento das etapas ocorre em branches próprias criadas diretamente a partir de `feature/tech-challenge`, não a partir da branch da etapa anterior.
- Ordem de execução das etapas: executar o grupo 4 (JWT e Spring Security) imediatamente após `3.1`–`3.2` e antes de `3.3`–`3.7`, seguindo a sequência `1 → 2 → 3.1–3.2 → 4 → 3.3–3.7 → 5 → 6 → 7 → 8 → 9 → 10`.
- Criar uma branch de etapa por conjunto coerente de tarefas, respeitando essa ordem:
    - `feature/fundacao` para tarefas 2.1–2.2;
    - `feature/dominio-schema` para tarefas 3.1–3.2 (entidades de domínio puro e migração Flyway MySQL);
    - `feature/jwt-autorizacao` para tarefas 4.1–4.3 (executada antes dos casos de uso e da integração JPA);
    - `feature/casos-de-uso-mysql` para tarefas 3.3–3.7 (casos de uso, adaptador JPA e testes de constraints MySQL);
    - `feature/api-documentacao` para tarefas 5.1–5.5 (rotas, `ProblemDetail`, Swagger, Postman e CORS);
    - `feature/docker-config` para tarefas 6.1–6.3;
    - `feature/documentacao-entrega` para tarefas 7.1–7.5 (README, exemplos `curl`, relatório PDF e repositório);
    - `feature/testes-contrato` para tarefas 8.1–8.2 (consolidação da suíte e testes de contrato OpenAPI);
    - `feature/qualidade-git` para tarefas 1.1–1.5 e conferências finais (9.1–9.5 e 10.1–10.11).
- Cada branch de etapa deve gerar um Pull Request direcionado a `feature/tech-challenge` via `gh pr create --base feature/tech-challenge`. Fazer revisão individual do diff com `gh pr diff`, verificar testes/build e corrigir os achados antes do merge com `gh pr merge`. Pull Request é o mecanismo de revisão e evidência, mesmo em trabalho individual; a revisão é feita pelo próprio autor, sem simular revisão por outra pessoa.
- Depois de abrir o PR de uma etapa, interromper o avanço do trabalho e aguardar autorização humana expressa antes de iniciar o desenvolvimento de uma nova etapa. Alterações solicitadas no PR aberto continuam pertencendo à etapa atual e não exigem iniciar outra etapa.
- Abrir PRs de etapa quando houver uma unidade lógica completa e verificável. Não é necessário abrir um PR por tarefa pequena; tarefas relacionadas podem ser entregues juntas em uma etapa, mas não se deve acumular todas as etapas em um único PR.
- Frequência de commits: criar um commit assim que uma unidade lógica de trabalho tiver resultado verificável, testes pertinentes passando e projeto compilável. Ao final de cada sessão de trabalho, não deixar alterações prontas e verificadas sem commit. Se uma tarefa grande levar várias sessões, dividi-la em incrementos independentes que compilem; não criar commits artificiais por arquivo ou por intervalo de tempo, e não registrar um estado quebrado como entrega concluída.
- Os commits devem ser atômicos, manter o projeto compilável e usar Conventional Commits no imperativo, com assunto de até 72 caracteres. Usar assuntos em português sempre que claros, por exemplo `feat(user): criar cadastro administrativo` ou `test(auth): cobrir rejeição de token expirado`.
- Antes de cada PR de etapa, executar a verificação pertinente à etapa e revisar o diff; antes do PR final, executar a suíte completa e o fluxo de avaliação Docker. Não reescrever nem apagar histórico publicado com force-push; não usar mensagens genéricas. Tags/releases permanecem opcionais.
- O PR para `main` é exclusivamente final: abrir da branch `feature/tech-challenge` para `main` com `gh pr create --base main` somente depois de todas as tarefas de implementação e verificação concluídas e de todas as mudanças OpenSpec relacionadas arquivadas. No PR final, apresentar resumo, evidências de `./mvnw verify`, Compose e checklist de entrega; integrar em `main` com `gh pr merge` somente após a revisão individual final.
- Manter `.gitignore` excluindo `target/`, `.idea/`, `.env` real, segredos e demais arquivos indevidos.

## Risks / Trade-offs

- [HMAC compartilhado depende de proteção da chave] → Manter chave fora do código, validar tamanho e separar valores de avaliação e produção.
- [JWT stateless permanece válido até expirar após exclusão ou troca de senha] → Usar validade curta; revogação imediata está fora do escopo.
- [Credenciais de avaliação precisam ser publicadas em texto claro no README/PDF] → Restringir seed a avaliação/desenvolvimento e nunca reutilizar credenciais em produção.
- [Autorização por identificador pode permitir acesso indevido entre contas] → Centralizar política e testar cada operação entre usuários.
- [Busca parcial pode percorrer a tabela] → Limitar paginação e adiar índice trigram até haver evidência de necessidade.
- [Swagger pode ser bloqueado pela segurança] → Verificar explicitamente seu acesso no perfil de avaliação e bloqueio/proteção em produção.
- [Submissão PDF e abertura do repositório dependem de ações externas ao build] → Reservar tarefas finais de conferência e registrar evidências da submissão e acesso público.

## Migration Plan

1. Criar e validar a migração Flyway contra MySQL 8.4.
2. Executar `docker compose up --build` em ambiente limpo e validar seed, login, autorização, documentação e coleção.
3. Para ambiente descartável, parar os serviços e recriar somente o volume específico da avaliação; não remover dados de ambiente persistente.
4. Antes da entrega, conferir o repositório público, os artefatos e a submissão do relatório PDF na plataforma FIAP dentro do prazo.

## Open Questions

Nenhuma. O modelo do endereço, seus campos, perfis, autorização, banco, autenticação e entregáveis estão definidos.
