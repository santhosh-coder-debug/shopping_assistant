# Shopping Assistant (Java 25 / Spring Boot / Spring AI)

A backend starter with product CRUD/search, Spring AI tool calling, pgvector semantic retrieval, JDBC-backed chat memory, Flyway, Docker and Kubernetes templates.

## Prerequisites
- JDK 25 and Maven 3.9+ for running from IntelliJ, or Docker Desktop for Compose.
- OpenAI API key with API billing enabled (ChatGPT subscription is not an API key).
- Ports 5432 and 8080 available.

## Start with Docker
1. Copy `.env.example` to `.env`; fill `OPENAI_API_KEY` and a strong `ADMIN_API_KEY`.
2. `docker compose up --build -d`
3. `curl http://localhost:8080/actuator/health`
4. Seed products are created by Flyway. Index them once:
   `curl -X POST http://localhost:8080/api/v1/products/reindex -H "X-Admin-Key: YOUR_ADMIN_KEY"`
5. Try `curl 'http://localhost:8080/api/v1/products?maxPrice=1000&currency=USD'`
6. Chat:
   `curl -X POST http://localhost:8080/api/v1/chat -H 'Content-Type: application/json' -d '{"conversationId":"demo-1","message":"Find a laptop under $1000"}'`

## Run from IntelliJ
Start only PostgreSQL: `docker compose up -d db`. Import the root `pom.xml` as a Maven project, select JDK 25, set `OPENAI_API_KEY`, `DB_URL=jdbc:postgresql://localhost:5432/shopping`, `DB_USER=shopping`, `DB_PASSWORD=shopping`, and `ADMIN_API_KEY`. Run `ShoppingAssistantApplication`.

## APIs
- `GET /api/v1/products?brand=&category=&maxPrice=&currency=` (public demo)
- `GET /api/v1/products/{id}` (public demo)
- `GET /api/v1/products/semantic?query=...` (public demo)
- `POST /api/v1/chat` body `{ "conversationId":"demo-1", "message":"..." }` (public demo)
- `POST /api/v1/products` create product (admin key)
- `PUT /api/v1/products/{id}` update product (admin key)
- `POST /api/v1/products/reindex` refresh vector index (admin key)

## Kubernetes
Provision PostgreSQL with pgvector and persistent storage outside these manifests. Push your Docker image and replace `YOUR_REGISTRY` in `k8s/deployment.yaml`. Create `shopping-assistant-secrets` with `DB_PASSWORD`, `OPENAI_API_KEY`, and `ADMIN_API_KEY` using your secrets manager; do not commit credentials. Adjust DB_URL in ConfigMap. `kubectl apply -f k8s/`. Add an Ingress or Gateway, HTTPS, external authentication and a managed DNS name to make it public.

## Important scope / production gaps
This is a runnable **backend starter**, not a security-certified public storefront. Public chat endpoints have no authentication or rate limiting. Conversation IDs are client-supplied and not bound to an identity. Before internet exposure, add JWT/OIDC, per-user conversation ownership, authorization for product reads, tenant isolation if applicable, rate limiting, audit logs, strict request size limits, CORS allowlists, prompt-injection tests, structured product responses, tracing, evaluation and secret rotation. The simple admin key is only for development; replace it with proper RBAC. Indexing is synchronous and manual; implement an outbox and async worker for catalog changes. JPA + a single PostgreSQL database use local transactions, so JTA is intentionally omitted. Chat is synchronous REST (not SSE). Catalog records are sample data, not real merchant inventory. The model may still make errors; validate returned product IDs and price constraints in application code before showing customer-facing recommendations.

## Dependency note
Versions are pinned to Spring Boot 3.5.7 and Spring AI 1.1.2 as a starting compatibility combination; this archive was not compiled against remote Maven repositories in this environment. If a repository artifact or API has changed, check the Spring AI compatibility matrix and update the BOM or corresponding code before deploying. Docker image tags for Java 25 also require verification against your registry.
