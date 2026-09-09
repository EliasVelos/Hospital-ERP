# Hospital-ERP

Aplicação acadêmica para praticar o desenvolvimento de um sistema hospitalar com Java e Spring Boot. Reúne cadastros de pacientes e profissionais, consultas, atendimentos, internações, leitos e movimentação de medicamentos.

O projeto está em revisão. Serve para estudo e demonstração com dados fictícios; ainda não está preparado para operar um hospital ou armazenar prontuários reais.

## Como o projeto está organizado

- `controller`: recebe as requisições e prepara as telas Thymeleaf.
- `service`: coordena cadastros, consultas e operações do hospital.
- `repository` e `entity`: persistência com Spring Data JPA.
- `security`: autenticação e autorização centralizadas.
- `src/test`: testes de acesso, senhas e telas afetadas pela revisão.

Java 17, Spring Boot 3.3.5, Thymeleaf, Spring Security e PostgreSQL. O Maven Wrapper está incluído. A versão do Spring Boot foi preservada nesta etapa; a atualização das dependências exige uma revisão separada.

## Executar localmente

Tenha um PostgreSQL local com um banco vazio chamado `hospital`. Os comandos abaixo são para **dados de demonstração**. O perfil `dev` permite ao Hibernate criar/atualizar as tabelas; os demais ambientes usam `validate`.

Linux/macOS:

```bash
export SPRING_PROFILES_ACTIVE=dev
export DATABASE_URL=jdbc:postgresql://localhost:5432/hospital
export PGUSER=postgres
read -rs -p 'Senha do PostgreSQL: ' PGPASSWORD; export PGPASSWORD; echo
export HOSPITAL_BOOTSTRAP_ENABLED=true
export HOSPITAL_ADMIN_USERNAME=admin
read -rs -p 'Nova senha do administrador (mínimo 12 caracteres): ' HOSPITAL_ADMIN_PASSWORD; export HOSPITAL_ADMIN_PASSWORD; echo
bash ./mvnw spring-boot:run
```

PowerShell:

```powershell
$env:SPRING_PROFILES_ACTIVE = 'dev'
$env:DATABASE_URL = 'jdbc:postgresql://localhost:5432/hospital'
$env:PGUSER = 'postgres'
$env:PGPASSWORD = [System.Net.NetworkCredential]::new('', (Read-Host 'Senha do PostgreSQL' -AsSecureString)).Password
$env:HOSPITAL_BOOTSTRAP_ENABLED = 'true'
$env:HOSPITAL_ADMIN_USERNAME = 'admin'
$env:HOSPITAL_ADMIN_PASSWORD = [System.Net.NetworkCredential]::new('', (Read-Host 'Nova senha do administrador (mínimo 12 caracteres)' -AsSecureString)).Password
.\mvnw.cmd spring-boot:run
```

Abra `http://localhost:8080/login`. A conta inicial só é criada se não houver usuários. Não existe senha padrão. Após criá-la, desative `HOSPITAL_BOOTSTRAP_ENABLED` e remova a variável `HOSPITAL_ADMIN_PASSWORD` do ambiente.

O `.env.example` é uma referência das variáveis; o Spring Boot não carrega um arquivo `.env` automaticamente. A senha aceita no cadastro deve ter pelo menos 12 caracteres e no máximo 72 bytes em UTF-8, limite do BCrypt.

## Perfis de acesso

O perfil vem do cadastro no servidor. O login pede apenas usuário e senha; CPF não é utilizado como segundo fator.

| Perfil | Acesso nesta versão |
|---|---|
| Administrador | Cadastros, contas, operação, relatórios e atendimentos. |
| Funcionário | Pacientes, agendamentos, internações, leitos e medicamentos. Não administra contas de funcionários/médicos nem acessa os detalhes clínicos. |
| Médico | Sua própria agenda e os atendimentos das consultas atribuídas a ele. |
| Paciente | Sua página inicial, dados cadastrais e consultas. |

Pacientes e profissionais são cadastrados pelas áreas autorizadas. O cadastro de médicos/funcionários fica restrito ao administrador. Uma conta desses perfis precisa estar vinculada ao respectivo cadastro para entrar.

## Segurança implementada nesta etapa

- BCrypt em todos os fluxos de criação e alteração de senha.
- Spring Security para login, sessão e permissões por rota.
- Proteção CSRF nos formulários; exclusão, alta e logout usam POST.
- Identidade autenticada usada para consultar dados do paciente e do médico.
- Verificação de vínculo antes de ler ou gravar um atendimento.
- Campos de acesso e registro clínico limitados no recebimento dos formulários.
- Edição de usuário preserva relacionamentos e nunca preenche o formulário com o hash da senha.
- Inicialização administrativa opcional, sem senha fixa ou credenciais nos logs.

## Atualizar uma base existente

**Faça backup e valide a atualização numa cópia da base antes de usar os dados originais.** Nenhum banco remoto é migrado por este repositório automaticamente.

O login não aceita as senhas em texto puro da versão anterior. Para uma base pequena de demonstração, há uma conversão pontual habilitada por `HOSPITAL_MIGRATE_LEGACY_PASSWORDS=true`. Ela converte os valores antigos em BCrypt numa transação e preserva os hashes BCrypt existentes. Depois da execução, remova a variável. Não há fallback para autenticação em texto puro.

Essa conversão preserva a senha escolhida anteriormente: ela **não corrige senhas fracas ou já expostas**. Redefina essas senhas, especialmente a antiga conta inicial, antes de disponibilizar o sistema. Valores vazios ou senhas que excedam o limite do BCrypt precisam ser corrigidos antes da conversão. Para uma base real, planeje redefinição de credenciais em vez de simplesmente reaproveitá-las.

Fora de `dev`, a aplicação exige que as tabelas já existam e usa cookie de sessão seguro, destinado a HTTPS. Não foi introduzido um baseline Flyway sobre um banco desconhecido; o versionamento completo do esquema permanece pendente.

## Testes

```bash
bash ./mvnw -B verify
```

No Windows: `.\mvnw.cmd -B verify`.

Os testes usam H2 isolado e não precisam das credenciais do PostgreSQL. Cobrem autenticação, recusa de senha em texto puro, restrições entre perfis, isolamento de atendimentos, CSRF, logout, criação de contas e renderização das telas alteradas. O GitHub Actions executa os testes e empacota a aplicação em cada alteração.

H2 não substitui testes de integração com PostgreSQL nem testes concorrentes.

## Próximos problemas a resolver

- Versionar o esquema com migrations verificadas contra a base existente.
- Revisar a política de perfis com quem operará cada módulo; hoje `FUNCIONARIO` é um perfil amplo.
- Tratar concorrência de consultas, leitos, alta e estoque; ampliar a auditoria das alterações.
- Adicionar redefinição de senha, limitação de tentativas de login e revogação de sessões após mudanças de perfil.
- Revisar validação dos campos, erros dos demais módulos e dependências.
- Corrigir o cadastro de CRM, que ainda gera um número demonstrativo; ele não verifica registro profissional.
- Revisar responsividade, acessibilidade e navegação das telas legadas.

## Contexto e autoria

Projeto de Arthur Amancio Francisco. O histórico do Git registra as contribuições e as revisões. As funcionalidades e limitações acima descrevem o estado do código, sem atribuir uso real em hospitais.
