# DynamicQueue - Discord Bot Project

Un bot de Discord desarrollado en Java que implementa un sistema de gestión de usuarios con diferentes niveles de permisos y funcionalidades de cola dinámica.

**Proyecto desarrollado por Fabricio YV - 2025**
## Características

- **Sistema de usuarios jerárquico**: Implementa diferentes tipos de usuarios (Administradores y Usuarios Regulares)
- **Gestión automática de permisos**: Detecta automáticamente los permisos de Discord para asignar roles
- **Sistema de colas dinámicas**: Funcionalidades de gestión de colas para usuarios
- **Configuración flexible**: Sistema de configuración centralizado
- **Arquitectura modular**: Diseño basado en patrones de diseño para facilitar el mantenimiento
- **Integración con base de datos**: Soporte para almacenamiento persistente
- **Sistema de logging**: Registro de actividades del bot

## Arquitectura del Proyecto

### Estructura de Paquetes

### Sistema de Usuarios

El proyecto implementa un sistema de usuarios basado en jerarquía:

#### BaseUser (Clase Base)
- Clase abstracta que define la estructura básica de todos los usuarios
- Contiene propiedades comunes como ID de Discord y nombre de usuario

#### Administrator
- Extiende de BaseUser
- Usuarios con permisos administrativos
- Puede ser propietario del bot o tener permisos de administrador en Discord

#### RegularUser
- Extiende de BaseUser
- Usuarios estándar sin permisos especiales

### UserFactory (Patrón Factory)

La clase `UserFactory` implementa el patrón Factory Method para crear instancias de usuarios:

#### Funcionalidades:

1. **Detección de Propietario del Bot**
    - Compara el ID de Discord con el ID del propietario configurado
    - Crea un Administrator con privilegios de propietario

2. **Detección de Permisos Administrativos**
    - Verifica permisos nativos de Discord (ADMINISTRATOR, MANAGE_SERVER)
    - Analiza roles que contengan "admin" o "mod" en el nombre
    - Crea un Administrator sin privilegios de propietario

3. **Usuario Regular por Defecto**
    - Si no cumple las condiciones anteriores, crea un RegularUser

#### Lógica de Permisos:

```java
// Orden de prioridad:
1. Bot Owner (máxima prioridad)
2. Permisos de Discord (ADMINISTRATOR/MANAGE_SERVER)
3. Roles con nombres que contengan "admin" o "mod"
4. Usuario regular (por defecto)
```

### Configuración Requerida
Antes de ejecutar el bot, debes configurar los siguientes valores en : `ConfigManager.java`

## Dependencias
### Principales
- **JDA (Java Discord API)**: Biblioteca para interactuar con la API de Discord
- **Java 21**: Versión mínima requerida
- **Maven**: Sistema de gestión de dependencias

### Base de Datos
- Soporte para conexión a base de datos (configuración requerida)

