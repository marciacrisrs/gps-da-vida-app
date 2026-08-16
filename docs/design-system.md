# GPS da Vida — Design System

## Direção

O GPS da Vida combina **planner editorial** com **GPS pessoal**.

A interface deve parecer uma página de planner digital bem cuidada, não um dashboard corporativo. O visual precisa transmitir calma, clareza e orientação: o app organiza a vida sem parecer que está cobrando a pessoa.

A referência é uma inspiração de linguagem, não um modelo a ser copiado literalmente.

## Princípios visuais

1. **Muito respiro** — espaço em branco é parte da composição.
2. **Poucos elementos por tela** — cada tela deve ter uma ação ou informação dominante.
3. **Cards grandes e arredondados** — agrupar informação sem criar uma grade densa.
4. **Editorial, não corporativo** — evitar excesso de linhas, tabelas, chips e dashboards.
5. **Orgânico com moderação** — blobs, círculos e linhas desenhadas entram como detalhe, nunca como ruído.
6. **Ação principal evidente** — o GPS deve deixar claro o que importa agora.
7. **Cor com função** — cor ajuda a orientar, não serve para decorar tudo.

## Paleta

| Token | Hex | Uso |
|---|---|---|
| Canvas | `#FBF7F4` | fundo principal |
| Surface | `#FFFDFC` | cards e superfícies |
| SurfaceWarm | `#F7EEEA` | superfícies secundárias |
| Ink | `#2E2927` | texto principal |
| InkSoft | `#625A56` | texto secundário |
| Outline | `#D9CDC7` | bordas e divisores |
| Terracotta | `#B9655F` | ação principal e navegação |
| TerracottaSoft | `#E9C5C0` | containers de destaque |
| Rose | `#D88E95` | acento editorial |
| RoseSoft | `#F1D8DA` | fundo de apoio |
| Sage | `#A7B3A4` | contexto secundário |
| SageSoft | `#DCE4D9` | container de contexto |
| BlueGray | `#8C9AA7` | informação neutra/contextual |
| BlueGraySoft | `#DCE2E7` | container neutro |

### Regra de proporção

- Neutros dominam a interface.
- Terracotta é o principal acento funcional.
- Rose, sage e blue-gray são secundários.
- Não usar todas as cores em uma mesma tela sem necessidade.

## Tipografia

A tipografia usa `SansSerif` do sistema para manter o app leve e consistente.

- Display: 34sp / bold
- Headline: 28sp / bold
- Section headline: 24sp / bold
- Title: 20sp / semibold
- Body: 16sp / 24sp
- Secondary body: 14sp / 20sp
- Label: 14sp / semibold
- Small label: 12sp / medium

Títulos podem ter maior presença; textos de apoio devem permanecer discretos.

## Formas

- Pequeno: 14dp
- Médio: 20dp
- Grande: 28dp
- Extra grande: 32dp

Cards principais devem preferir os raios maiores. Evitar componentes excessivamente quadrados.

## Espaçamento

Base de 4dp, com preferência por 8/12/16/24/32dp.

Margem de página padrão: 24dp.

## Iconografia

Ícones devem ser simples, reconhecíveis e funcionais. A iconografia não deve competir com títulos ou com a próxima ação.

## Elementos orgânicos

Podem aparecer como:

- pequenos círculos;
- blobs assimétricos;
- linhas desenhadas;
- pequenos marcadores;
- ilustrações minimalistas.

Nunca usar decoração atrás de conteúdo crítico ou perto da ação principal a ponto de reduzir contraste.

## O que evitar

- dashboard com dezenas de cards;
- excesso de cores por categoria;
- gradientes chamativos;
- sombras pesadas;
- excesso de bordas;
- microcomponentes em sequência;
- aparência de sistema corporativo;
- gamificação agressiva;
- transformar o planner em uma lista de notificações.

## Aplicação ao produto

A linguagem visual deve reforçar a metáfora central:

**Plano → Programação → Rota → Execução**

O visual não deve esconder a complexidade do motor. Deve transformar sua saída em uma orientação simples e humana.

A tela `Agora` é operacional. A tela `Meu Dia` é a página de planner. A semana é visão estratégica. O componente `O que faço agora?` é o ponto de maior destaque da experiência.
