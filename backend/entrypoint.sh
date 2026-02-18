#!/bin/sh
# Garante que /app/uploads exista e tenha permissão para o usuário app
if [ -d /app/uploads ]; then
  chown -R app:app /app/uploads 2>/dev/null || true
fi
exec su-exec app java -jar app.jar
