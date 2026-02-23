# 🚀 GUÍA RÁPIDA DE INICIO

## ✅ LO QUE NECESITAS PARA DESPLEGAR

### 1️⃣ Base de Datos AWS RDS (Ya tienes el endpoint)
Anota estos datos:
```
DB_HOST=tu-endpoint.rds.amazonaws.com
DB_PORT=5432
DB_NAME=biblioteca
DB_USERNAME=postgres
DB_PASSWORD=tu_password
```

### 2️⃣ Plataforma de Despliegue (Elige UNA)

**OPCIÓN A: Railway.app** ⭐ LA MÁS FÁCIL
- Gratis hasta $5/mes
- No requiere configuración de servidor
- Deploy automático desde GitHub
- URL: https://railway.app

**OPCIÓN B: Render.com**
- Similar a Railway
- Gratis con algunas limitaciones
- URL: https://render.com

**OPCIÓN C: AWS Elastic Beanstalk**
- Si quieres todo en AWS
- Más configuración pero más control

**OPCIÓN D: AWS EC2**
- Manual pero completo control
- Requiere más conocimientos de Linux

---

## 🎯 PASOS PARA DESPLEGAR (Railway - Recomendado)

### Paso 1: Subir a GitHub
```bash
cd biblioteca-api
git init
git add .
git commit -m "Initial commit"
git remote add origin https://github.com/TU_USUARIO/biblioteca-api.git
git push -u origin main
```

### Paso 2: Desplegar en Railway
1. Ir a https://railway.app
2. Sign up con GitHub
3. Click "New Project"
4. "Deploy from GitHub repo"
5. Seleccionar tu repositorio `biblioteca-api`
6. Railway detectará automáticamente el proyecto Maven

### Paso 3: Configurar Variables de Entorno
En Railway → Variables, agregar:
```
DB_HOST=tu-endpoint-rds.amazonaws.com
DB_PORT=5432
DB_NAME=biblioteca
DB_USERNAME=postgres
DB_PASSWORD=tu_password_seguro
SPRING_PROFILES_ACTIVE=prod
```

### Paso 4: Esperar el Deploy
- Railway compilará automáticamente
- Te dará una URL pública
- Ejemplo: `https://biblioteca-api-production.up.railway.app`

### Paso 5: Probar la API
Acceder a Swagger:
```
https://tu-url-railway.up.railway.app/swagger-ui.html
```

---

## 💻 PARA DESARROLLO LOCAL

### Requisitos
- JDK 17+
- IntelliJ IDEA
- Docker Desktop (para tests)

### Ejecutar
```bash
# Abrir el proyecto en IntelliJ
# Click derecho en BibliotecaApiApplication.java → Run

# O desde terminal:
mvn spring-boot:run
```

Acceder a:
- Aplicación: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui.html
- Consola H2: http://localhost:8080/h2-console

---

## 📝 CONTENIDO DEL PROYECTO

### ✅ Cumple TODOS los requisitos
1. ✅ Arquitectura por capas (Controller/Service/Repository)
2. ✅ Relación 1:1 (Usuario-Perfil)
3. ✅ Relación 1:N (Usuario-Préstamos, Libro-Préstamos)
4. ✅ DTOs implementados
5. ✅ Endpoints GET, POST, PUT, DELETE
6. ✅ Tests Unitarios (LibroServiceTest)
7. ✅ Tests Integración Repository (Testcontainers)
8. ✅ Tests Integración Controller (MockMvc)
9. ✅ Swagger/OpenAPI configurado
10. ✅ Listo para desplegar en la nube

### 📦 Entidades del Sistema
- **Usuario** → tiene un **Perfil** (1:1)
- **Usuario** → tiene muchos **Préstamos** (1:N)
- **Libro** → tiene muchos **Préstamos** (1:N)

### 🔗 Endpoints Principales
```
GET    /api/libros                      - Todos los libros
POST   /api/libros                      - Crear libro
GET    /api/usuarios                    - Todos los usuarios
POST   /api/usuarios                    - Crear usuario con perfil
POST   /api/prestamos                   - Crear préstamo
PUT    /api/prestamos/{id}/devolver     - Devolver libro
```

---

## 🧪 EJECUTAR TESTS

```bash
# Asegúrate de tener Docker Desktop corriendo

# Ejecutar todos los tests
mvn test

# Solo tests unitarios
mvn test -Dtest=*ServiceTest

# Solo tests de integración
mvn test -Dtest=*RepositoryTest,*ControllerTest
```

---

## 📄 ARCHIVOS IMPORTANTES

- `README.md` - Documentación completa
- `DEPLOYMENT.md` - Guía detallada de despliegue AWS
- `postman_collection.json` - Colección Postman para probar
- `Dockerfile` - Para despliegue con Docker
- `pom.xml` - Dependencias del proyecto

---

## 🆘 SOLUCIÓN DE PROBLEMAS

### "No se puede conectar a la base de datos"
- Verificar que el endpoint de RDS es correcto
- Verificar Security Group permite conexiones en puerto 5432
- Verificar credenciales

### "Tests fallan"
- Asegurarse de que Docker Desktop está corriendo
- Los tests usan Testcontainers que necesita Docker

### "Puerto 8080 en uso"
- Cambiar puerto: `server.port=8081` en application.properties

---

## 📧 PARA LA DEFENSA

Prepara:
1. ✅ Mostrar el código en IntelliJ
2. ✅ Mostrar Swagger funcionando (tu URL desplegada)
3. ✅ Demostrar operaciones CRUD desde Swagger
4. ✅ Mostrar tests pasando (mvn test)
5. ✅ Explicar las relaciones 1:1 y 1:N en las entidades
6. ✅ Mostrar logs de la aplicación corriendo

---

## 🎓 EVALUACIÓN - 10 PUNTOS

| Punto | Requisito | ¿Cumple? |
|-------|-----------|----------|
| 1 | Relación 1:1 (Usuario-Perfil) | ✅ SÍ |
| 1 | Relación 1:N (Usuario/Libro-Préstamos) | ✅ SÍ |
| 1 | Endpoint GET | ✅ SÍ |
| 1 | Endpoint POST | ✅ SÍ |
| 1 | Endpoint PUT | ✅ SÍ |
| 1 | Endpoint DELETE | ✅ SÍ |
| 1 | Test Unitario Servicio | ✅ SÍ |
| 1 | Test Integración Repositorio/Controller | ✅ SÍ |
| 1 | Documentación API (Swagger) | ✅ SÍ |
| 1 | Despliegue en la nube | ⏳ Por hacer |

**TOTAL: 9/10** (10/10 cuando despliegues)

---

## 🎯 RESUMEN ULTRA-RÁPIDO

1. **Subir a GitHub** → Hacer push del código
2. **Ir a Railway.app** → Crear cuenta con GitHub
3. **Deploy from GitHub** → Seleccionar repo
4. **Configurar variables** → Pegar tus datos de AWS RDS
5. **Esperar deploy** → Railway lo hace todo automático
6. **Abrir Swagger** → `tu-url/swagger-ui.html`
7. **Probar endpoints** → Crear libros, usuarios, préstamos
8. **¡Listo!** → Proyecto completo funcionando

**Tiempo estimado: 15-20 minutos**

---

¡Éxito en tu defensa! 🚀
