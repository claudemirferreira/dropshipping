# Prompt Task: Melhorar UI/UX e Layout do Sistema Dropshipping

> Documento para orientar implementação de melhorias visuais, usabilidade e layout profissional. Use como prompt para IA ou especificação técnica para desenvolvedores.

---

## Contexto

**Projeto:** Frontend Angular 19 + PrimeNG 19 (tema Aura, cor primária emerald)

**Layout atual:** Inspirado no Sakai (PrimeNG), com:
- Topbar fixo (4rem) com gradiente verde, logo, hamburger e avatar
- Sidebar fixa (17rem) com menu dinâmico por perfil/rotinas
- Main content com `router-outlet`
- Footer simples

**Padrão de páginas:** `page-header` (título + descrição + badge) → `page-toolbar` (busca, filtros, ações) → `table-card` ou cards

**Objetivo:** Elevar o visual para nível profissional, reforçar identidade de marca e melhorar usabilidade sem quebrar funcionalidades.

---

## Regras Gerais

1. Manter compatibilidade com Angular 19 e PrimeNG 19.
2. Preservar responsividade (breakpoint 992px).
3. Não alterar lógica de negócio nem rotas; apenas estilos, componentes visuais e estrutura de layout.
4. Usar variáveis CSS (`:root`) para cores e espaçamentos; facilitar manutenção.
5. Garantir contraste WCAG AA em texto e interações.
6. Preferir transições suaves (0.2s–0.3s) em hovers e mudanças de estado.

---

## Tarefas por Categoria

### 1. Identidade Visual e Paleta

| ID | Tarefa | Prioridade | Detalhes |
|----|--------|------------|----------|
| P1.1 | Definir paleta de cores consistente | Alta | Trocar verde genérico por teal/índigo profissional. Exemplo: primária `#0d9488` (teal) ou `#4f46e5` (índigo); sucesso `#059669`; neutros em escala slate. Atualizar `styles.scss`, variáveis PrimeNG e `sakai-topbar`. |
| P1.2 | Aplicar cor primária em CTAs e estados ativos | Alta | Botões primários, item ativo do menu, links de destaque e focus states devem usar a cor primária da paleta. |
| P1.3 | Separar visualmente sidebar do conteúdo | Média | Sidebar com fundo `#f1f5f9` ou borda sutil; área de conteúdo com fundo `#f8fafc`; cards/tabelas brancos para contraste. |
| P1.4 | Criar variáveis de design token | Média | Centralizar em `:root`: `--primary`, `--primary-hover`, `--surface-page`, `--surface-card`, `--text-primary`, `--text-muted`, `--border-color`, `--shadow-sm`, `--radius-md`. |

### 2. Topbar (Header)

| ID | Tarefa | Prioridade | Detalhes |
|----|--------|------------|----------|
| T2.1 | Refinar topbar para aspecto profissional | Alta | Manter altura 4rem; gradiente suave ou sólido na cor primária; botões só ícone, transparentes, hover sutil. Já implementado parcialmente em `styles.scss`. |
| T2.2 | Incluir nome do sistema na topbar | Média | Nome "Dropshipping" ao lado do ícone (já existe no template); garantir legibilidade em branco sobre fundo escuro. |
| T2.3 | Melhorar menu do usuário (avatar) | Média | Popup com fundo branco, borda leve, sombra; item "Sair" com ícone e label; sem fundo preto. Já há override em `styles.scss` para `.topbar-user-menu`. |
| T2.4 | Adicionar breadcrumb (opcional) | Baixa | Abaixo ou ao lado do título da página, ex.: "Dashboard" ou "Usuários > Lista". Considerar componente PrimeNG ou custom. |

### 3. Sidebar

| ID | Tarefa | Prioridade | Detalhes |
|----|--------|------------|----------|
| S3.1 | Melhorar indicação do item ativo | Alta | Trocar fundo sólido por barra vertical (3–4px) à esquerda + fundo suave na cor primária (`#eef2ff` ou similar). Texto na cor primária ou cinza escuro. |
| S3.2 | Brand com ícone e nome | Média | Substituir emoji 📦 por ícone PrimeIcons (`pi-box` ou `pi-shopping-bag`). Nome "Dropshipping" no header da sidebar (ou apenas no topbar, conforme decisão). |
| S3.3 | Refinar grupos de menu (perfis) | Média | Manter `menu-group-header` uppercase; opcional: ícone ou badge ao lado do nome do perfil para hierarquia visual. |
| S3.4 | Garantir sidebar colapsável em mobile | Alta | Já existe; verificar animação suave, overlay escuro e bloqueio de scroll ao abrir. |

### 4. Área de Conteúdo e Páginas

| ID | Tarefa | Prioridade | Detalhes |
|----|--------|------------|----------|
| C4.1 | Padronizar page-header | Alta | Título 1.5rem, weight 700; descrição 0.875rem, cor muted; badge com dot colorido + valor + label. Garantir layout flex responsivo (wrap em mobile). |
| C4.2 | Padronizar page-toolbar | Alta | Busca à esquerda, filtros no centro, ações à direita; alinhamento e espaçamento consistentes; input de busca com ícone e placeholder claro. |
| C4.3 | Cards com sombra e borda suave | Média | `box-shadow: 0 1px 3px rgba(0,0,0,0.08)`; `border-radius: var(--p-border-radius)`; fundo branco. |
| C4.4 | Tabelas com zebra sutil | Média | Linhas pares `#f8fafc`; hover `#f1f5f9`; header `#f8fafc`; bordas `#e2e8f0`. Já há override em `styles.scss`; validar consistência. |
| C4.5 | Limite de largura em telas grandes (opcional) | Baixa | `max-width` no main content (ex.: 1400px) com `margin: 0 auto` para evitar linhas muito longas em monitores grandes. |

