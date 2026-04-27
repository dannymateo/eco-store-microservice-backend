# Documentación de Contratos del API Gateway

## Puerto: **8080**

---

## Auth Service (`/api/v1/auth`)

### 1. POST /login - Iniciar Sesión

**Endpoint:** `POST /api/v1/auth/login`

| Campo | Tipo | Requerido | Ejemplo |
|-------|------|----------|---------|
| email | String | ✅ | `"usuario@test.com"` |
| password | String | ✅ | `"Password123!"` |

**Cuerpo de solicitud:**
```json
{
  "email": "usuario@test.com",
  "password": "Password123!"
}
```

**Respuesta exitosa (200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 1,
  "role": "CLIENT"
}
```

**Errores:**
- 401: Credenciales inválidas

---

### 2. POST /register - Registrar Usuario

**Endpoint:** `POST /api/v1/auth/register`

| Campo | Tipo | Requerido | Ejemplo |
|-------|------|----------|---------|
| email | String | ✅ | `"nuevousuario@test.com"` |
| password | String | ✅ (≥8 caracteres) | `"Password123!"` |
| role | String | ✅ | `"CLIENT"` o `"ADMIN"` |

**Cuerpo de solicitud:**
```json
{
  "email": "nuevousuario@test.com",
  "password": "Password123!",
  "role": "CLIENT"
}
```

**Respuesta exitosa (201):**
```json
{
  "id": 2,
  "email": "nuevousuario@test.com",
  "role": "CLIENT"
}
```

**Errores:**
- 409: Email ya registrado

---

### 3. POST /forgot-password - Solicitar Recuperación de Contraseña

**Endpoint:** `POST /api/v1/auth/forgot-password`

| Campo | Tipo | Requerido | Ejemplo |
|-------|------|----------|---------|
| email | String | ✅ | `"usuario@test.com"` |

**Cuerpo de solicitud:**
```json
{
  "email": "usuario@test.com"
}
```

**Respuesta exitosa (200):**
```json
{
  "message": "Se ha enviado un email para restablecer la contraseña",
  "resetToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

> **Nota:** El campo `resetToken` se devuelve solo en entorno de desarrollo/testing. En producción, el token se envía por email al usuario.

**Errores:**
- 404: Usuario no encontrado

---

### 4. POST /reset-password - Restablecer Contraseña

**Endpoint:** `POST /api/v1/auth/reset-password`

| Campo | Tipo | Requerido | Ejemplo |
|-------|------|----------|---------|
| token | String | ✅ | `"eyJhbGciOiJIUzI1NiJ9..."` |
| newPassword | String | ✅ (≥8 caracteres) | `"NuevaContrasena456!"` |

**Cuerpo de solicitud:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "newPassword": "NuevaContrasena456!"
}
```

**Respuesta exitosa (200):**
```json
{
  "message": "Contraseña actualizada correctamente"
}
```

**Errores:**
- 401: Token expirado o inválido
- 400: Contraseña inválida

---

## User Service (`/api/v1/users`)

### 5. POST / - Crear Usuario

**Endpoint:** `POST /api/v1/users`

| Campo | Tipo | Requerido | Ejemplo |
|-------|------|----------|---------|
| email | String | ✅ | `"admin@test.com"` |
| password | String | ✅ (≥8 caracteres) | `"AdminPass123!"` |
| role | String | ✅ | `"ADMIN"` o `"CLIENT"` |

**Cuerpo de solicitud:**
```json
{
  "email": "admin@test.com",
  "password": "AdminPass123!",
  "role": "ADMIN"
}
```

**Respuesta exitosa (201):**
```json
{
  "id": 1,
  "email": "admin@test.com",
  "role": "ADMIN",
  "active": true
}
```

**Errores:**
- 409: Email ya existe

---

### 6. GET /{id} - Obtener Usuario por ID

**Endpoint:** `GET /api/v1/users/{id}`

**Parámetros de path:**
| Campo | Tipo | Ejemplo |
|-------|------|--------|
| id | Long | `1` |

**Respuesta exitosa (200):**
```json
{
  "id": 1,
  "email": "admin@test.com",
  "role": "ADMIN",
  "active": true
}
```

**Errores:**
- 404: Usuario no encontrado

---

### 7. PUT /{id} - Actualizar Usuario

**Endpoint:** `PUT /api/v1/users/{id}`

**Parámetros de path:**
| Campo | Tipo | Ejemplo |
|-------|------|--------|
| id | Long | `1` |

**Cuerpo de solicitud:**
| Campo | Tipo | Requerido | Ejemplo |
|-------|------|----------|---------|
| email | String | ❌ | `"nuevo@test.com"` |
| role | String | ❌ | `"ADMIN"` o `"CLIENT"` |
| active | Boolean | ❌ | `true` o `false` |

```json
{
  "email": "nuevo@test.com",
  "role": "ADMIN",
  "active": true
}
```

**Respuesta exitosa (200):**
```json
{
  "id": 1,
  "email": "nuevo@test.com",
  "role": "ADMIN",
  "active": true
}
```

---

## Product Service (`/api/v1/products`)

### 8. POST / - Crear Producto

**Endpoint:** `POST /api/v1/products`

| Campo | Tipo | Requerido | Ejemplo |
|-------|------|----------|---------|
| name | String | ✅ | `"Camiseta Eco Bamboo"` |
| description | String | ❌ | `"Camiseta de fibra de bamboo"` |
| category | String | ✅ | `"NORMAL"` o `"TEMPORADA_PASADA"` |
| originalPrice | BigDecimal | ✅ (≥0) | `79.90` |
| stock | Integer | ✅ (≥0) | `20` |

**Cuerpo de solicitud:**
```json
{
  "name": "Camiseta Eco Bamboo",
  "description": "Camiseta de fibra de bamboo, talla M",
  "category": "NORMAL",
  "originalPrice": 79.90,
  "stock": 20
}
```

**Respuesta exitosa (201):**
```json
{
  "id": 1,
  "name": "Camiseta Eco Bamboo",
  "description": "Camiseta de fibra de bamboo, talla M",
  "category": "NORMAL",
  "originalPrice": 79.90,
  "stock": 20
}
```

---

### 9. GET /{id} - Obtener Producto por ID

**Endpoint:** `GET /api/v1/products/{id}`

**Parámetros de path:**
| Campo | Tipo | Ejemplo |
|-------|------|--------|
| id | Long | `1` |

**Respuesta exitosa (200):** Mismo que create

---

### 10. GET / - Listar Productos

**Endpoint:** `GET /api/v1/products`

**Respuesta exitosa (200):**
```json
[
  {
    "id": 1,
    "name": "Producto 1",
    "category": "NORMAL",
    "originalPrice": 79.90,
    "stock": 20
  },
  {
    "id": 2,
    "name": "Producto 2",
    "category": "TEMPORADA_PASADA",
    "originalPrice": 49.90,
    "stock": 5
  }
]
```

---

### 11. PUT /{id} - Actualizar Producto

**Endpoint:** `PUT /api/v1/products/{id}`

**Parámetros de path:**
| Campo | Tipo | Ejemplo |
|-------|------|--------|
| id | Long | `1` |

| Campo | Tipo | Requerido | Ejemplo |
|-------|------|----------|---------|
| name | String | ✅ | `"Camiseta Eco Premium"` |
| description | String | ✅ | `"Version premium con costuras reforzadas"` |
| category | String | ✅ | `"TEMPORADA_PASADA"` |
| originalPrice | BigDecimal | ✅ | `99.90` |
| stock | Integer | ✅ | `12` |

**Cuerpo de solicitud:**
```json
{
  "name": "Camiseta Eco Premium",
  "description": "Version premium con costuras reforzadas",
  "category": "TEMPORADA_PASADA",
  "originalPrice": 99.90,
  "stock": 12
}
```

**Respuesta exitosa (200):** Mismo que create

---

### 12. DELETE /{id} - Eliminar Producto

**Endpoint:** `DELETE /api/v1/products/{id}`

**Respuesta exitosa (204):** Sin contenido

---

## Cart Service (`/api/v1/carts`)

### 13. POST /{cartId}/items - Agregar Producto al Carrito

**Endpoint:** `POST /api/v1/carts/{cartId}/items`

**Parámetros de path:**
| Campo | Tipo | Ejemplo |
|-------|------|--------|
| cartId | String | `"cart-123"` |

| Campo | Tipo | Requerido | Ejemplo |
|-------|------|----------|---------|
| productId | Long | ✅ | `1` |
| quantity | Integer (≥1) | ✅ | `2` |

**Cuerpo de solicitud:**
```json
{
  "productId": 1,
  "quantity": 2
}
```

**Respuesta exitosa (200):**
```json
{
  "cartId": "cart-123",
  "items": [
    {
      "productId": 1,
      "quantity": 2
    }
  ]
}
```

**Errores:**
- 404: Producto no encontrado
- 400: Stock insuficiente

---

### 14. DELETE /{cartId}/items/{productId} - Eliminar Item del Carrito

**Endpoint:** `DELETE /api/v1/carts/{cartId}/items/{productId}`

**Respuesta exitosa (200):** Mismo estructura

---

### 15. GET /{cartId} - Obtener Carrito

**Endpoint:** `GET /api/v1/carts/{cartId}`

**Respuesta exitosa (200):** Mismo estructura

---

### 16. POST /{cartId}/checkout - Cerrar Carrito

**Endpoint:** `POST /api/v1/carts/{cartId}/checkout`

**Respuesta exitosa (200):**
```json
{
  "cartId": "cart-123",
  "status": "CLOSED"
}
```

**Errores:**
- 409: Carrito ya cerrado o sin items

---

## Checkout Service (`/api/v1/checkouts`)

### 17. POST /process - Procesar Checkout

**Endpoint:** `POST /api/v1/checkouts/process`

| Campo | Tipo | Requerido | Ejemplo |
|-------|------|----------|---------|
| cartId | String | ✅ | `"cart-123"` |

**Cuerpo de solicitud:**
```json
{
  "cartId": "cart-123"
}
```

**Respuesta exitosa (201):**
```json
{
  "checkoutId": 1,
  "cartId": "cart-123",
  "status": "PROCESSED",
  "total": 159.80
}
```

---

### 18. POST /{checkoutId}/confirm - Confirmar Checkout

**Endpoint:** `POST /api/v1/checkouts/{checkoutId}/confirm`

**Parámetros de path:**
| Campo | Tipo | Ejemplo |
|-------|------|--------|
| checkoutId | Long | `1` |

**Respuesta exitosa (200):**
```json
{
  "checkoutId": 1,
  "status": "CONFIRMED",
  "paymentStatus": "PAID"
}
```

**Errores:**
- 409: Solo se pueden confirmar checkouts procesados

---

## Subjects NATS

| Microservicio | Subject | Descripción |
|--------------|---------|-------------|
| Auth | `v1.auth.login` | Login de usuario |
| Auth | `v1.auth.register` | Registro de usuario |
| Auth | `v1.auth.forgot-password` | Solicitar recuperación |
| Auth | `v1.auth.reset-password` | Restablecer contraseña |
| User | `v1.user.create` | Crear usuario |
| User | `v1.user.get` | Obtener usuario |
| User | `v1.user.update` | Actualizar usuario |
| Product | `v1.catalog.product.create` | Crear producto |
| Product | `v1.catalog.product.get` | Obtener producto |
| Product | `v1.catalog.product.list` | Listar productos |
| Product | `v1.catalog.product.update` | Actualizar producto |
| Product | `v1.catalog.product.delete` | Eliminar producto |
| Cart | `v1.cart.add-product` | Agregar producto |
| Cart | `v1.cart.remove-product` | Eliminar producto |
| Cart | `v1.cart.get` | Obtener carrito |
| Cart | `v1.cart.checkout` | Cerrar carrito |
| Checkout | `v1.checkout.process` | Procesar checkout |
| Checkout | `v1.checkout.confirm` | Confirmar checkout |

---

## Códigos de Estado HTTP

| Código | Descripción |
|--------|-------------|
| 200 | OK |
| 201 | Created |
| 204 | No Content |
| 400 | Bad Request |
| 401 | Unauthorized |
| 404 | Not Found |
| 409 | Conflict |
| 500 | Internal Server Error |