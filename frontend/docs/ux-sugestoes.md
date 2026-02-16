# Sugestões de UI/UX – Sistema Dropshipping

Visão geral de melhorias em **cores**, **menu**, **tipografia** e **layout** para dar mais identidade e usabilidade ao sistema.

---

## 1. Cores e identidade

**Situação atual:** Paleta genérica (slate/cinza + verde #22c55e). Pouca identidade de marca.

**Sugestões:**

| Uso | Atual | Sugestão | Motivo |
|-----|--------|----------|--------|
| **Cor primária** | Verde (#22c55e) pontual | Um azul índigo (ex: `#4f46e5`) ou teal (`#0d9488`) | Transmite confiança/profissionalismo; combina com e-commerce |
| **Sidebar** | `#f8fafc` | Levemente mais escuro ou com borda sutil (`#f1f5f9`) ou sidebar em cor primária suave | Separa melhor da área de conteúdo |
| **Item ativo no menu** | Fundo cinza | Barra vertical na esquerda na cor primária + fundo suave | Deixa óbvio onde o usuário está |
| **Header** | Branco | Manter branco ou igual ao fundo do conteúdo com sombra leve | Consistência e hierarquia |
| **Área de conteúdo** | Branco | Fundo levemente off-white (`#fafafa` ou `#f8fafc`) | Reduz cansaço e destaca cards/tabelas brancos |

**Paleta sugerida (exemplo – modo claro):**
- Primária: `#4f46e5` (índigo)
- Primária hover: `#4338ca`
- Neutros: manter escala slate (#0f172a → #f8fafc)
- Sucesso: `#059669` (emerald) ou manter `#22c55e`
- Superfícies: branco para cards/tabelas; fundo da página um tom mais suave

---

## 2. Menu (sidebar)

**Situação atual:** Menu funcional com grupos por perfil; brand só com emoji 📦.

**Sugestões:**

1. **Brand**
   - Nome do sistema (“Dropshipping”) ao lado do ícone.
   - Ícone: manter 📦 ou trocar por ícone PrimeIcons (`pi-box` ou `pi-shopping-bag`) para ficar alinhado ao resto.
   - Altura fixa e padding para não “sumir” quando há muitos itens.

2. **Item ativo**
   - Barra vertical (3–4px) à esquerda na cor primária.
   - Fundo do item ativo em tom muito suave da primária (ex: `#eef2ff`) em vez de cinza.
   - Texto do item ativo na cor primária ou em cinza escuro.

3. **Grupos (perfil)**
   - Manter `menu-group-header` em uppercase pequeno.
   - Opcional: ícone ou badge ao lado do nome do perfil para reforçar hierarquia.

4. **Footer (avatar)**
   - Separador visual já existe; opcional: nome do usuário truncado ao lado do avatar em telas maiores.
   - Tooltip no avatar está bom para mobile.

5. **Responsivo**
   - Em telas pequenas: sidebar colapsável (hamburger) ou drawer; header com botão para abrir menu.

---

## 3. Header

**Situação atual:** Título “Dropshipping”, busca (readonly), refresh e sair.

**Sugestões:**

1. **Hierarquia**
   - Título principal menor ou com peso 600; opcional: breadcrumb (ex: “Dashboard” ou “Usuários > Lista”) ao lado ou abaixo para contexto.

2. **Busca**
   - Quando for implementada: atalho Ctrl+K já está no placeholder; manter.
   - Estilo: borda suave, foco com outline na cor primária.

3. **Ações (refresh, sair)**
   - Manter ícones; opcional: “Sair” com texto em telas maiores.
   - Cor dos ícones em cinza e hover na primária ou em cinza mais escuro para consistência.

4. **Altura**
   - Manter ~4rem; evita header pesado e deixa mais espaço para conteúdo.

---

## 4. Tipografia

**Situação atual:** `var(--font-family)` + fallback system (Segoe UI, Roboto, sans-serif).

**Sugestões:**

1. **Fonte com personalidade (opcional)**
   - **Plus Jakarta Sans** ou **DM Sans** (Google Fonts): moderna, boa legibilidade em UI.
   - Carregar no `index.html` e definir em `:root` como `--font-family`.

2. **Hierarquia**
   - Títulos de página: 1.5rem, weight 700 (já usado no dashboard).
   - Subtítulos/descrição: 0.875rem, cor secundária (#64748b).
   - Labels de formulário e tabelas: 0.875rem, weight 500.
   - Manter line-height ~1.5 no corpo para leitura confortável.

---

## 5. Área de conteúdo e páginas

- **Padding:** Manter 1.5rem–2rem; em telas grandes pode aumentar um pouco (ex: max-width no conteúdo com margin auto).
- **Cards (ex.: dashboard):** Bordas suaves, sombra leve (`box-shadow: 0 1px 3px rgba(0,0,0,0.08)`) para destacar do fundo.
- **Tabelas:** Manter cabeçalho em cinza claro e zebra sutil; hover já está bom no `styles.scss`.
- **Botões primários:** Usar a cor primária da paleta para CTAs principais (salvar, confirmar).

---

## 6. Resumo de prioridades

| Prioridade | Ação |
|------------|------|
| Alta | Definir cor primária e usar em menu ativo + botões principais |
| Alta | Sidebar: barra de “ativo” + brand com nome |
| Média | Fundo da área de conteúdo off-white; cards com sombra leve |
| Média | Fonte customizada (Plus Jakarta Sans ou DM Sans) |
| Baixa | Breadcrumb no header; sidebar colapsável no mobile |

Se quiser, na próxima etapa podemos aplicar apenas as de **prioridade alta** nos arquivos (variáveis em `styles.scss` + `main-layout`) para você ver o resultado e depois refinar.
