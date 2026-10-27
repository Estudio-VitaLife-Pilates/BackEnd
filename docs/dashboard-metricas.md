# Métricas da Dashboard (Visão Geral)

Este documento descreve, para cada número exibido na tela "Visão Geral" do frontend, de onde ele vem e como é calculado no backend.

Toda a lógica está centralizada em `MetricasService` (`src/main/java/com/pilates/thais/almeida/service/MetricasService.java`), exposta via `MetricasController` sob `/dashboard/*`. Datas de "mês" e "semana" sempre usam `LocalDate.now()` do servidor como referência, salvo quando o endpoint aceita o parâmetro `mes` explicitamente.

---

## `GET /dashboard/kpis` — cards do topo

| Campo (DTO) | Card na tela | Cálculo |
|---|---|---|
| `totalAlunos` | **Total de alunos** | `AlunoRepository.countByAtivoTrue()` — conta todos os alunos com `ativo = true`, **independente de terem ou não um plano ativo**. A legenda do card é "ativos no sistema" exatamente por isso. |
| `taxaOcupacao` | **Taxa de ocupação** | Para cada turma ativa (`Turma.ativa = true`), calcula `matriculados / capacidadeMax`; o resultado é a **média simples** dessas razões entre todas as turmas ativas (turmas sem `capacidadeMax` ou com capacidade 0 são ignoradas). "Matriculados" vem de `AlunoTurmaRepository.countMatriculadosPorTurma()`, que conta vínculos `AlunoTurma.ativo = true`. |
| `aulasSemana` | **Aulas esta semana** | `aulasProgramadasSemana + aulasCanceladasSemana` da semana atual (segunda a domingo, por `dataAula`). |
| `aulasProgramadasSemana` | legenda "de X programadas" | `AulaRepository.countByDataAulaBetweenAndMarcadaTrue` na semana atual — conta **sessões de aula** (`Aula`, não alunos) com `marcada = true`. |
| `aulasCanceladasSemana` | (usado só para somar `aulasSemana`) | `AulaRepository.countByDataAulaBetweenAndMarcadaFalse` na semana atual — sessões inteiras com `marcada = false`. **Atenção:** não existe, hoje, nenhum botão na UI que desative uma aula inteira (`PUT /aulas/{id}/desativar`); esse número é sempre 0 na prática. |
| `taxaRetencao` | **Taxa de retenção** | `1 - taxaSaidaMes` (ver abaixo). Se não havia nenhum aluno com plano ativo no início do mês, `taxaSaidaMes` é `0.0` e a retenção fica em `100%` (não em `0%`) — ver nota de design abaixo. |
| `taxaSaidaMes` | legenda "X% saída este mês" | `desmatriculadosMes / ativosInicioMes`, onde `ativosInicioMes = AlunoPlanoRepository.countAtivosNaData(inicioMes)` (quantos `AlunoPlano` estavam ativos no dia 1º do mês) e `desmatriculadosMes = AlunoPlanoRepository.countByAtivoFalseAndDataFimBetween(inicioMes, fimMes)` (planos que viraram inativos com `dataFim` dentro do mês). Se `ativosInicioMes == 0`, o resultado é `0.0` (sem base para medir saída). |
| `variacaoOcupacaoMes` | *(não exibido)* | Hardcoded `0.0` — não existe snapshot histórico de ocupação por mês no schema atual. |

### Nota de design: por que `taxaRetencao` não é simplesmente `1 - taxaSaidaMes` em todos os casos

Quando `ativosInicioMes == 0` (ninguém tinha plano ativo no início do mês — típico logo após o studio começar a usar o sistema, ou após um reset de dados), a fórmula antiga zerava **as duas** taxas (`taxaSaidaMes = 0.0` e `taxaRetencao = 0.0`), o que produzia o texto contraditório "Taxa de retenção: 0% / 0% saída este mês". Hoje `taxaRetencao` é sempre `1 - taxaSaidaMes`, então nesse caso de borda ela aparece como `100%` (ninguém saiu = retenção plena), que é a leitura correta quando não há evidência de perda de aluno.

---

