# Spec Delta

## Purpose

Permite que usuários autorizados do sistema de restaurantes gerenciem contas e perfis. Garante os campos obrigatórios definidos no desafio, a unicidade das contas, o tratamento seguro de senhas e a busca confiável de usuários.

## ADDED Requirements

### Requirement: Contas de usuário possuem os campos obrigatórios e tipos suportados
O sistema SHALL manter para cada usuário nome, e-mail único, login único, senha armazenada como hash, data/hora da última alteração, endereço e tipo `DONO_RESTAURANTE` ou `CLIENTE`. O endereço SHALL ser um objeto composto por rua, número, cidade e CEP. A data/hora da última alteração SHALL ser preenchida na criação e atualizada em cada alteração de dados ou senha.

#### Scenario: Criar um usuário com todos os dados obrigatórios
- **WHEN** um solicitante autenticado `DONO_RESTAURANTE` envia nome, e-mail, login, senha, endereço completo e tipo suportado
- **THEN** o sistema cria a conta, inicializa a data/hora da última alteração e retorna os dados de perfil sem expor a senha nem seu hash

#### Scenario: Rejeitar campos obrigatórios ausentes ou inválidos
- **WHEN** um solicitante envia um usuário sem qualquer campo obrigatório, com endereço incompleto ou com tipo não suportado
- **THEN** o sistema rejeita a solicitação como inválida e não cria a conta

### Requirement: E-mail e login são únicos
O sistema SHALL impedir que duas contas tenham o mesmo e-mail ou login normalizados, inclusive em tentativas concorrentes de criação ou atualização.

#### Scenario: Rejeitar e-mail ou login já utilizado
- **WHEN** um solicitante cria ou atualiza uma conta com e-mail ou login atribuído a outra conta
- **THEN** o sistema retorna `409 Conflict` e não altera as contas existentes

#### Scenario: Permitir que a conta mantenha seus próprios identificadores
- **WHEN** um solicitante atualiza a conta sem mudar e-mail ou login, ou envia os mesmos valores já atribuídos à própria conta
- **THEN** o sistema aceita a solicitação se as demais validações forem atendidas

### Requirement: Senhas são armazenadas e retornadas com segurança
O sistema SHALL armazenar senhas somente como hash unidirecional com salt e SHALL NOT incluir senha ou hash de senha nas respostas da API.

#### Scenario: Persistir senha de forma segura
- **WHEN** uma conta é criada ou sua senha é alterada
- **THEN** o sistema persiste um hash unidirecional e não o texto puro recebido

#### Scenario: Consultar ou buscar perfis
- **WHEN** um solicitante autorizado consulta ou busca usuários
- **THEN** nenhuma senha ou hash de senha é incluído nas respostas

### Requirement: Atualização de perfil e alteração de senha são operações distintas
O sistema SHALL disponibilizar operações distintas para atualizar os demais dados do usuário e para alterar senha. A operação de perfil SHALL NOT aceitar nem alterar senha. O tipo do usuário SHALL NOT ser alterável por atualização de perfil.

#### Scenario: Atualizar dados do perfil sem modificar senha
- **WHEN** um solicitante autorizado atualiza nome, e-mail, login ou endereço
- **THEN** o sistema atualiza os dados, preserva a senha e o tipo de usuário, e registra a nova data/hora da última alteração

#### Scenario: Rejeitar atualização de perfil com campos proibidos
- **WHEN** uma solicitação de atualização de perfil tenta alterar senha ou tipo de usuário
- **THEN** o sistema rejeita a solicitação e preserva esses valores

#### Scenario: Alterar a própria senha com senha atual válida
- **WHEN** um usuário autenticado envia sua senha atual correta e uma nova senha válida para a própria conta
- **THEN** o sistema armazena o hash da nova senha e atualiza a data/hora da última alteração

#### Scenario: Rejeitar senha atual incorreta
- **WHEN** um usuário envia uma senha atual incorreta
- **THEN** o sistema rejeita a alteração e preserva a senha existente

#### Scenario: Impedir alteração de senha de outra conta
- **WHEN** qualquer solicitante tenta alterar ou redefinir a senha de outro usuário
- **THEN** o sistema retorna `403 Forbidden` e preserva a senha dessa conta

### Requirement: Atribuição do tipo de usuário é restrita
O sistema SHALL permitir que somente um solicitante `DONO_RESTAURANTE` crie usuários e atribua, na criação, um dos tipos `DONO_RESTAURANTE` ou `CLIENTE`.

#### Scenario: Criar conta com tipo permitido
- **WHEN** um solicitante autenticado `DONO_RESTAURANTE` cria uma conta com um dos tipos suportados
- **THEN** o sistema cria a conta com o tipo solicitado

#### Scenario: Impedir criação de conta por cliente
- **WHEN** um solicitante `CLIENTE` tenta criar uma conta
- **THEN** o sistema retorna `403 Forbidden` e não cria usuário

### Requirement: Usuários podem ser consultados, buscados e excluídos conforme suas permissões
O sistema SHALL permitir que `DONO_RESTAURANTE` consulte, busque por nome, atualize o perfil e exclua qualquer usuário. Um `CLIENTE` SHALL poder consultar, atualizar o perfil e excluir somente a própria conta e SHALL NOT poder buscar usuários. Qualquer usuário pode alterar somente a própria senha.

#### Scenario: Buscar usuários por nome
- **WHEN** um solicitante autenticado `DONO_RESTAURANTE` envia uma busca por nome não vazia
- **THEN** o sistema retorna os perfis correspondentes, sem dados de senha, em resultados paginados e limitados

#### Scenario: Impedir que cliente busque usuários
- **WHEN** um solicitante autenticado `CLIENTE` tenta buscar usuários
- **THEN** o sistema retorna `403 Forbidden` e não revela perfis

#### Scenario: Consultar uma conta inexistente
- **WHEN** um solicitante autorizado consulta um identificador inexistente
- **THEN** o sistema retorna `404 Not Found`

#### Scenario: Excluir conta existente
- **WHEN** um solicitante `DONO_RESTAURANTE` exclui qualquer conta existente ou um solicitante `CLIENTE` exclui a própria conta
- **THEN** o sistema exclui a conta e confirma a conclusão

#### Scenario: Excluir conta inexistente
- **WHEN** um solicitante autorizado exclui um identificador inexistente
- **THEN** o sistema retorna `404 Not Found`

#### Scenario: Impedir cliente de acessar conta alheia
- **WHEN** um solicitante `CLIENTE` tenta consultar, atualizar ou excluir a conta de outra pessoa
- **THEN** o sistema retorna `403 Forbidden` e não altera nem revela a conta alheia
