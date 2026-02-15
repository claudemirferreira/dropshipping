# Dropshipping - Frontend

Aplicação Angular 19 com PrimeNG 21 para o sistema de Dropshipping.

## Pré-requisitos

- Node.js 20+ e npm
- Backend rodando em `http://localhost:8080`

## Instalação

```bash
npm install
```

## Desenvolvimento

```bash
npm start
```

Acesse [http://localhost:4200](http://localhost:4200). O proxy redireciona requisições `/api` para o backend em `localhost:8080`.

## Build

```bash
npm run build
```

Os arquivos gerados ficam em `dist/frontend`.

## Credenciais de teste (perfil dev do backend)

- **E-mail:** admin@dropshipping.com  
- **Senha:** Senha@123

## Estrutura

- `src/app/core/` - Serviços, guards, interceptors
- `src/app/features/` - Módulos por funcionalidade (auth, dashboard, users)
- `src/environments/` - Configurações de ambiente
