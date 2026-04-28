# EcoStore Microservice Backend

Backend de EcoStore desarrollado para el taller practico de Arquitectura Hexagonal, Contenedores y Documentacion RFC del curso Software Architecture II. El proyecto implementa una solucion de comercio electronico basada en microservicios, comunicacion por NATS, persistencia con PostgreSQL, almacenamiento temporal con Redis y despliegue reproducible mediante Docker Compose.

## Tabla de Contenido

1. [Descripcion General](#descripcion-general)
2. [Arquitectura del Sistema](#arquitectura-del-sistema)
3. [Tecnologias Utilizadas](#tecnologias-utilizadas)
4. [Estructura del Repositorio](#estructura-del-repositorio)
5. [Microservicios](#microservicios)
6. [Puertos del Proyecto](#puertos-del-proyecto)
7. [Requisitos Previos](#requisitos-previos)
8. [Ejecucion con Docker Compose](#ejecucion-con-docker-compose)
9. [Verificacion del Entorno](#verificacion-del-entorno)
10. [Endpoints Principales](#endpoints-principales)
11. [Flujo de Prueba Recomendado](#flujo-de-prueba-recomendado)
12. [Arquitectura Hexagonal](#arquitectura-hexagonal)
13. [Mensajeria NATS](#mensajeria-nats)
14. [Persistencia](#persistencia)
15. [Comandos Utiles](#comandos-utiles)
16. [Solucion de Problemas](#solucion-de-problemas)
17. [Documentacion Relacionada](#documentacion-relacionada)
18. [Integrantes](#integrantes)

## Descripcion General

EcoStore es una plataforma backend de comercio electronico organizada como un ecosistema de microservicios. La aplicacion permite gestionar usuarios, autenticacion, catalogo de productos, carrito de compras y proceso de checkout.

El sistema fue disenado para cumplir los requisitos academicos del taller:

- Aplicar arquitectura hexagonal en los microservicios principales.
- Usar persistencia real mediante bases de datos integradas por adaptadores de infraestructura.
- Orquestar todo el entorno mediante Docker Compose.
- Documentar las decisiones arquitectonicas con enfoque RFC y Modelo C4.
- Permitir que el docente o cualquier integrante pueda levantar el proyecto con comandos reproducibles.

## Arquitectura del Sistema

La solucion esta compuesta por un API Gateway, cuatro microservicios de negocio y componentes de infraestructura compartida.

```text
Cliente / Consumidor API
        |
        v
API Gateway :8080
        |
        v
NATS :4222
        |
        +--> Product Service  :8081 --> PostgreSQL product_db
        +--> Cart Service     :8082 --> Redis
        +--> Checkout Service :8083 --> PostgreSQL checkout_db
        +--> Users Service    :8084 --> PostgreSQL users_db
```

El API Gateway expone los endpoints HTTP publicos. Internamente, las solicitudes son transformadas en comandos NATS hacia los microservicios correspondientes. Cada microservicio mantiene su propia responsabilidad y su propia tecnologia de persistencia.

## Tecnologias Utilizadas

- Java 25
- Spring Boot 4.0.3
- Maven
- Docker
- Docker Compose
- NATS
- PostgreSQL 16 Alpine
- Redis 7 Alpine
- Spring Data JPA
- Spring Data Redis
- Spring Security
- JWT
- SpringDoc OpenAPI / Swagger
- Lombok
- MapStruct

## Estructura del Repositorio

```text
eco-store-microservice-backend/
|-- api-gateway/
|   |-- Dockerfile
|   |-- pom.xml
|   `-- src/
|-- product-service/
|   |-- Dockerfile
|   |-- pom.xml
|   `-- src/
|-- cart-service/
|   |-- Dockerfile
|   |-- pom.xml
|   `-- src/
|-- checkout-service/
|   |-- Dockerfile
|   |-- pom.xml
|   `-- src/
|-- users-service/
|   |-- Dockerfile
|   |-- pom.xml
|   `-- src/
|-- docs/
|   `-- API_GATEWAY_CONTRACTS.md
|-- docker-compose.yml
|-- pom.xml
|-- mvnw
|-- mvnw.cmd
`-- README.md
```

## Microservicios

| Servicio | Puerto | Responsabilidad |
|---|---:|---|
| API Gateway | 8080 | Punto de entrada HTTP. Expone endpoints publicos y envia comandos por NATS. |
| Product Service | 8081 | Gestiona el catalogo de productos y su persistencia en PostgreSQL. |
| Cart Service | 8082 | Gestiona carritos de compra y almacena informacion temporal en Redis. |
| Checkout Service | 8083 | Procesa la compra, calcula el total, confirma el pago y persiste la orden. |
| Users Service | 8084 | Gestiona usuarios, autenticacion, recuperacion de contrasena y tokens JWT. |

## Puertos del Proyecto

| Componente | Puerto Host | Puerto Contenedor | Descripcion |
|---|---:|---:|---|
| API Gateway | 8080 | 8080 | API publica del sistema |
| Product Service | 8081 | 8081 | Microservicio de productos |
| Cart Service | 8082 | 8082 | Microservicio de carrito |
| Checkout Service | 8083 | 8083 | Microservicio de checkout |
| Users Service | 8084 | 8084 | Microservicio de usuarios |
| NATS | 4222 | 4222 | Broker de mensajeria |
| PostgreSQL Productos | 5432 | 5432 | Base de datos `product_db` |
| PostgreSQL Checkout | 5433 | 5432 | Base de datos `checkout_db` |
| PostgreSQL Usuarios | 5434 | 5432 | Base de datos `users_db` |
| Redis | 6379 | 6379 | Almacenamiento temporal del carrito |

## Requisitos Previos

Para ejecutar el proyecto con contenedores se requiere:

- Docker instalado.
- Docker Compose instalado o disponible como plugin de Docker.
- Git instalado para clonar el repositorio.
- Puertos `8080`, `8081`, `8082`, `8083`, `8084`, `4222`, `5432`, `5433`, `5434` y `6379` disponibles.

Para ejecutar o compilar localmente sin Docker se requiere adicionalmente:

- Java 25.
- Maven o el wrapper incluido en el proyecto.
- PostgreSQL, Redis y NATS ejecutandose localmente.

## Ejecucion con Docker Compose

### 1. Clonar el repositorio

```bash
git clone https://github.com/dannymateo/eco-store-microservice-backend.git
cd eco-store-microservice-backend
```

### 2. Construir y levantar todo el ecosistema

```bash
docker compose up -d --build
```

Este comando construye las imagenes de los microservicios y levanta los contenedores definidos en `docker-compose.yml`.

### 3. Verificar contenedores activos

```bash
docker compose ps
```

Se deben observar contenedores similares a:

```text
eco-store-api-gateway
eco-store-product-service
eco-store-cart-service
eco-store-checkout-service
eco-store-users-service
eco-store-nats
eco-store-product-db
eco-store-checkout-db
eco-store-users-db
eco-store-cart-redis
```

### 4. Revisar logs generales

```bash
docker compose logs -f
```

### 5. Revisar logs de un servicio especifico

```bash
docker compose logs -f api-gateway
docker compose logs -f product-service
docker compose logs -f cart-service
docker compose logs -f checkout-service
docker compose logs -f users-service
```

### 6. Detener el entorno

```bash
docker compose down
```

### 7. Detener el entorno y eliminar volumenes

Use este comando solamente si desea borrar los datos persistidos en las bases de datos y Redis.

```bash
docker compose down -v
```

## Verificacion del Entorno

Una vez levantados los contenedores, el punto principal de entrada es:

```text
http://localhost:8080
```

La documentacion Swagger del API Gateway esta disponible en:

```text
http://localhost:8080/swagger-ui.html
```

Tambien se puede consultar la especificacion OpenAPI en:

```text
http://localhost:8080/v1/api-docs
```

## Endpoints Principales

Todos los endpoints se consumen desde el API Gateway en el puerto `8080`.

### Autenticacion

| Metodo | Endpoint | Descripcion |
|---|---|---|
| POST | `/api/v1/auth/register` | Registrar usuario |
| POST | `/api/v1/auth/login` | Iniciar sesion |
| POST | `/api/v1/auth/forgot-password` | Solicitar recuperacion de contrasena |
| POST | `/api/v1/auth/reset-password` | Restablecer contrasena |

### Usuarios

| Metodo | Endpoint | Descripcion |
|---|---|---|
| POST | `/api/v1/users` | Crear usuario |
| GET | `/api/v1/users/{id}` | Consultar usuario por ID |
| PUT | `/api/v1/users/{id}` | Actualizar usuario |

### Productos

| Metodo | Endpoint | Descripcion |
|---|---|---|
| POST | `/api/v1/products` | Crear producto |
| GET | `/api/v1/products` | Listar productos |
| GET | `/api/v1/products/{id}` | Consultar producto por ID |
| PUT | `/api/v1/products/{id}` | Actualizar producto |
| DELETE | `/api/v1/products/{id}` | Eliminar producto |

### Carrito

| Metodo | Endpoint | Descripcion |
|---|---|---|
| POST | `/api/v1/carts/{cartId}/items` | Agregar producto al carrito |
| GET | `/api/v1/carts/{cartId}` | Consultar carrito |
| DELETE | `/api/v1/carts/{cartId}/items/{productId}` | Eliminar producto del carrito |
| POST | `/api/v1/carts/{cartId}/checkout` | Cerrar carrito |

### Checkout

| Metodo | Endpoint | Descripcion |
|---|---|---|
| POST | `/api/v1/checkouts/process` | Procesar checkout |
| POST | `/api/v1/checkouts/{checkoutId}/confirm` | Confirmar checkout |

## Flujo de Prueba Recomendado

Los siguientes comandos permiten validar el flujo principal desde el API Gateway.

### 1. Crear un usuario

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"cliente@test.com","password":"Password123!","role":"CLIENT"}'
```

### 2. Iniciar sesion

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"cliente@test.com","password":"Password123!"}'
```

### 3. Listar productos

```bash
curl http://localhost:8080/api/v1/products
```

### 4. Crear un producto

```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Camiseta Eco Bamboo","description":"Camiseta de fibra de bamboo","category":"NORMAL","originalPrice":79900,"stock":20}'
```

### 5. Agregar producto al carrito

```bash
curl -X POST http://localhost:8080/api/v1/carts/cart-123/items \
  -H "Content-Type: application/json" \
  -d '{"productId":1,"quantity":2}'
```

### 6. Consultar carrito

```bash
curl http://localhost:8080/api/v1/carts/cart-123
```

### 7. Cerrar carrito

```bash
curl -X POST http://localhost:8080/api/v1/carts/cart-123/checkout
```

### 8. Procesar checkout

```bash
curl -X POST http://localhost:8080/api/v1/checkouts/process \
  -H "Content-Type: application/json" \
  -d '{"cartId":"cart-123"}'
```

### 9. Confirmar checkout

```bash
curl -X POST http://localhost:8080/api/v1/checkouts/1/confirm
```

## Arquitectura Hexagonal

El proyecto separa la logica de negocio de los detalles de infraestructura mediante arquitectura hexagonal. Esta separacion facilita probar los casos de uso sin depender directamente de bases de datos, broker de mensajeria o frameworks HTTP.

La organizacion general por capas es:

| Capa | Responsabilidad |
|---|---|
| Dominio | Entidades, objetos de valor, reglas de negocio y estados propios del sistema. |
| Aplicacion | Casos de uso y puertos de entrada/salida. Coordina el flujo de negocio. |
| Adaptadores de entrada | Reciben comandos externos, principalmente desde NATS o HTTP segun el modulo. |
| Adaptadores de salida | Implementan persistencia, mensajeria y comunicacion con otros servicios. |
| Infraestructura | Configuracion de Spring Boot, NATS, Redis, PostgreSQL y Docker. |

En el microservicio de checkout se evidencia especialmente este enfoque:

- `domain/model`: modelo de checkout, estados, items y dinero.
- `application/port/in`: casos de uso de procesamiento y confirmacion.
- `application/port/out`: contratos hacia carrito, productos, pagos y repositorio.
- `application/service`: orquestacion del flujo de checkout.
- `infrastructure/adapter/in`: entrada por comandos NATS.
- `infrastructure/adapter/out`: adaptadores de PostgreSQL, carrito, productos y proveedor de pago.

## Mensajeria NATS

NATS funciona como broker de mensajeria entre el API Gateway y los microservicios. El Gateway recibe solicitudes HTTP y las envia a los servicios usando subjects definidos.

Subjects principales:

| Servicio | Subject | Descripcion |
|---|---|---|
| Auth | `v1.auth.login` | Login de usuario |
| Auth | `v1.auth.register` | Registro de usuario |
| Auth | `v1.auth.forgot-password` | Recuperacion de contrasena |
| Auth | `v1.auth.reset-password` | Cambio de contrasena |
| User | `v1.user.create` | Crear usuario |
| User | `v1.user.get` | Consultar usuario |
| User | `v1.user.update` | Actualizar usuario |
| Product | `v1.catalog.product.create` | Crear producto |
| Product | `v1.catalog.product.get` | Consultar producto |
| Product | `v1.catalog.product.list` | Listar productos |
| Product | `v1.catalog.product.update` | Actualizar producto |
| Product | `v1.catalog.product.delete` | Eliminar producto |
| Cart | `v1.cart.add-product` | Agregar producto al carrito |
| Cart | `v1.cart.remove-product` | Eliminar producto del carrito |
| Cart | `v1.cart.get` | Consultar carrito |
| Cart | `v1.cart.checkout` | Cerrar carrito |
| Checkout | `v1.checkout.process` | Procesar checkout |
| Checkout | `v1.checkout.confirm` | Confirmar checkout |

## Persistencia

Cada servicio usa la tecnologia de almacenamiento mas adecuada para su responsabilidad.

| Servicio | Tecnologia | Motivo |
|---|---|---|
| Product Service | PostgreSQL | Datos estructurados de catalogo, stock y precios. |
| Checkout Service | PostgreSQL | Informacion transaccional de ordenes y confirmaciones. |
| Users Service | PostgreSQL | Usuarios, roles, estado y credenciales cifradas. |
| Cart Service | Redis | Informacion temporal del carrito con acceso rapido. |

Las credenciales usadas por defecto en Docker Compose son de entorno local academico y estan definidas en `docker-compose.yml`.

## Comandos Utiles

### Levantar entorno completo

```bash
docker compose up -d --build
```

### Ver estado de contenedores

```bash
docker compose ps
```

### Ver logs de todos los servicios

```bash
docker compose logs -f
```

### Reconstruir un servicio especifico

```bash
docker compose build product-service
docker compose up -d product-service
```

### Detener contenedores

```bash
docker compose down
```

### Limpiar contenedores y volumenes

```bash
docker compose down -v
```

### Compilar con Maven en Windows

```bash
mvnw.cmd clean package
```

### Compilar con Maven en Linux/macOS

```bash
./mvnw clean package
```

### Compilar un modulo especifico

```bash
./mvnw -pl api-gateway -am clean package
./mvnw -pl product-service -am clean package
./mvnw -pl cart-service -am clean package
./mvnw -pl checkout-service -am clean package
./mvnw -pl users-service -am clean package
```

En Windows se puede usar `mvnw.cmd` en lugar de `./mvnw`.

## Solucion de Problemas

### Docker no reconoce el comando `docker compose`

Verifique que Docker Desktop este instalado y en ejecucion. En versiones antiguas puede usarse:

```bash
docker-compose up -d --build
```

### Un puerto ya esta ocupado

Revise si otro proceso usa alguno de estos puertos: `8080`, `8081`, `8082`, `8083`, `8084`, `4222`, `5432`, `5433`, `5434` o `6379`.

Solucion recomendada:

```bash
docker compose down
docker compose up -d --build
```

Si el puerto sigue ocupado, cierre el proceso externo o cambie el mapeo de puertos en `docker-compose.yml`.

### El API Gateway responde error 500

Revise que NATS y el microservicio destino esten activos:

```bash
docker compose ps
docker compose logs -f api-gateway
docker compose logs -f nats
```

### Un microservicio no responde

Revise sus logs individuales:

```bash
docker compose logs -f product-service
docker compose logs -f cart-service
docker compose logs -f checkout-service
docker compose logs -f users-service
```

### Se necesita reiniciar desde cero

Este comando elimina contenedores y volumenes. Se perderan los datos persistidos localmente.

```bash
docker compose down -v
docker compose up -d --build
```

## Documentacion Relacionada

- Contratos del API Gateway: `docs/API_GATEWAY_CONTRACTS.md`
- Informe tecnico: `Informe tecnico EcoStore.pdf`
- Taller practico: `Taller Practico_ Arquitectura Hexagonal, Contenedores y Documentacion RFC.pdf`
- Repositorio: `https://github.com/dannymateo/eco-store-microservice-backend`
- Modelo C4: `https://drive.google.com/drive/folders/1-cAOOtzIkIXFxkPaj-P24fn2DiOVgjwX?usp=sharing`

## Integrantes

- Duban Guerra Castro
- Kevin Santiago Martinez Molina
- Daniel Zapata Ramirez

## Entrega Academica

Para la entrega final se recomienda comprimir en un archivo `.zip` los siguientes elementos:

- Codigo fuente completo.
- `docker-compose.yml`.
- `Dockerfile` de cada microservicio.
- `README.md` actualizado.
- Informe tecnico en PDF.
- Documento del taller en PDF.
- Imagenes o enlaces de los diagramas C4.

El comando base para levantar el proyecto es:

```bash
docker compose up -d --build
```