## `GET /dashboard/alertas` — os 3 banners (amarelo, vermelho, verde)

| Campo (DTO) | Banner | Cálculo |
|---|---|---|
| `alunosParaReagendamento` | 🟡 **Alunos para reagendamento** | `AulaAlunoRepository.countAguardandoReagendamento()`: conta `AulaAluno` com `status = 'AUSENTE'` que **não** têm nenhum outro `AulaAluno` com `status = 'REPOSICAO'` cujo `aulaOrigem` aponte de volta pra essa aula/aluno. Ou seja: faltas que ainda não foram remarcadas. Um aluno é marcado como ausente pela ação "Marcar falta" em `TurmaEspecifica` (endpoint `PUT /aulas/alunos/{id}/ausente`). |
| `aulasCanceladasMes` | 🔴 **Aulas canceladas no mês** | `AulaAlunoRepository.countFaltasNoPeriodo(inicioMes, fimMes)`: conta `AulaAluno` cuja **aula de origem** (`aula.dataAula`) caiu neste mês, e que ou (a) ainda estão com `status = 'AUSENTE'`, ou (b) já têm uma `REPOSICAO` vinculada. Isto é: toda falta de aluno que aconteceu neste mês, resolvida ou não. |
| `aulasRemarcadasMes` | legenda "X foram remarcadas (Y%)" | `AulaAlunoRepository.countFaltasRemarcadasNoPeriodo(inicioMes, fimMes)`: subconjunto do anterior — quantas dessas faltas de origem neste mês já têm uma `REPOSICAO` vinculada. O percentual (`Y%`) é calculado **no frontend** (`VisaoGeral.jsx`) como `aulasRemarcadasMes / aulasCanceladasMes`. |
| `novosAlunosMes` | 🟢 **Novos alunos este mês** | `AlunoRepository.countByDataCadastroBetween(inicioMes, fimMes)`. |
| `desmatriculasMes` | legenda "X desmatrícula(s) no período" | `AlunoPlanoRepository.countByAtivoFalseAndDataFimBetween(inicioMes, fimMes)` — mesma base usada em `taxaSaidaMes`. |

### Nota de design: por que "canceladas" e "remarcadas" usam `AulaAluno`, não `Aula`

Antes da correção, `aulasCanceladasMes` contava **sessões inteiras** (`Aula.marcada = false`) enquanto `aulasRemarcadasMes` contava **reposições de alunos individuais** (`AulaAluno.status = 'REPOSICAO'`) filtradas pela data de **destino** da reposição — duas populações sem relação causal entre si, e a segunda nem sequer tinha como aparecer vinculada à primeira (desativar uma aula inteira não tem UI, e uma reposição nasce de uma falta individual, não de uma sessão cancelada). O resultado prático era sempre "0 canceladas, N remarcadas (0%)", sem fazer sentido. A correção faz as duas métricas usarem a mesma origem (faltas de aluno) e a mesma referência de data (a aula em que a falta ocorreu, não a aula de destino da reposição).

---

## `GET /dashboard/desempenho?periodo=mensal|semanal` — gráfico de barras

Série de `{ periodo, aulasRealizadas, aulasCanceladas }`, as duas sempre no nível de **aluno** (`AulaAluno`), para serem diretamente comparáveis na mesma barra:

- **Mensal** (padrão): últimos 6 meses (incluindo o atual). Para cada mês, `aulasRealizadas` = `AulaAlunoRepository.countByStatusAndAula_DataAulaBetween("REALIZADA", inicioMes, fimMes)` e `aulasCanceladas` = `countFaltasNoPeriodo` (mesma query do banner de alertas e do card "Métricas de aulas").
- **Semanal**: últimas 8 semanas (segunda a domingo). Mesmas duas queries, por semana.

### Nota de design: por que as duas pontas do gráfico usam `AulaAluno`, não `Aula`

