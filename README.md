# Chato API – Rental Backend (Spring Boot)

Backend REST sécurisé par JWT pour la gestion d’annonces immobilières (**rentals**), des utilisateurs et de messages entre utilisateurs.

## 🧰 Stack

- Java 17+ / Spring Boot 3
- Spring Web, Spring Security (JWT), Spring Data JPA (Hibernate)
- MySQL 8
- Swagger / OpenAPI (springdoc)
- Maven

---

## ✅ Prérequis

- **Java** 17 ou 21 (LTS)
- **Maven** 3.9+
- **MySQL** 8.x (local ou Docker)

---

## 🚀 Démarrage rapide

### 1) Récupérer le projet

```bash
git clone https://github.com/jeremie74/projet-3-apirest-chato.git
cd https://github.com/jeremie74/projet-3-apirest-chato.git
```

### 2) Configurer l’application (si besoin)

Fichier `src/main/resources/application.properties` :

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/rental_oc
spring.datasource.username=rental_user
spring.datasource.password=KuTvFT9Eu8XHgLf

spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
spring.jpa.open-in-view=false

spring.mvc.servlet.path=/api

# JWT
jwt.secret=maSuperCleUltraSecreteEtLonguePourJWT123456789
jwt.expiration=86400000

# Swagger + Logbook
logging.level.org.zalando.logbook=TRACE
logbook.format.style=json
logbook.obfuscate.headers=Authorization
```

> `ddl-auto=validate` signifie que **la base doit exister** et correspondre au schéma ci-dessous.

---

## 🗄️ Installation de la base de données

### Option A — MySQL local (terminal)

1. **Créer la base et l’utilisateur**

```sql
-- À exécuter avec un utilisateur disposant des droits (root)
CREATE DATABASE IF NOT EXISTS rental_oc
  CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

CREATE USER IF NOT EXISTS 'rental_user'@'%' IDENTIFIED BY 'KuTvFT9Eu8XHgLf';
GRANT ALL PRIVILEGES ON rental_oc.* TO 'rental_user'@'%';
FLUSH PRIVILEGES;
```

2. **Se connecter et exécuter le schéma**

```bash
mysql -u rental_user -p rental_oc
```

Puis coller le script **Schéma SQL** ci-dessous.

> Si vous aviez déjà créé les tables avec `integer`, voir la section “Migration rapide”.

### Option B — Docker (MySQL 8)

Fichier `docker-compose.yml` minimal :

```yaml
services:
  mysql:
    image: mysql:8.0
    container_name: mysql_rental_oc
    restart: unless-stopped
    environment:
      MYSQL_DATABASE: rental_oc
      MYSQL_USER: rental_user
      MYSQL_PASSWORD: KuTvFT9Eu8XHgLf
      MYSQL_ROOT_PASSWORD: root
    ports:
      - "3306:3306"
    command: ["--default-authentication-plugin=mysql_native_password"]
    volumes:
      - dbdata:/var/lib/mysql
volumes:
  dbdata:
