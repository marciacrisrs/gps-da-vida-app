# Como entregamos o app

`main` permanece compilável. Cada issue vira um branch `feat/<n>-slug`, um PR e o fechamento da issue no merge.

## Ordem

1. **#18** — esqueleto (feito)
2. **#1** — modelo de domínio (feito)
3. Cadastros **#2–#8** (eventos → tarefas → hábitos → rotinas → disponibilidade → prioridade → duração)
4. Motor **#9–#12** e **#16** (regras, agenda, “agora”, recálculo, atraso)
5. UI **#13–#15** (Agora, Meu Dia, conclusão)

Não pular o motor para desenhar telas “de mentira”: placeholders de navegação na #18 bastam.

## Regras

- Um PR ≈ uma issue. Dependência explícita na descrição.
- Código e pacotes em inglês; strings da UI em português.
- `domain` sem Android; `ui` sem Room; regras de agenda só em `domain/planning`.
- Skill nova: propor → Márcia aceita → só então gravar `SKILL.md`.
- Commits e PRs: o agente pode abrir, revisar e mergear. Márcia só entra nos **gates humanos** abaixo.

## Gates humanos

Avisar Márcia quando for preciso:

- Rodar o app no emulador/aparelho e validar visualmente (primeiro: após #18)
- Conta Google / Play Console / keystore de release
- Decisão de produto que mude o ADR (backend, sync, XML, multi-módulo)
- Screenshots ou copy final das telas

Arquitetura: [ADR 001](adr/001-arquitetura-inicial.md).