Antes da correção, `aulasRealizadas` contava **sessões de turma** (`Aula.marcada = true`) enquanto `aulasCanceladas` contava **faltas de alunos individuais**. Numa turma de 5 alunos onde 2 faltam, isso virava "+1 realizada, +2 canceladas" — os dois números descreviam populações diferentes (uma sessão vs. presenças individuais) e não eram comparáveis entre si no mesmo gráfico, o que tornava a leitura enganosa. A correção trocou `aulasRealizadas` para contar `AulaAluno.status = 'REALIZADA'` (setado automaticamente pelo `PresencaScheduler` para aulas passadas que continuavam `AGENDADO`), deixando as duas séries na mesma granularidade: presenças confirmadas vs. faltas, por aluno.

Isso é diferente do que o card "Métricas de aulas" faz com `aulasRealizadas`/`aulasProgramadas` (ver abaixo) — lá a pergunta é "quantas das minhas turmas aconteceram de fato?" (nível de sessão), uma pergunta legítima e distinta da que o gráfico responde.

O texto de alerta abaixo do gráfico ("Atenção: X teve o maior pico de cancelamentos...") é calculado **no frontend**, a partir desses mesmos dados.

---

## `GET /dashboard/alunos-recentes?limit=5` — lista "Alunos recentes"

`AlunoRepository.findTop5ByOrderByDataCadastroDesc()`, limitado a `limit` (padrão 5). Para cada aluno, busca o primeiro `AlunoPlano` com `ativo = true` na lista já carregada em memória (`aluno.getAlunoPlanos()`) e expõe `nomePlano`/`frequenciaSemanal` desse plano (ou `null` se o aluno não tiver plano ativo — por isso alguns alunos aparecem com `—` na coluna de plano).

---

## `GET /dashboard/aulas-hoje?periodo=hoje|semana` — lista "Aulas de hoje"

Lista `Aula` do dia (`AulaRepository.findAllByDataAula(hoje)`) ou da semana atual (`findAllByDataAulaBetween`), mapeadas com horário/professor/dia da turma e `totalAlunos = aula.getAlunos().size()` (todos os vínculos `AulaAluno` daquela aula, **sem filtrar por status** — inclui cancelados/ausentes na contagem de alunos exibida).

---

## `GET /dashboard/metricas-alunos?mes=yyyy-MM` — card "Métricas de alunos"

Aceita um mês explícito (padrão: mês atual). Compara sempre com o mês anterior:

| Campo | Cálculo |
|---|---|
| `novosAlunos` / `variacaoNovosAlunos` | `AlunoRepository.countByDataCadastroBetween` no mês vs. no mês anterior; variação = diferença simples. |
| `desmatriculados` / `variacaoDesmatriculados` | `AlunoPlanoRepository.countByAtivoFalseAndDataFimBetween` no mês vs. no mês anterior. |
| `paraReagendamento` | Mesmo `countAguardandoReagendamento()` do banner de alertas — **não é filtrado pelo mês pedido**, é sempre o total pendente atual. |
| `taxaSaida` | Mesma fórmula de `taxaSaidaMes` dos KPIs, mas para o mês pedido: `desmatriculados / ativosInicioMes` (0 se não havia base). |

---

## `GET /dashboard/metricas-aulas?mes=yyyy-MM` — card "Métricas de aulas"

| Campo | Cálculo |
|---|---|
| `aulasRealizadas` | `AulaRepository.countByDataAulaBetweenAndMarcadaTrue` no mês — nível de **sessão**. |
| `aulasProgramadas` | `aulasRealizadas + AulaRepository.countByDataAulaBetweenAndMarcadaFalse` no mês — também nível de sessão; é o denominador de "X de Y programadas". |
| `aulasCanceladas` | `AulaAlunoRepository.countFaltasNoPeriodo` no mês — nível de **aluno** (mesma base do banner de alertas). |
| `aulasRemarcadas` | `AulaAlunoRepository.countFaltasRemarcadasNoPeriodo` no mês — nível de aluno. |
| `taxaRemarcacao` | `aulasRemarcadas / aulasCanceladas` (0 se não houve nenhuma falta no mês). **Não** usa `aulasProgramadas` como denominador — antes da correção usava, o que diluía a taxa (dividia remarcações de alunos pelo total de sessões do mês, uma base ~5x maior e sem relação direta) e fazia a meta "acima de 60%" praticamente impossível de bater. |