```

D démarrer :

```bash
docker compose up -d
```

Exécuter le schéma :

```bash
docker exec -i mysql_rental_oc mysql -u root -proot rental_oc < schema.sql
```

---

## 🧬 Schéma SQL (corrigé pour `@Id Long`)

Compatible avec `@Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;`

```sql
-- TABLES
CREATE TABLE IF NOT EXISTS `USERS` (
  `id`         BIGINT PRIMARY KEY AUTO_INCREMENT,
  `email`      VARCHAR(255) NOT NULL,
  `name`       VARCHAR(255) NOT NULL,
  `password`   VARCHAR(255) NOT NULL,
  `created_at` TIMESTAMP NULL,
  `updated_at` TIMESTAMP NULL,
  UNIQUE KEY `UK_USERS_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `RENTALS` (
  `id`          BIGINT PRIMARY KEY AUTO_INCREMENT,
  `name`        VARCHAR(255),
  `surface`     INT,
  `price`       DECIMAL(10,2),
  `picture`     VARCHAR(255),
  `description` VARCHAR(2000),
  `owner_id`    BIGINT NOT NULL,
  `created_at`  TIMESTAMP NULL,
  `updated_at`  TIMESTAMP NULL,
  KEY `IDX_RENTALS_owner_id` (`owner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `MESSAGES` (
  `id`         BIGINT PRIMARY KEY AUTO_INCREMENT,
  `rental_id`  BIGINT NOT NULL,
  `user_id`    BIGINT NOT NULL,
  `message`    VARCHAR(2000),
  `created_at` TIMESTAMP NULL,
  `updated_at` TIMESTAMP NULL,
  KEY `IDX_MESSAGES_rental_id` (`rental_id`),
  KEY `IDX_MESSAGES_user_id`   (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- CONTRAINTES FK
ALTER TABLE `RENTALS`
  ADD CONSTRAINT `FK_RENTALS_owner_id__USERS_id`
    FOREIGN KEY (`owner_id`) REFERENCES `USERS` (`id`);

ALTER TABLE `MESSAGES`
  ADD CONSTRAINT `FK_MESSAGES_user_id__USERS_id`
    FOREIGN KEY (`user_id`) REFERENCES `USERS` (`id`);

ALTER TABLE `MESSAGES`
  ADD CONSTRAINT `FK_MESSAGES_rental_id__RENTALS_id`
    FOREIGN KEY (`rental_id`) REFERENCES `RENTALS` (`id`);
```

### Migration rapide (si tables existantes en `integer`)

```sql
ALTER TABLE USERS    MODIFY id BIGINT AUTO_INCREMENT;
ALTER TABLE RENTALS  MODIFY id BIGINT AUTO_INCREMENT, MODIFY owner_id BIGINT;
ALTER TABLE MESSAGES MODIFY id BIGINT AUTO_INCREMENT, MODIFY user_id BIGINT, MODIFY rental_id BIGINT;
```

---

## ▶️ Lancer l’API

**Dev (hot reload maven)**

```bash
mvn spring-boot:run
```

**Build & run**

```bash
mvn clean package
java -jar target/*.jar
```

**Swagger UI**

- URL : `http://localhost:8080/api/swagger-ui/index.html`
- Bouton **Authorize** → coller **uniquement** le JWT (sans `Bearer `).

---

## 🔐 Authentification (JWT)

- `POST /api/auth/register` → crée un utilisateur
- `POST /api/auth/login` → renvoie `{ "token": "..." }`
- Utiliser l’entête HTTP `Authorization: Bearer <token>` pour toutes les routes protégées.

---

## 📡 Endpoints (aperçu)

- **Auth**

  - `POST /api/auth/register` _(public)_
  - `POST /api/auth/login` _(public)_
  - `GET  /api/auth/me` _(JWT)_

- **Users**

  - `GET  /api/user/{id}` _(JWT)_

- **Rentals** _(JWT)_

  - `GET  /api/rentals`
  - `GET  /api/rentals/{id}`
  - `POST /api/rentals`
  - `PUT  /api/rentals/{id}` _(propriétaire uniquement)_

- **Messages** _(JWT)_
  - `POST /api/messages` `{ "user_id": <id>, "rental_id": <id>, "message": "..." }`  
    → `{"message":"Message send with success"}`

---

## 🧪 Test rapide via cURL

```bash
# Register
curl -X POST http://localhost:8080/api/auth/register -H "Content-Type: application/json"  -d '{"name":"Test","email":"test@test.com","password":"test!31"}'

# Login
TOKEN=$(curl -s http://localhost:8080/api/auth/login -H "Content-Type: application/json"  -d '{"email":"test@test.com","password":"test!31"}' | jq -r .token)

# Rentals (GET all)
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/rentals

# Message
curl -X POST http://localhost:8080/api/messages -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json"  -d '{"user_id":1,"rental_id":1,"message":"Hello!"}'
```

---

## 🛠️ Dépannage (FAQ)

- **403 / 401** : token manquant ou invalide. Vérifier l’en-tête `Authorization: Bearer <token>`.
- **404 avec un “/**” final\*\* : appeler `/api/messages` (sans slash) ou activer le mapping `{ "", "/" }` côté contrôleur.
- **415 Unsupported Media Type** : envoyer du **JSON** (`Content-Type: application/json`) ; éviter `multipart/form-data` sauf upload de fichier.
- **CORS** : en dev, utiliser le proxy Angular. En prod, définir un `CorsConfigurationSource`.
