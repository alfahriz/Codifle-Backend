# Codifle API

Headless content delivery backend for company websites. One API powers every section — hero, about, services, products, blog, clients, partners, contact, and SEO — all content-managed, zero redeployment required.

Built on **Quarkus 3.36.1** (Java 21) with PostgreSQL.

---

## Features

### Content API

- **10+ page sections** — each independently configurable: visibility toggle, display order, full content management
- **Navigation** — auto-syncs to visible sections; supports dropdowns and nested links
- **Hero** — title lines, subtitle, dual CTAs, stat blocks
- **Manifesto** — eyebrow + statement
- **About** — body content, CTAs, milestone timeline, stat cards with featured flag
- **Services** — hierarchical (service → sub-services), icon/gradient support, slug-based lookup
- **Products** — category filtering, feature lists, demo links, branded gradients
- **Clients** — 3-track layout for flexible display, active/order control
- **Partners** — logo showcase with attribution links
- **Blog** — paginated articles, category filtering, author profiles, featured flag, read-time, SEO slugs
- **Contact** — form submission endpoint, stored with read/unread status
- **Brand Theme** — color tokens (light + dark), typography, font URLs — design system via API

### SEO

- `GET /sitemap.xml` — auto-generated from live services, products, and articles
- `GET /robots.txt` — dynamically served with sitemap reference

### Authentication & Security

- **JWT with RSA-2048** — asymmetric cryptography, 8-hour expiry, role-based access control
- **Bcrypt password hashing** — cost 12, via PostgreSQL `pgcrypto`
- **Rate limiting** — 5 req/min on `/auth/login` and `/contact/submit`; 120 req/min globally per IP (Bucket4j)
- **Security headers** — X-Frame-Options, X-XSS-Protection, X-Content-Type-Options, Referrer-Policy, Permissions-Policy
- **Safe error responses** — all unhandled errors return generic `{"error":"Internal server error"}`, no stack trace exposure

### Developer Experience

- **OpenAPI / Swagger UI** at `/swagger-ui` and `/openapi`
- **PostgreSQL function layer** — all data access via stored functions (`fn_get_*`, `fn_count_*`, `fn_*`)
- **Snake_case → camelCase** auto-conversion on all query results
- **Environment-based config** — 12-factor ready
- **3 build modes** — dev (live reload), JVM JAR, GraalVM native binary

---

## API Endpoints

| Method | Path                          | Description                                      |
| ------ | ----------------------------- | ------------------------------------------------ |
| POST   | `/api/auth/login`             | Login, returns JWT                               |
| GET    | `/api/auth/me`                | Current user info (admin)                        |
| GET    | `/api/company`                | Company profile                                  |
| GET    | `/api/company/socials`        | Social media links                               |
| GET    | `/api/theme`                  | Brand theme tokens                               |
| GET    | `/api/sections`               | All sections with order/visibility               |
| GET    | `/api/sections/nav`           | Visible nav links                                |
| GET    | `/api/hero`                   | Hero content                                     |
| GET    | `/api/hero/stats`             | Hero stat blocks                                 |
| GET    | `/api/manifesto`              | Manifesto statement                              |
| GET    | `/api/about`                  | About content                                    |
| GET    | `/api/about/milestones`       | Company milestones                               |
| GET    | `/api/about/stats`            | About stat cards                                 |
| GET    | `/api/services`               | All active services                              |
| GET    | `/api/services/{key}`         | Single service by slug                           |
| GET    | `/api/services/{key}/subs`    | Sub-services                                     |
| GET    | `/api/products`               | Products (optional `?category=`)                 |
| GET    | `/api/products/categories`    | Product categories                               |
| GET    | `/api/products/{id}/features` | Product features                                 |
| GET    | `/api/clients`                | Active clients (3-track ordered)                 |
| GET    | `/api/partners`               | Active partners                                  |
| GET    | `/api/blog/articles`          | Paginated articles (`?limit=&offset=&category=`) |
| GET    | `/api/blog/articles/{slug}`   | Single article by slug                           |
| GET    | `/api/blog/section`           | Blog section metadata                            |
| GET    | `/api/contact`                | Contact section content                          |
| POST   | `/api/contact/submit`         | Submit contact form                              |
| GET    | `/sitemap.xml`                | Auto-generated XML sitemap                       |
| GET    | `/robots.txt`                 | Robots file                                      |

Most endpoints also have a `/count` variant (e.g. `GET /api/services/count`).

---

## Running

**Dev mode (live reload):**

```shell
./mvnw quarkus:dev
```

Dev UI available at `http://localhost:8080/q/dev/`

**Production JAR:**

```shell
./mvnw package
java -jar target/quarkus-app/quarkus-run.jar
```

**Über-JAR:**

```shell
./mvnw package -Dquarkus.package.jar.type=uber-jar
java -jar target/*-runner.jar
```

**Native executable (requires GraalVM):**

```shell
./mvnw package -Dnative
./target/codifle-api-1.0.0-SNAPSHOT-runner
```

**Native via container (no GraalVM needed):**

```shell
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

---

## Tech Stack

| Layer           | Technology              |
| --------------- | ----------------------- |
| Framework       | Quarkus 3.36.1          |
| Language        | Java 21                 |
| Database        | PostgreSQL              |
| Auth            | SmallRye JWT + RSA-2048 |
| Rate Limiting   | Bucket4j 7.6.0          |
| Validation      | Hibernate Validator     |
| Serialization   | Jackson                 |
| Connection Pool | Agroal                  |
| Docs            | SmallRye OpenAPI        |
