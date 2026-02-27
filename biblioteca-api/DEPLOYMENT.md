# Guía de Despliegue en la Nube (AWS Elastic Beanstalk + GitHub Actions)

Cumpliendo con el requisito del enunciado *"Despliega la aplicación en un entorno en la nube de forma que se pueda acceder a la API de forma remota"*, la forma más directa y estandarizada es utilizando **AWS Elastic Beanstalk**.

Al no exigirse levantar un Servidor de Base de Datos externo (como AWS RDS) o una máquina virtual al vuelo paso a paso (EC2 manual), nos valdremos de **Elastic Beanstalk**. Este servicio de AWS "orquesta" automáticamente la máquina virtual en la nube (crea el EC2 internamente, los balanceadores, puertos, etc.) subiendo únicamente tu código.

Nuestra aplicación usará la base de datos **H2 en memoria** incluso en la nube, la cual cumple perfectamente el requisito del enunciado de "Diseñar una base de datos propia" mediante JPA.

## Paso 1: Configurar AWS Elastic Beanstalk

1. Accede a tu consola de AWS y busca **Elastic Beanstalk**.
2. Haz clic en **Create application** (Crear aplicación).
3. Selecciona:
   - **Environment tier**: Web server environment.
   - **Application information**: Nombre de la aplicación (ej. `biblioteca-api`).
   - **Platform**: `Java` (elige Corretto 17).
   - **Application code**: Selecciona *Sample application* de momento para que AWS cree la infraestructura por defecto.
4. Dale a **Create environment** y espera a que AWS levante el entorno. Se te asignará una URL terminada en `elasticbeanstalk.com`.
5. Ve a tu Perfil de Seguridad de tu AWS (IAM) y consigue un "Access Key" y un "Secret Key" de tu usuario de AWS. Esto le dará permiso a GitHub para conectarse en tu nombre.

## Paso 2: Configurar GitHub Secrets

En tu repositorio de GitHub, ve a **Settings > Secrets and variables > Actions** y añade los siguientes **New repository secrets**:

- `AWS_ACCESS_KEY_ID`: Tu access key de AWS.
- `AWS_SECRET_ACCESS_KEY`: Tu secret key de AWS.
- `AWS_REGION`: La región de despliegue, por ejemplo `us-east-1`
- `EB_APP_NAME`: El nombre de la aplicación que creaste (ej. `biblioteca-api`).
- `EB_ENV_NAME`: El nombre del entorno (ej. `Bibliotecaapi-env`).

## Paso 3: Workflow de GitHub Actions

Añade este archivo en tu proyecto bajo la ruta `.github/workflows/deploy.yml`:

```yaml
name: Deploy to AWS Elastic Beanstalk

on:
  push:
    branches:
      - main

jobs:
  deploy:
    runs-on: ubuntu-latest

    steps:
    - name: Checkout code
      uses: actions/checkout@v3

    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'corretto'

    - name: Build with Maven
      run: mvn clean package -DskipTests

    - name: Deploy to Elastic Beanstalk
      uses: einaregilsson/beanstalk-deploy@v21
      with:
        aws_access_key: ${{ secrets.AWS_ACCESS_KEY_ID }}
        aws_secret_key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
        application_name: ${{ secrets.EB_APP_NAME }}
        environment_name: ${{ secrets.EB_ENV_NAME }}
        version_label: ${{ github.sha }}
        region: ${{ secrets.AWS_REGION }}
        deployment_package: target/biblioteca-api-1.0.0.jar
```

## Paso 4: ¡Eso es todo!

Como todo ocurre automáticamente:
1. Haces un `git push` a la rama `main`.
2. Tu pestaña de **Actions** en GitHub empaquetará la app usando el archivo `pom.xml`.
3. Automáticamente conectará con tu **Elastic Beanstalk** enviando el `.jar`.
4. El servidor de Amazon lo ejecuta usando Java automáticamente por el puerto `8080` (predeterminado en Beanstalk).

Podrás acceder a la documentación de Swagger de tu API, completamente operativa, abriendo `http://TU-URL-DE-AWS.elasticbeanstalk.com/documentacion`.
