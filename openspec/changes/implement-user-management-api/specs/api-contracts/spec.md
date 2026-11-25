# Spec Delta

## Purpose

Define as rotas HTTP versionadas, os formatos de resposta e os materiais de documentação e teste necessários para que a API possa ser utilizada e avaliada de forma reproduzível.

## ADDED Requirements

### Requirement: Rotas da API são versionadas e explícitas
O sistema SHALL expor as operações sob `/api/v1`, com os seguintes contratos:
- `POST /api/v1/auth/login`: login público; `200 OK` para credenciais válidas e `401 Unauthorized` para inválidas.
- `POST /api/v1/users`: criação autenticada por `DONO_RESTAURANTE`; `201 Created` com header `Location: /api/v1/users/{id}` apontando para a conta criada.
- `GET /api/v1/users/{id}`: consulta; `200 OK` quando encontrada.
- `GET /api/v1/users?name={nome}&page={pagina}&size={tamanho}`: busca paginada por nome, disponível a `DONO_RESTAURANTE`, com `size` máximo de `50` (retornando `400 Bad Request` para valores inválidos ou acima do limite).
- `PUT /api/v1/users/{id}`: atualização dos demais dados, sem senha nem tipo de usuário; `200 OK`.
- `PUT /api/v1/users/{id}/password`: alteração exclusiva da própria senha mediante senha atual; `204 No Content`.
- `DELETE /api/v1/users/{id}`: exclusão autorizada; `204 No Content`.

#### Scenario: Chamar rota da versão suportada
- **WHEN** um solicitante chama uma rota documentada sob `/api/v1`
- **THEN** o sistema processa a solicitação conforme o método, entrada, autorização e resposta definidos para a rota

#### Scenario: Chamar versão não suportada
- **WHEN** um solicitante chama uma rota de versão não suportada
- **THEN** o sistema retorna erro de cliente no formato ProblemDetail

### Requirement: Erros da API usam ProblemDetail e códigos HTTP apropriados
O sistema SHALL retornar erros no formato `application/problem+json`, compatível com ProblemDetail da RFC 7807, incluindo status HTTP e explicação estável, não sensível.

#### Scenario: Rejeitar entrada inválida
- **WHEN** um solicitante envia corpo malformado, campo obrigatório ausente ou dado inválido
- **THEN** o sistema retorna `400 Bad Request` com ProblemDetail que identifica a falha sem revelar informações internas

#### Scenario: Rejeitar autenticação inválida ou ausente
- **WHEN** o login é inválido ou um endpoint protegido é chamado sem autenticação/token válido
- **THEN** o sistema retorna `401 Unauthorized` com ProblemDetail

#### Scenario: Rejeitar solicitante autenticado sem permissão
- **WHEN** usuário autenticado tenta operação não permitida para seu tipo ou sua própria conta
- **THEN** o sistema retorna `403 Forbidden` com ProblemDetail

#### Scenario: Informar recurso inexistente
- **WHEN** uma operação autorizada consulta, atualiza ou exclui usuário inexistente
- **THEN** o sistema retorna `404 Not Found` com ProblemDetail

#### Scenario: Informar conflito de unicidade
- **WHEN** e-mail ou login conflita com outra conta
- **THEN** o sistema retorna `409 Conflict` com ProblemDetail

#### Scenario: Não expor detalhes internos em falhas inesperadas
- **WHEN** ocorre falha interna não prevista
- **THEN** o sistema retorna `500 Internal Server Error` genérico, sem SQL, credenciais, stack trace, conteúdo de token ou detalhes de implementação

### Requirement: OpenAPI/Swagger documenta operações e exemplos
O sistema SHALL publicar OpenAPI/Swagger para todas as rotas versionadas, com exemplos de requisição e resposta de sucesso e erro, incluindo `400`, `401`, `403`, `404` e `409`, e indicar quais operações requerem bearer JWT.

#### Scenario: Consultar documentação no ambiente de avaliação
- **WHEN** o avaliador abre Swagger UI ou o documento OpenAPI no ambiente de avaliação
- **THEN** encontra rotas, campos de entrada/saída, autorização necessária e exemplos de sucesso e erro

#### Scenario: Usar login e token documentados
- **WHEN** o avaliador segue o fluxo de login e uso do bearer token indicado na documentação
- **THEN** consegue chamar as operações protegidas às quais a conta de avaliação tem acesso

### Requirement: Coleção Postman e environment cobrem os fluxos do desafio
O repositório SHALL incluir `postman/collection.json` e `postman/environment.json`, com variável `baseUrl` e testes que verifiquem os status HTTP esperados.

#### Scenario: Executar cenários principais da coleção
- **WHEN** a coleção Postman é importada e executada contra o ambiente configurado
- **THEN** cobre criação válida, e-mail/login duplicados, campos obrigatórios ausentes, troca de senha com sucesso e erro, atualização de perfil com sucesso e erro, busca por nome e validação de login

#### Scenario: Executar fluxo JWT da coleção
- **WHEN** o avaliador executa login e chamadas protegidas na coleção
- **THEN** a coleção obtém e reutiliza o token, verifica sucesso e cobre respostas de autenticação/autorização negadas
