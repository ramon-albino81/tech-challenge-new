# Instruções do projeto

Estas instruções se aplicam a todo o repositório. Os requisitos detalhados e as decisões da implementação estão nos artefatos OpenSpec, em especial na change `implement-user-management-api`; em caso de conflito, não presuma uma decisão nova: identifique a divergência e peça esclarecimento.

## Contexto e decisões do projeto

- O projeto atende ao Tech Challenge FIAP Fase 1 e será desenvolvido por uma única pessoa.
- Stack definida: Java 21, Spring Boot 3.x, Maven Wrapper, MySQL 8.4/InnoDB, Flyway, Spring Security, JWT com Nimbus, BCrypt, OpenAPI/Swagger, coleção Postman, Docker multi-stage e Docker Compose.
- A aplicação será um monólito modular. Mantenha domínio e casos de uso independentes de Spring MVC, Spring Security, JPA e MySQL; use portas/adaptadores onde necessárias, injeção por construtor e DTOs da API separados das entidades de persistência.
- A API usa `/api/v1`, validação explícita e erros `ProblemDetail` compatíveis com RFC 7807.
- Os tipos de usuário são `DONO_RESTAURANTE` e `CLIENTE`. Não há cadastro público. O endereço é persistido como JSON MySQL com `street`, `number`, `city` e `postal_code`.
- JWTs são emitidos e validados com as bibliotecas Spring Security/Nimbus, não por lógica criptográfica manual. Senhas são armazenadas com BCrypt. Chaves e configurações sensíveis devem vir do ambiente, nunca do código-fonte.
- A conta de avaliação é criada por seed idempotente somente nos perfis autorizados de desenvolvimento/avaliação; não executar o seed em produção. As credenciais de avaliação documentadas não podem ser reutilizadas em produção.
- Escrever documentação e artefatos do projeto em português do Brasil. Preservar em inglês nomes de classes, identificadores, rotas, comandos, termos de protocolo e palavras-chave de ferramentas quando apropriado.
- Não adicionar comentários ao código: comentários de linha, de bloco, multilinha ou Javadoc são proibidos em código de produção, testes, scripts e migrações. Em arquivos de configuração, comentários só podem ser incluídos quando estritamente necessários para explicar uma configuração não óbvia.

## Skills obrigatórias

Antes de executar uma tarefa, carregue e aplique as skills pertinentes. Para tarefas que cruzem escopos, aplique todas as skills relevantes. Não alegue ter usado uma skill indisponível; comunique a limitação.

| Skill                     | Aplicação obrigatória                                                        |
| ------------------------- | ---------------------------------------------------------------------------- |
| `/clean-architecture`     | Camadas, limites, direção de dependências e portas/adaptadores               |
| `/solid-principles`       | Responsabilidades de módulos, serviços, interfaces e extensibilidade         |
| `/java-springboot`        | Código/configuração Spring, persistência, validação, segurança e testes Java |
| `/mysql`                  | Esquema, migrações, tipos, restrições, consultas e testes MySQL              |
| `/multi-stage-dockerfile` | Dockerfile, imagens e etapas de build/execução em contêiner                  |
| `/swagger-doc-creator`    | Endpoints, contratos HTTP e documentação OpenAPI/Swagger                     |
| `/conventional-commits`   | Preparar, revisar ou criar mensagens de commit                               |

## Implementação, testes e segurança

- Siga os contratos e regras de autorização das especificações OpenSpec; não altere senha ou tipo por atualização de perfil.
- Nunca exponha senha, hash, tokens, chaves, credenciais ou detalhes internos em respostas, logs ou exemplos destinados à produção.
- Use JUnit e Mockito para testes unitários de domínio/casos de uso e testes de integração/API para persistência, segurança e contratos. Para comportamento MySQL, prefira validar no MySQL real usado nos testes de integração.
- Execute a validação mais específica que cubra a alteração. Antes de concluir uma etapa, execute sua suíte pertinente; para a verificação completa, use `./mvnw verify` e valide o fluxo Docker de avaliação conforme as tarefas.
- Atualize documentação, exemplos Swagger e coleção Postman quando contratos ou comportamento observável mudarem.
- Não marque tarefas OpenSpec como concluídas sem implementação presente e evidência da verificação correspondente.
- Escreva código autoexplicativo sem comentários; se uma regra exigir explicação, prefira nomes claros, estrutura adequada ou documentação externa. Não use comentários como substituto para documentação do projeto.

## Docker e configuração

- O Dockerfile deve ser multi-stage, usar imagens fixadas, runtime mínimo compatível e usuário não root.
- O `docker-compose.yml` deve iniciar a aplicação e MySQL 8.4 com health check e configuração externa.
- Mantenha `.env.example` sem credenciais reais e explicitamente não produtivo. Não adicione `.env` real, chaves ou segredos ao Git.
- Verifique o ambiente de avaliação a partir de uma configuração limpa, incluindo prontidão do banco/aplicação, seed, login JWT e acesso aos endpoints protegidos.

## Fluxo Git individual

- Mantenha `main` estável. Prepare nela uma base mínima compilável com `.gitignore` adequado e crie `feature/tech-challenge` a partir de `main`.
- Crie uma branch `feature/<etapa>` por etapa, diretamente a partir de `feature/tech-challenge`. Abra o PR de cada etapa com `gh pr create --base feature/tech-challenge`; revise diff com `gh pr diff` e testes antes do merge com `gh pr merge`.
- Depois de abrir o PR de uma etapa, interrompa o avanço do trabalho e aguarde autorização humana expressa antes de iniciar o desenvolvimento de uma nova etapa. Alterações solicitadas no PR aberto continuam pertencendo à etapa atual.
- O PR para `main` é exclusivamente final: abra-o com `gh pr create --base main` somente depois de concluir e verificar todas as tarefas e arquivar as changes OpenSpec relacionadas. Inclua no PR resumo, evidências de `./mvnw verify`, fluxo Docker e checklist de entrega.
- Faça commits atômicos quando um incremento lógico estiver verificado e compilável; não acumule incrementos prontos sem commit ao fim de uma sessão e não crie commits artificiais por arquivo ou horário.
- Use Conventional Commits, com assunto imperativo de até 72 caracteres e em português sempre que claro. Não use mensagens genéricas nem reescreva histórico publicado com force-push.
- Configure autoria Git com a identidade real do único autor. Não invente coautores ou revisores; `git shortlog -sn` deve representar honestamente a autoria individual.
- Use o GitHub CLI (`gh`) para todas as operações que envolvam o repositório remoto: `gh pr create`, `gh pr list`, `gh pr diff`, `gh pr merge`, `gh pr status` e `gh repo view`. Operações locais (commit, branch, log, shortlog) continuam com `git`. Verifique autenticação com `gh auth status` antes de tarefas que interajam com o GitHub.
