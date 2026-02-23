# 📚 Biblioteca API - Sistema de Gestión de Biblioteca

API REST desarrollada con Spring Boot para la gestión de una biblioteca, incluyendo usuarios, libros y préstamos.

## 🎯 Características

- ✅ Arquitectura por capas (Controller, Service, Repository)
- ✅ Operaciones CRUD completas (GET, POST, PUT, DELETE)
- ✅ Relaciones JPA: 1:1 (Usuario-Perfil) y 1:N (Usuario-Préstamos, Libro-Préstamos)
- ✅ DTOs para separación de capas
- ✅ Validaciones con Bean Validation
- ✅ Tests unitarios (Mockito) y de integración (Testcontainers, MockMvc)
- ✅ Documentación con Swagger/OpenAPI
- ✅ Manejo global de excepciones
- ✅ Preparado para despliegue en la nube

## 🛠️ Tecnologías

- Java 17
- Spring Boot 3.2.1
- Spring Data JPA
- PostgreSQL / H2
- Lombok
- Swagger/OpenAPI
- JUnit 5 + Mockito
- Testcontainers
- Maven

## 📋 Requisitos Previos

- JDK 17 o superior
- Maven 3.6+
- Docker Desktop (para Testcontainers)
- PostgreSQL (para producción)

## 🚀 Instalación y Ejecución

### Modo Desarrollo (Base de datos H2 en memoria)

```bash
# Clonar el repositorio
git clone <tu-repositorio>
cd biblioteca-api

# Compilar el proyecto
mvn clean install

# Ejecutar la aplicación
mvn spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080`

### Modo Producción (PostgreSQL)

1. **Configurar variables de entorno:**

```bash
export DB_HOST=tu-endpoint-rds.amazonaws.com
export DB_PORT=5432
export DB_NAME=biblioteca
export DB_USERNAME=tu_usuario
export DB_PASSWORD=tu_password
```

2. **Ejecutar con perfil de producción:**

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

O empaquetar y ejecutar el JAR:

```bash
mvn clean package
java -jar -Dspring.profiles.active=prod target/biblioteca-api-1.0.0.jar
```

## 📚 Documentación de la API

Una vez iniciada la aplicación, accede a:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs (JSON)**: http://localhost:8080/api-docs

## 🗂️ Estructura del Proyecto

```
biblioteca-api/
├── src/
│   ├── main/
│   │   ├── java/com/biblioteca/api/
│   │   │   ├── controller/       # Controladores REST
│   │   │   ├── service/          # Lógica de negocio
│   │   │   ├── repository/       # Acceso a datos (JPA)
│   │   │   ├── entity/           # Entidades JPA
│   │   │   ├── dto/              # Data Transfer Objects
│   │   │   ├── exception/        # Manejo de excepciones
│   │   │   └── config/           # Configuraciones
│   │   └── resources/
│   │       ├── application.properties         # Configuración desarrollo
│   │       └── application-prod.properties    # Configuración producción
│   └── test/
│       └── java/com/biblioteca/api/
│           ├── service/          # Tests unitarios
│           ├── repository/       # Tests de integración (Testcontainers)
│           └── controller/       # Tests de integración (MockMvc)
└── pom.xml
```

## 🔗 Endpoints Principales

### 📖 Libros

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/libros` | Obtener todos los libros |
| GET | `/api/libros/{id}` | Obtener libro por ID |
| GET | `/api/libros/disponibles` | Obtener libros disponibles |
| GET | `/api/libros/genero/{genero}` | Obtener libros por género |
| POST | `/api/libros` | Crear nuevo libro |
| PUT | `/api/libros/{id}` | Actualizar libro |
| DELETE | `/api/libros/{id}` | Eliminar libro |

### 👤 Usuarios

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/usuarios` | Obtener todos los usuarios |
| GET | `/api/usuarios/{id}` | Obtener usuario por ID |
| POST | `/api/usuarios` | Crear nuevo usuario |
| PUT | `/api/usuarios/{id}` | Actualizar usuario |
| DELETE | `/api/usuarios/{id}` | Eliminar usuario |