### Por que `aulasRealizadas`/`aulasProgramadas` continuam em nível de sessão, mas `aulasCanceladas`/`aulasRemarcadas` são em nível de aluno

São duas perguntas diferentes que esse card responde ao mesmo tempo: "quantas das minhas turmas aconteceram de fato?" (sessão) e "dos alunos que faltaram, quantos eu já reagendei?" (aluno). Misturar as duas no mesmo denominador é que causava o problema original.

---

## Resumo das queries novas (pós-correção)

Adicionadas em `AulaAlunoRepository`:

```java
// Faltas cuja aula de origem caiu no período — pendentes ou já remarcadas
long countFaltasNoPeriodo(LocalDate inicio, LocalDate fim);

// Subconjunto das acima que já têm uma REPOSICAO vinculada
long countFaltasRemarcadasNoPeriodo(LocalDate inicio, LocalDate fim);
```

Ambas usam a mesma relação que `countAguardandoReagendamento()` já usava (`AulaAluno.aulaOrigem` apontando de volta pra aula/aluno de uma reposição), só que parametrizadas por intervalo de datas e sem o filtro fixo em `status = 'AUSENTE'`.

---

## Funcionalidades incompletas que afetam estas métricas

1. **Marcar falta** (`AulaAluno.status = 'AUSENTE'`) — implementado em `feature/marcar-falta-aluno` (endpoint `PUT /aulas/alunos/{id}/ausente` + botão em `TurmaEspecifica`). Antes dessa branch, nada no sistema definia esse status, então `alunosParaReagendamento`, `aulasCanceladasMes` e `aulasRemarcadas` ficavam sempre em 0.
2. **Desativar aula inteira** (`Aula.marcada = false`, endpoint `PUT /aulas/{id}/desativar`) — existe no backend desde sempre, mas **não tem nenhum botão no frontend**. Isso significa que `aulasCanceladasSemana` (KPI "aulas esta semana") e `aulasProgramadas` do card "Métricas de aulas" tendem a ficar sempre artificialmente baixos/zerados na prática, mesmo sem ser um bug de cálculo — é simplesmente um recurso que o studio não tem como acionar pela tela ainda. Isso **não afeta mais** a série "Canceladas" do gráfico de desempenho nem `aulasCanceladas` do card de métricas, que hoje usam `AulaAluno` (faltas por aluno), independente desse campo.

## Status `REALIZADA`: presença confirmada por padrão

Até aqui, `AulaAluno.status` ficava em `AGENDADO` para sempre depois que a data da aula passava, a menos que alguém tomasse uma ação manual (marcar falta, cancelar, remarcar). Não havia como saber, olhando o status, se o aluno realmente compareceu ou se simplesmente ninguém mexeu no registro.

Agora existe um quinto status, `REALIZADA` ("presença confirmada"), e um job (`PresencaScheduler`, roda uma vez ao iniciar a aplicação e todo dia à 00:05) que varre `AulaAluno` com `status = 'AGENDADO'` e `aula.dataAula < hoje`, e marca como `REALIZADA` — presença é o padrão assumido quando ninguém registrou uma falta/cancelamento/reposição.

Pontos importantes:
- A correção continua possível depois do fato: o botão "Marcar falta" em `TurmaEspecifica` aceita tanto `AGENDADO` quanto `REALIZADA` como estado de partida, então dá pra reverter um "presença assumida" pra "ausente" mesmo depois do job já ter rodado.
- `REALIZADA` conta como ocupante normal em `countOcupantesAtivosDaAula` (mesma regra de antes: tudo que não é `CANCELADA` ocupa vaga) e não interfere nas queries de falta/reagendamento (que olham especificamente para `AUSENTE`).
- Isso é um status por **aluno**, independente do `Aula.marcada` (sessão inteira) usado por `aulasRealizadas`/`aulasProgramadas` do card "Métricas de aulas" — os dois não são a mesma coisa (ver nota do item 2 acima). Já o gráfico de desempenho usa esse status `REALIZADA` diretamente (ver seção do gráfico, acima).
