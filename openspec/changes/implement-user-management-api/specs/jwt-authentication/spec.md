# Spec Delta

## Purpose

Permite a autenticação dos usuários por credenciais e o acesso a operações protegidas por tokens bearer assinados e com prazo de validade. Fornece ao avaliador uma conta de exemplo disponível em ambiente limpo, sem exigir cadastro público ou ação externa ao material entregue.

## ADDED Requirements

### Requirement: Usuários podem autenticar-se e receber JWT
O sistema SHALL disponibilizar login público que valide login e senha e, para credenciais válidas, retorne token bearer assinado e informação de expiração.

#### Scenario: Autenticar com credenciais válidas
- **WHEN** um solicitante envia login e senha válidos
- **THEN** o sistema retorna um JWT bearer e a respectiva expiração

#### Scenario: Rejeitar credenciais inválidas
- **WHEN** um solicitante envia login desconhecido ou senha incorreta
- **THEN** o sistema retorna `401 Unauthorized` com erro genérico, sem revelar qual credencial está incorreta

### Requirement: Operações protegidas exigem token válido
O sistema SHALL exigir JWT válido e não expirado em todas as operações de usuários e SHALL rejeitar token ausente, malformado, com assinatura inválida ou expirado.

#### Scenario: Chamar operação protegida sem token
- **WHEN** um solicitante chama operação protegida sem token bearer
- **THEN** o sistema retorna `401 Unauthorized` e não executa a operação

#### Scenario: Chamar operação protegida com token inválido ou expirado
- **WHEN** um solicitante usa JWT malformado, com assinatura inválida ou expirado
- **THEN** o sistema retorna `401 Unauthorized` e não executa a operação

#### Scenario: Chamar operação protegida com token válido
- **WHEN** um solicitante usa JWT válido
- **THEN** o sistema autentica o solicitante e processa a operação conforme suas permissões e validações

### Requirement: Cadastro público está desabilitado
O sistema SHALL NOT disponibilizar criação não autenticada de contas de usuário.

#### Scenario: Tentar criar conta sem autenticação
- **WHEN** solicitante não autenticado tenta criar uma conta
- **THEN** o sistema retorna `401 Unauthorized` e não cria usuário

### Requirement: Conta de avaliação está pronta para uso em ambiente limpo
O sistema SHALL criar automaticamente ao menos um usuário válido `DONO_RESTAURANTE` na inicialização de ambiente limpo de desenvolvimento/avaliação, sem passos manuais. As credenciais em texto claro SHALL constar no README e no relatório PDF, enquanto a senha armazenada no banco SHALL estar em hash. Essa conta SHALL poder acessar todos os endpoints protegidos destinados à avaliação.

#### Scenario: Iniciar ambiente limpo de avaliação
- **WHEN** a aplicação inicia pelo fluxo documentado em ambiente de avaliação com banco vazio
- **THEN** a conta de avaliação existe, a senha está armazenada como hash e as credenciais documentadas permitem obter um token

#### Scenario: Iniciar fora de desenvolvimento ou avaliação
- **WHEN** a aplicação inicia em produção ou em outro perfil não autorizado para seed
- **THEN** a conta de avaliação não é criada

#### Scenario: Reiniciar ambiente de avaliação
- **WHEN** a aplicação reinicia após a criação da conta de avaliação
- **THEN** não cria duplicata nem substitui silenciosamente a senha existente

#### Scenario: Usar a conta para testar endpoints protegidos
- **WHEN** o avaliador autentica com as credenciais documentadas e usa o JWT conforme a coleção Postman, Swagger/OpenAPI ou exemplo curl
- **THEN** consegue executar todos os endpoints protegidos incluídos no escopo de avaliação

### Requirement: Tipo e identificador do usuário são associados ao JWT
O sistema SHALL representar no JWT o identificador e o tipo do usuário autenticado e SHALL aplicar as permissões correspondentes a esse tipo.

#### Scenario: Autenticar qualquer tipo suportado
- **WHEN** usuário válido `DONO_RESTAURANTE` ou `CLIENTE` se autentica
- **THEN** o token identifica a conta e seu tipo e somente autoriza operações permitidas para esse tipo
