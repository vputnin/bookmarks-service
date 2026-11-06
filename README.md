# bookmarks-service

A small Spring Boot 3 / Java 21 bookmarks API with a plain HTML page, built to showcase
an **AI-first development workflow with Claude Code**: shared `.claude/` configuration,
`CLAUDE.md` as the project reference, `TASKS.md`-driven iterations, project skills
(`/commit`, `/review`, `/wrap`, `/merge-to-main`), a code-review subagent with persistent
memory, and hooks that enforce conventions automatically.

**Live demo:** https://bookmarks-service.thankfulisland-b78d27a9.westeurope.azurecontainerapps.io

## Run
```bash
mvn -q test
mvn spring-boot:run   # http://localhost:8080
```

## API
| Method | Path | Result |
|---|---|---|
| GET | `/api/bookmarks` | list, newest first |
| GET | `/api/bookmarks/{id}` | one bookmark or 404 ProblemDetail |
| POST | `/api/bookmarks` | 201 + Location, 400 ProblemDetail on validation errors |
| DELETE | `/api/bookmarks/{id}` | 204 or 404 |

## Deployment
The app runs on [Azure Container Apps](https://learn.microsoft.com/azure/container-apps/),
built from the multi-stage `Dockerfile` (Java 21, non-root user, port 8080).

One-time setup (Azure CLI):
```bash
az login
az extension add --name containerapp --upgrade
az provider register --namespace Microsoft.App
az provider register --namespace Microsoft.OperationalInsights
```

Variables used below:
```bash
RG=bookmarks-rg
ENV=bookmarks-service-env
ACR=ca9efd84facaacr
IMAGE="$ACR.azurecr.io/bookmarks-service:$(git rev-parse --short HEAD)"
```

Build locally, push to ACR, deploy the image (repeat to redeploy after changes).
`--platform linux/amd64` is required when building on Apple Silicon:
```bash
az acr login --name "$ACR"
docker build --platform linux/amd64 -t "$IMAGE" .
docker push "$IMAGE"

az containerapp up \
  --name bookmarks-service \
  --resource-group "$RG" \
  --location westeurope \
  --environment "$ENV" \
  --image "$IMAGE" \
  --registry-server "$ACR.azurecr.io" \
  --ingress external \
  --target-port 8080
```

Alternative: let Azure build from source. This needs ACR Tasks, which are disabled in
some subscriptions (including ours), so the image flow above is the default:
```bash
az containerapp up \
  --name bookmarks-service \
  --resource-group "$RG" \
  --location westeurope \
  --environment "$ENV" \
  --source . \
  --ingress external \
  --target-port 8080
```

Storage is in-memory, so keep a single replica (otherwise each replica has its own data);
bookmarks are lost on restart or redeploy:
```bash
az containerapp update --name bookmarks-service --resource-group "$RG" \
  --min-replicas 1 --max-replicas 1
```

Public URL and logs:
```bash
az containerapp show --name bookmarks-service --resource-group "$RG" \
  --query properties.configuration.ingress.fqdn -o tsv
az containerapp logs show --name bookmarks-service --resource-group "$RG" --follow
```

## AI workflow layout
```
CLAUDE.md                     project reference, loaded every session
TASKS.md                      backlog + session log, updated by /wrap
docs/architecture.md          imported into CLAUDE.md via @docs/architecture.md
.claude/settings.json         shared permissions + hooks
.claude/hooks/                protect_files.py (PreToolUse), check_java_conventions.py (PostToolUse)
.claude/skills/               commit, review, wrap, merge-to-main, add-endpoint
.claude/agents/code-reviewer.md   review subagent, memory: project
.claude/agent-memory/code-reviewer/   the subagent's persistent memory (index + topic files)
```
