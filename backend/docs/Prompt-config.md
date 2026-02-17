### Prompt – Configuração de OCI e `.pem`

Use este passo a passo sempre que precisar configurar credenciais da OCI (tanto em dev quanto em produção/VPS).

---

**1. Criar/obter a API Key na OCI**

1. Acesse o console OCI.  
2. Vá em: **Menu → Identity & Security → Users → [seu usuário] → API Keys**.  
3. Clique em **Add API Key**:
   - Escolha **Generate API Key Pair** (deixe a OCI gerar).
   - Baixe o arquivo **private key (.pem)** e guarde (ex: `oci_api_key.pem`).  
4. Após salvar, copie os valores mostrados na tela:
   - `User OCID`
   - `Tenancy OCID`
   - `Fingerprint`
   - `Region` (ex: `sa-saopaulo-1`).

---

**2. Criar diretório `.oci` e mover a chave**

No terminal da máquina (dev ou VPS), como o usuário que vai rodar o serviço:

```bash
cd ~
mkdir -p .oci

# copie o arquivo baixado (ajuste o caminho de origem)
mv /caminho/onde/baixou/oci_api_key.pem ~/.oci/oci_api_key.pem

chmod 600 ~/.oci/oci_api_key.pem
```

---

**3. Criar o arquivo `~/.oci/config`**

Ainda na mesma máquina:

```bash
nano ~/.oci/config
```

Cole o modelo abaixo, preenchendo com os valores reais copiados da OCI:

```ini
[DEFAULT]
user=ocid1.user.oc1..SEU_USER_OCID_AQUI
fingerprint=SEU_FINGERPRINT_AQUI
tenancy=ocid1.tenancy.oc1..SEU_TENANCY_OCID_AQUI
region=sa-saopaulo-1
key_file=/home/SEU_USUARIO/.oci/oci_api_key.pem
```

- Troque `SEU_USUARIO` pelo usuário real (ex: `joao` ou `ubuntu`).  
- Ajuste `region` se usar outra região.

Depois salve e garanta permissão segura:

```bash
chmod 600 ~/.oci/config
```

---

**4. Configurar a aplicação (Spring Boot)**

No `application.yaml` da aplicação Java, garantir:

```yaml
oci:
  object-storage:
    namespace: SEU_NAMESPACE_DO_BUCKET
    bucket-name: NOME_DO_BUCKET
    region: sa-saopaulo-1
    config-profile: DEFAULT
```

- `namespace`: está na tela de detalhes do bucket.  
- `bucket-name`: nome do bucket (ex: `products`).

---

**5. Relacionamento com produtos e tabela `PRODUCT_FILE`**

- Cada **produto** pode ter **várias fotos** e **vários vídeos**.  
- Cada upload realizado para o Object Storage (imagem ou vídeo) é associado a um produto e **registrado na tabela `PRODUCT_FILE`**.  
- Nesta tabela ficam os **metadados do arquivo** (por exemplo: identificador do produto, tipo do arquivo – imagem/vídeo, nome original, caminho/`objectName` no bucket, etc.), enquanto o **conteúdo binário** fica sempre no Object Storage da OCI.

Organização dos arquivos no bucket:
- Se o arquivo for uma **foto**, ele deve ser salvo na pasta (prefix) `imagem/` do bucket (ex: `imagem/12345-minha-foto.jpg`).  
- Se o arquivo for um **vídeo**, ele deve ser salvo na pasta (prefix) `video/` do bucket (ex: `video/12345-meu-video.mp4`).  
- O campo de caminho/`objectName` salvo na `PRODUCT_FILE` deve refletir esse prefixo (`imagem/` ou `video/`) para permitir que a aplicação recupere o arquivo corretamente.

Isso significa que:
- Ao subir uma nova foto/vídeo de produto, a aplicação:
  1. Envia o arquivo para o bucket configurado na OCI.  
  2. Cria um registro em `PRODUCT_FILE` vinculado ao produto correspondente.  
- Ao listar os arquivos de um produto, a aplicação lê os registros de `PRODUCT_FILE` e monta as URLs/paths a partir dos metadados salvos.

---

**6. Teste rápido**

1. Suba a aplicação:

```bash
./mvnw spring-boot:run
```

2. Crie (ou pegue) o **ID de um produto** existente na base (via endpoint de criação de produto ou pela própria base de dados).

3. Teste o upload de uma **imagem** de produto usando Swagger ou `curl`:

```bash
curl -X POST "http://localhost:8080/api/v1/products/{PRODUCT_ID}/files?type=IMAGE" \
  -H "Authorization: Bearer SEU_TOKEN_JWT_AQUI" \
  -F "file=@/caminho/para/sua/imagem.jpg"
```

4. Para enviar um **vídeo**, altere o parâmetro `type` para `VIDEO` e aponte para um arquivo de vídeo:

```bash
curl -X POST "http://localhost:8080/api/v1/products/{PRODUCT_ID}/files?type=VIDEO" \
  -H "Authorization: Bearer SEU_TOKEN_JWT_AQUI" \
  -F "file=@/caminho/para/seu/video.mp4"
```

Se der erro de credencial na OCI, revisar:
- Caminho do `key_file` no `config`.  
- Permissões do `.pem` (`chmod 600`).  
- Valores de `user`, `tenancy`, `fingerprint` e `region`.

