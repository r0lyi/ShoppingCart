# 🛒 Carrito de Compras Inteligente con Spring Boot y Memcached

![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.4-green.svg)
![Memcached](https://img.shields.io/badge/Memcached-1.6+-blue.svg)
![MySQL](https://img.shields.io/badge/MySQL-8.0-orange.svg)

Aplicación web moderna para gestión de carritos de compras con persistencia dual en MySQL y caché de alto rendimiento usando Memcached. Diseñada para optimizar el rendimiento en escenarios de alta demanda mediante estrategias avanzadas de caching.

## 🚀 Características Principales

### 🧠 Núcleo Inteligente de Caché
- **Caché Multinivel**: Combina almacenamiento persistente en MySQL con caché en memoria usando Memcached
- **Estrategias de Expiración**: Configuración granular de tiempos de vida para diferentes tipos de datos
- **Gestión de Sesiones**: Carritos temporales para usuarios no registrados con auto-expiración

### ✨ Funcionalidades Clave
- **🛍️ Carrito en Tiempo Real**
  - Adición/eliminación instantánea de productos
  - Conteo de artículos sin acceso a base de datos
  - Persistencia temporal para usuarios invitados
 
- **🔥 Productos Populares**
  - Cache automático de productos más visitados
  - Actualización en tiempo real de rankings
  - Pre-caching de productos frecuentes por usuario

- **🔍 Búsquedas Optimizadas**
  - Cache de resultados de búsquedas frecuentes
  - Invalidación automática al actualizar catálogo
  - Búsqueda en caché antes de consultar la base de datos

### ⚙️ Arquitectura Técnica
```mermaid
graph TD
    A[Cliente Web] --> B[Controlador Spring]
    B --> C{¿En Caché?}
    C -->|Sí| D[Memcached]
    C -->|No| E[MySQL]
    D --> F[Respuesta Rápida]
    E --> G[Actualizar Caché]
    G --> F