### 🔖 Préstamos

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/prestamos` | Obtener todos los préstamos |
| GET | `/api/prestamos/{id}` | Obtener préstamo por ID |
| GET | `/api/prestamos/usuario/{usuarioId}/activos` | Obtener préstamos activos de un usuario |
| POST | `/api/prestamos` | Crear nuevo préstamo |
| PUT | `/api/prestamos/{id}/devolver` | Devolver libro prestado |
| DELETE | `/api/prestamos/{id}` | Eliminar préstamo |

## 🧪 Ejecutar Tests

```bash
# Todos los tests
mvn test

# Solo tests unitarios
mvn test -Dtest=*ServiceTest

# Solo tests de integración
mvn test -Dtest=*RepositoryTest,*ControllerTest
```

**Nota:** Para ejecutar los tests de integración, Docker debe estar en ejecución (Testcontainers).

## 📦 Despliegue en la Nube

### Opción 1: Railway (Recomendado)

1. Crear cuenta en [Railway.app](https://railway.app)
2. Conectar con GitHub
3. Configurar variables de entorno:
   - `DB_HOST`
   - `DB_PORT`
   - `DB_NAME`
   - `DB_USERNAME`
   - `DB_PASSWORD`
   - `SPRING_PROFILES_ACTIVE=prod`
4. Railway detectará automáticamente el proyecto Maven

### Opción 2: AWS Elastic Beanstalk

```bash
# Crear archivo .jar
mvn clean package

# Subir el JAR a Elastic Beanstalk
# Configurar variables de entorno en la consola de AWS
```

### Opción 3: Render.com

1. Crear cuenta en [Render.com](https://render.com)
2. Conectar repositorio
3. Configurar como "Web Service"
4. Build Command: `mvn clean package`
5. Start Command: `java -jar target/biblioteca-api-1.0.0.jar`

## 📊 Modelo de Datos

### Relación 1:1 (Usuario ↔ Perfil)
- Un usuario tiene un perfil
- Un perfil pertenece a un usuario

### Relación 1:N (Usuario → Préstamos)
- Un usuario puede tener muchos préstamos
- Un préstamo pertenece a un usuario

### Relación 1:N (Libro → Préstamos)
- Un libro puede tener muchos préstamos
- Un préstamo está asociado a un libro

## 📝 Ejemplos de Uso

### Crear un Usuario con Perfil

```json
POST /api/usuarios
{
  "nombre": "Juan Pérez",
  "email": "juan@email.com",
  "telefono": "123456789",
  "perfil": {
    "direccion": "Calle Principal 123",
    "ciudad": "Madrid",
    "codigoPostal": "28001",
    "preferenciasLectura": "Ficción, Historia"
  }
}
```

### Crear un Libro

```json
POST /api/libros
{
  "titulo": "El Quijote",
  "isbn": "978-8420412146",
  "autor": "Miguel de Cervantes",
  "anioPublicacion": 1605,
  "genero": "Novela",
  "disponible": true
}
```

### Realizar un Préstamo

```json
POST /api/prestamos
{
  "usuarioId": 1,
  "libroId": 1
}
```

### Devolver un Libro

```
PUT /api/prestamos/1/devolver
```

## 🐛 Solución de Problemas

### Error de conexión a la base de datos
- Verificar que PostgreSQL esté en ejecución
- Comprobar las credenciales en `application-prod.properties`
- Verificar que el puerto no esté bloqueado por firewall

### Tests fallan con Testcontainers
- Asegurarse de que Docker Desktop esté en ejecución
- Verificar que Docker tenga permisos suficientes

### Puerto 8080 ya en uso
```bash
# Cambiar el puerto en application.properties
server.port=8081
```

## 👥 Autor

Proyecto desarrollado para la 2ª Evaluación - IES Álvaro Falomir

## 📄 Licencia

Este proyecto es de uso educativo.