### 5. Tipografia

| ID | Tarefa | Prioridade | Detalhes |
|----|--------|------------|----------|
| Ty5.1 | Carregar fonte com personalidade | Média | Plus Jakarta Sans ou DM Sans (Google Fonts). Definir em `index.html` e `--font-family` em `:root`. |
| Ty5.2 | Hierarquia tipográfica consistente | Alta | H1 página: 1.5rem/700; H2 card: 1.25rem/600; descrição: 0.875rem/400; labels: 0.875rem/500; line-height ~1.5. |
| Ty5.3 | Cor de texto secundário | Média | `#64748b` ou `#475569` para descrições, labels, badges. |

### 6. Componentes PrimeNG (Overrides)

| ID | Tarefa | Prioridade | Detalhes |
|----|--------|------------|----------|
| PM6.1 | Garantir fundo claro em overlays | Alta | Select, dropdown, menu popup, dialog: fundo branco, borda `#e2e8f0`, sombra suave. Revisar `styles.scss` para `.p-select-overlay`, `.p-dialog`, etc. |
| PM6.2 | Botões de ação em tabelas | Média | Coluna Ações: botões ícone, text/rounded, cor neutra; hover na cor primária. Já existe override para `.p-datatable .p-datatable-tbody > tr > td:last-child`. |
| PM6.3 | Inputs e formulários | Média | Fundo branco, borda clara, placeholder cinza; focus com outline na cor primária. |
| PM6.4 | PickList e DataTable em dialogs | Média | Garantir fundo claro em listas e filtros dentro de dialogs. Já há regras em `styles.scss`. |

### 7. Microinterações e Feedback

| ID | Tarefa | Prioridade | Detalhes |
|----|--------|------------|----------|
| M7.1 | Estados de hover consistentes | Alta | Links, botões, itens de menu: transição 0.2s; cor de destaque no hover. |
| M7.2 | Estados de loading | Média | Skeleton ou spinner em tabelas lazy; desabilitar botões durante submit. |
| M7.3 | Feedback de sucesso/erro | Média | Toast PrimeNG já usado; garantir posição e estilo adequados. |

### 8. Responsividade e Acessibilidade

| ID | Tarefa | Prioridade | Detalhes |
|----|--------|------------|----------|
| R8.1 | Mobile-first em toolbars e headers | Alta | page-header e page-toolbar em coluna ou wrap; botões e busca empilhados em telas pequenas. |
| R8.2 | Tabelas responsivas | Média | Scroll horizontal ou cards em mobile; considerar `p-table` com `scrollable`. |
| R8.3 | Contraste e foco | Alta | Focus visible em botões e links; contraste mínimo WCAG AA. |
| R8.4 | Labels em formulários | Média | Associar `label` a `input` via `for`/`id`; placeholders complementares, não substitutos. |

---

## Ordem de Execução Sugerida

1. **Fase 1 (identidade):** P1.1, P1.2, P1.4, Ty5.2
2. **Fase 2 (layout):** S3.1, T2.1, C4.1, C4.2, P1.3
3. **Fase 3 (refino):** S3.2, S3.3, C4.3, C4.4, PM6.1, Ty5.1
4. **Fase 4 (polimento):** M7.1, R8.1, R8.3, T2.4, C4.5

---

## Arquivos Principais a Alterar

| Arquivo | Uso |
|---------|-----|
| `src/styles.scss` | Variáveis globais, overrides PrimeNG, layout classes |
| `src/app/core/layout/sakai/sakai-sidebar.component.ts` | Estilos e template do sidebar |
| `src/app/core/layout/sakai/sakai-topbar.component.ts` | Estilos e template do topbar |
| `src/app/core/layout/sakai/sakai-footer.component.ts` | Estilos do footer |
| `src/app/core/layout/main-layout/main-layout.ts` | Layout wrapper e main |
| `src/index.html` | Google Fonts, meta viewport |
| `angular.json` ou `styles` em `project` | Inclusão de estilos globais |
| Componentes de feature (dashboard, users-list, etc.) | Classes de page-header, page-toolbar, table-card |

---

## Critérios de Conclusão

- [ ] Paleta de cores aplicada em todo o app via variáveis CSS
- [ ] Item ativo do menu com barra lateral e fundo suave (não sólido)
- [ ] Topbar e sidebar com aparência consistente e profissional
- [ ] Page-header e page-toolbar padronizados em todas as listas
- [ ] Tabelas e cards com fundo claro e zebra/hover consistentes
- [ ] Overlays (select, dialog, menu) com fundo branco e borda clara
- [ ] Responsividade preservada (sidebar mobile, toolbars wrap)
- [ ] Sem regressões visuais em telas existentes

---

## Notas para IA

Ao implementar:
1. Leia `styles.scss`, `sakai-sidebar.component.ts`, `sakai-topbar.component.ts` e `main-layout.ts` antes de editar.
2. Use `!important` apenas onde necessário para sobrescrever PrimeNG; preferir especificidade quando possível.
3. Mantenha estilos inline/encapsulados nos componentes; evite duplicar regras já em `styles.scss`.
4. Consulte `docs/ux-sugestoes.md` para contexto adicional sobre sugestões anteriores.
