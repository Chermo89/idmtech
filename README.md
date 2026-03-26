# 🚀 Proyecto Spring Boot WebFlux

API REST reactiva desarrollada con Spring Boot WebFlux.
Permite la gestión de clientes utilizando programación no bloqueante (reactiva).

---

## Stack Tecnológico

* Java 17
* Spring Boot 3.5
* Spring WebFlux
* Spring Data R2DBC
* H2 Database (Driver R2DBC)
* Maven
* Docker
* JaCoCo (cobertura de pruebas)
* JUnit 5 / Mockito

---

## Compilación del proyecto

Para compilar el proyecto y generar el archivo `.jar`:

```bash
mvn clean package
```

El archivo generado estará en:

```
target/*.jar
```

---

## Ejecución con Docker

### 1. Construir la imagen Docker

```bash
docker build -t spring-webflux-app .
```

### 2. Ejecutar el contenedor

```bash
docker run -p 8080:8080 spring-webflux-app
```

La aplicación estará disponible en:

```
http://localhost:8080
```

---

## Ejecución de pruebas y cobertura

Para ejecutar los tests y generar el reporte de cobertura:

```bash
mvn clean verify
```

El reporte de cobertura JaCoCo se genera en:

```
target/site/jacoco/index.html
```

Abrir este archivo en el navegador para visualizar la cobertura.

---

## 🌐 Endpoints disponibles

### Crear cliente

```bash
curl -X POST http://localhost:8080/clientes \
-H "Content-Type: application/json" \
-d '{"nombre": "Juan ggg", "email": "juanggg@gmail.com"}'
```

---

### Listar clientes

```bash
curl http://localhost:8080/clientes
```

---

### Obtener cliente por ID (si aplica)

```bash
curl http://localhost:8080/clientes/{id}
```

---

## Colección Postman (opcional)

Si se incluye una colección de Postman:

```
/postman/collection.json
```

Puede ser importada en Postman para probar los endpoints.

---

## Arquitectura

El proyecto sigue una arquitectura en capas:

* **Controller** → Manejo de endpoints
* **Service** → Lógica de negocio
* **Repository** → Acceso a datos (reactivo)
* **Model** → Entidades

---

## Cobertura de pruebas

Se asegura un mínimo de cobertura en:

* Lógica de negocio (Service)
* Controlador (WebTestClient)
