# Proposta

## Why

O desafio da Fase 1 exige um backend Spring Boot para gestão de usuários, com autenticação, persistência relacional e documentação suficiente para avaliação. Este change transforma os critérios do PDF — incluindo os entregáveis, o histórico Git, a execução Docker e o fluxo de avaliação com JWT — em requisitos e tarefas verificáveis.

## What Changes

- Implementar cadastro administrativo, consulta, busca por nome, atualização de perfil, alteração de senha e exclusão de usuários, com os perfis `DONO_RESTAURANTE` e `CLIENTE`.
- Persistir todos os campos obrigatórios, com e-mail único, login único, senha com hash e data/hora da última alteração.
- Adicionar login JWT e proteger as operações, sem cadastro público, disponibilizando conta de avaliação automaticamente somente em ambiente de desenvolvimento/avaliação.
- Padronizar validações, códigos HTTP e erros no formato `ProblemDetail` compatível com RFC 7807; documentar contratos e exemplos de sucesso e erro no OpenAPI/Swagger.
- Preparar persistência MySQL, migrações, `Dockerfile` multi-stage, `docker-compose.yml`, `.env.example`, estrutura sugerida do repositório, coleção e environment Postman.
- Persistir o endereço como coluna MySQL `JSON` contendo `street`, `number`, `city` e `postal_code`.
- Incluir testes automatizados, README, relatório técnico PDF, evidências de histórico Git e conferência integral do checklist de entrega.
- Exigir que o agente consulte e aplique as skills de arquitetura, Java/Spring Boot, MySQL, SOLID, Docker multi-stage, Swagger e Conventional Commits conforme cada tarefa.

## Capabilities

### New Capabilities

- `user-management`: campos obrigatórios, tipos, permissões, ciclo de vida, alterações de perfil e senha, unicidade e busca.
- `jwt-authentication`: validação de credenciais, emissão e validação de tokens, proteção da API e conta de avaliação.
- `api-contracts`: rotas e respostas versionadas, validação, `ProblemDetail`, OpenAPI e materiais de teste da API.

### Modified Capabilities

Nenhuma.

## Impact

- Nova aplicação Java/Spring Boot e seus testes, sem código de aplicação preexistente.
- Banco MySQL e migrações; API `/api/v1`; autenticação e autorização JWT; arquivos Docker e configuração por variáveis de ambiente.
- Entregáveis previstos: `src/`, `postman/collection.json`, `postman/environment.json`, `docs/`, `Dockerfile`, `docker-compose.yml`, `.env.example`, `.gitignore` e `README.md`.
- Relatório técnico com link do repositório público e evidências exigidas, a ser submetido em PDF na plataforma FIAP dentro do prazo informado na plataforma.
