# 🚀 Guía de Despliegue en AWS

## Paso 1: Configurar Base de Datos AWS RDS (PostgreSQL)

### 1.1 Crear instancia RDS

1. Acceder a AWS Console → RDS
2. Click en "Create database"
3. Configuración:
   - **Engine**: PostgreSQL
   - **Version**: 15.x (recomendado)
   - **Template**: Free tier (para desarrollo) o Production
   - **DB instance identifier**: biblioteca-db
   - **Master username**: postgres
   - **Master password**: [tu_password_seguro]
   - **DB instance class**: db.t3.micro (Free tier) o superior
   - **Storage**: 20 GB (mínimo)
   - **Public access**: Yes (para poder conectar desde fuera)
   - **VPC security group**: Crear nuevo o usar existente

4. Click "Create database"

### 1.2 Configurar Security Group

1. Ir a la instancia RDS creada
2. Click en el Security Group asociado
3. Editar "Inbound rules"
4. Agregar regla:
   - **Type**: PostgreSQL
   - **Port**: 5432
   - **Source**: 0.0.0.0/0 (Anywhere) - Para desarrollo
   - Para producción: Limitar a IPs específicas

### 1.3 Obtener Endpoint

1. En la instancia RDS, copiar el **Endpoint**
2. Ejemplo: `biblioteca-db.xxxxx.us-east-1.rds.amazonaws.com`

### 1.4 Crear la Base de Datos

```bash
# Conectar a RDS usando psql
psql -h biblioteca-db.xxxxx.us-east-1.rds.amazonaws.com -U postgres -d postgres

# Crear base de datos
CREATE DATABASE biblioteca;

# Salir
\q
```

## Paso 2: Configurar Variables de Entorno

Guardar estos valores para usarlos en el despliegue:

```
DB_HOST=biblioteca-db.xxxxx.us-east-1.rds.amazonaws.com
DB_PORT=5432
DB_NAME=biblioteca
DB_USERNAME=postgres
DB_PASSWORD=tu_password
```

## Paso 3: Opciones de Despliegue

### Opción A: Railway (Más Fácil) ⭐ Recomendado

1. **Crear cuenta en Railway**
   - Ir a https://railway.app
   - Sign up con GitHub

2. **Crear nuevo proyecto**
   - Click "New Project"
   - "Deploy from GitHub repo"
   - Seleccionar tu repositorio

3. **Configurar variables de entorno**
   - En el proyecto, ir a "Variables"
   - Agregar:
     ```
     DB_HOST=tu-endpoint-rds.amazonaws.com
     DB_PORT=5432
     DB_NAME=biblioteca
     DB_USERNAME=postgres
     DB_PASSWORD=tu_password
     SPRING_PROFILES_ACTIVE=prod
     ```

4. **Desplegar**
   - Railway detectará automáticamente el proyecto Maven
   - Click "Deploy"
   - Esperar a que termine el build

5. **Obtener URL**
   - Railway te dará una URL pública
   - Ejemplo: `https://biblioteca-api-production.up.railway.app`

### Opción B: AWS Elastic Beanstalk

1. **Instalar EB CLI**
```bash
pip install awsebcli
```

2. **Inicializar EB**
```bash
cd biblioteca-api
eb init -p java-17 biblioteca-api --region us-east-1
```

3. **Crear entorno**
```bash
eb create biblioteca-api-env
```

4. **Configurar variables de entorno**
```bash
eb setenv DB_HOST=tu-endpoint-rds.amazonaws.com \
         DB_PORT=5432 \
         DB_NAME=biblioteca \
         DB_USERNAME=postgres \
         DB_PASSWORD=tu_password \
         SPRING_PROFILES_ACTIVE=prod
```

5. **Desplegar**
```bash
mvn clean package
eb deploy
```

6. **Abrir aplicación**
```bash
eb open
```

### Opción C: AWS ECS (Container)

1. **Construir imagen Docker**
```bash
docker build -t biblioteca-api .
```

2. **Crear repositorio ECR**
```bash
aws ecr create-repository --repository-name biblioteca-api
```

3. **Subir imagen a ECR**
```bash
# Autenticar
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin <tu-account-id>.dkr.ecr.us-east-1.amazonaws.com

# Tag
docker tag biblioteca-api:latest <tu-account-id>.dkr.ecr.us-east-1.amazonaws.com/biblioteca-api:latest

# Push
docker push <tu-account-id>.dkr.ecr.us-east-1.amazonaws.com/biblioteca-api:latest
```

