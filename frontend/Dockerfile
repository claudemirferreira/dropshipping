# Build
FROM node:20-alpine AS build
WORKDIR /app

COPY package.json package-lock.json ./
RUN npm ci

COPY . .
RUN npm run build

# Serve com nginx
FROM nginx:alpine
ARG NGINX_CONFIG=nginx.conf
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html
COPY ${NGINX_CONFIG} /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