4. **Crear cluster ECS y task definition**
   - Desde AWS Console
   - Configurar variables de entorno en la task definition

### Opción D: EC2 Manual

1. **Lanzar instancia EC2**
   - AMI: Amazon Linux 2
   - Instance type: t2.micro (Free tier)
   - Security Group: Abrir puerto 8080

2. **Conectar a la instancia**
```bash
ssh -i tu-key.pem ec2-user@tu-ip-publica
```

3. **Instalar Java 17**
```bash
sudo yum update -y
sudo yum install java-17-amazon-corretto -y
```

4. **Copiar el JAR**
```bash
# Desde tu máquina local
scp -i tu-key.pem target/biblioteca-api-1.0.0.jar ec2-user@tu-ip-publica:/home/ec2-user/
```

5. **Ejecutar la aplicación**
```bash
# En la instancia EC2
export DB_HOST=tu-endpoint-rds.amazonaws.com
export DB_PORT=5432
export DB_NAME=biblioteca
export DB_USERNAME=postgres
export DB_PASSWORD=tu_password

java -jar -Dspring.profiles.active=prod biblioteca-api-1.0.0.jar
```

6. **Configurar como servicio systemd** (opcional pero recomendado)
```bash
sudo nano /etc/systemd/system/biblioteca-api.service
```

Contenido:
```ini
[Unit]
Description=Biblioteca API
After=network.target

[Service]
Type=simple
User=ec2-user
Environment="DB_HOST=tu-endpoint-rds.amazonaws.com"
Environment="DB_PORT=5432"
Environment="DB_NAME=biblioteca"
Environment="DB_USERNAME=postgres"
Environment="DB_PASSWORD=tu_password"
ExecStart=/usr/bin/java -jar -Dspring.profiles.active=prod /home/ec2-user/biblioteca-api-1.0.0.jar
Restart=always

[Install]
WantedBy=multi-user.target
```

Habilitar y arrancar:
```bash
sudo systemctl enable biblioteca-api
sudo systemctl start biblioteca-api
sudo systemctl status biblioteca-api
```

## Paso 4: Verificar el Despliegue

1. **Acceder a Swagger**
   - `https://tu-url/swagger-ui.html`

2. **Probar endpoints**
```bash
# Crear un libro
curl -X POST https://tu-url/api/libros \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "1984",
    "isbn": "978-0451524935",
    "autor": "George Orwell",
    "anioPublicacion": 1949,
    "genero": "Distopía",
    "disponible": true
  }'

# Obtener todos los libros
curl https://tu-url/api/libros
```

3. **Verificar logs**
   - Railway: Ver logs en el dashboard
   - Elastic Beanstalk: `eb logs`
   - EC2: `sudo journalctl -u biblioteca-api -f`

## Paso 5: Monitoreo (Opcional)

### CloudWatch Logs (para EB y EC2)
1. Ir a AWS CloudWatch
2. Ver logs de la aplicación

### Railway Logs
- Directamente en el dashboard de Railway
- Logs en tiempo real

## Problemas Comunes

### No se puede conectar a RDS
- Verificar Security Group
- Verificar que el endpoint es correcto
- Verificar credenciales
- Ping al endpoint: `ping biblioteca-db.xxxxx.us-east-1.rds.amazonaws.com`

### Error de memoria
- Aumentar el heap de Java:
  ```bash
  java -Xmx512m -jar biblioteca-api-1.0.0.jar
  ```

### Puerto en uso
- Verificar que el puerto 8080 esté disponible
- Cambiar puerto con: `export SERVER_PORT=8081`

## Costos Estimados (AWS Free Tier)

- **RDS db.t3.micro**: Gratis primer año (750 horas/mes)
- **EC2 t2.micro**: Gratis primer año (750 horas/mes)
- **Elastic Beanstalk**: Gratis (pagas por recursos EC2/RDS)
- **Railway**: Gratis hasta $5/mes de crédito

## Seguridad en Producción

1. **Cambiar contraseña de RDS** a algo seguro
2. **Limitar acceso de Security Group** a IPs específicas
3. **Usar IAM roles** en lugar de credenciales hardcodeadas
4. **Habilitar SSL** en la conexión a RDS
5. **Configurar HTTPS** con certificado SSL/TLS

## Resultado Final

Tu URL final será algo como:
- Railway: `https://biblioteca-api-production.up.railway.app`
- Elastic Beanstalk: `http://biblioteca-api-env.xxxxx.us-east-1.elasticbeanstalk.com`
- EC2: `http://tu-ip-publica:8080`

Swagger disponible en: `[TU_URL]/swagger-ui.html`
