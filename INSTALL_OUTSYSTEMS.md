# 🚀 Instalación del Plugin MultiBrowser en OutSystems

## 📋 Prerrequisitos

- **OutSystems 11.0+** con capacidades de desarrollo móvil
- **Cordova CLI** instalado en tu entorno de desarrollo
- **Git** para clonar el repositorio

## 🔧 Instalación desde Repositorio

### Opción 1: Instalación Directa desde Git (Recomendada)

```bash
# En tu proyecto Cordova/OutSystems
cordova plugin add https://github.com/[tu-usuario]/cordova-outsystems-inappbrowser.git
```

### Opción 2: Clonar y Instalar Localmente

```bash
# 1. Clonar el repositorio
git clone https://github.com/[tu-usuario]/cordova-outsystems-inappbrowser.git

# 2. Navegar al directorio
cd cordova-outsystems-inappbrowser

# 3. Instalar dependencias
npm install

# 4. Construir el plugin
npm run build

# 5. Instalar en tu proyecto
cordova plugin add .
```

### Opción 3: Instalación Manual (Para Casos Especiales)

1. **Clonar el repositorio** en tu máquina local
2. **Construir el plugin**: `npm run build`
3. **Copiar archivos** a tu proyecto OutSystems:
   ```
   src/ios/ → platforms/ios/[TuApp]/Plugins/multibrowser/
   src/android/ → platforms/android/app/src/main/kotlin/com/outsystems/plugins/inappbrowser/osinappbrowser/
   dist/ → www/plugins/multibrowser/
   ```

## 📱 Configuración en OutSystems

### 1. Agregar el Plugin en config.xml

```xml
<feature name="multibrowser">
    <param name="ios-package" value="HiddenInAppBrowser" />
    <param name="android-package" value="com.outsystems.plugins.inappbrowser.osinappbrowser.HiddenInAppBrowser" />
</feature>
```

### 2. Incluir el JavaScript

```html
<!-- En tu archivo HTML principal -->
<script src="cordova.js"></script>
<script src="plugins/multibrowser/plugin.js"></script>
```

### 3. Verificar Permisos

```xml
<!-- En config.xml -->
<allow-intent href="http://*/*" />
<allow-intent href="https://*/*" />
<allow-navigation href="*" />
```

## 🔧 Uso en OutSystems

### JavaScript Básico

```javascript
// Esperar a que Cordova esté listo
document.addEventListener(
  "deviceready",
  function () {
    // Verificar que el plugin esté disponible
    if (window.HiddenInAppBrowser) {
      console.log("Plugin MultiBrowser disponible");
    }
  },
  false
);

// Abrir WebView visible
function abrirWebView() {
  window.HiddenInAppBrowser.openInWebView("https://www.google.com", {
    success: function () {
      console.log("WebView abierto exitosamente");
    },
    error: function (error) {
      console.error("Error al abrir WebView:", error);
    },
  });
}

// Abrir WebView oculto
function abrirWebViewOculto() {
  window.HiddenInAppBrowser.open("https://www.google.com", {
    success: function () {
      console.log("WebView oculto abierto");
    },
  });
}

// Abrir en navegador externo
function abrirNavegadorExterno() {
  window.HiddenInAppBrowser.openInExternalBrowser("https://www.google.com");
}
```

### TypeScript (Si usas TypeScript)

```typescript
// Importar tipos
import { HiddenInAppBrowserOpenOptions } from "./plugins/multibrowser/definitions";

// Definir opciones
const options: HiddenInAppBrowserOpenOptions = {
  success: () => console.log("WebView abierto"),
  error: (error) => console.error("Error:", error),
};

// Usar el plugin
window.HiddenInAppBrowser.openInWebView("https://www.google.com", options);
```

## 🐛 Solución de Problemas Comunes

### Error: "Plugin not found"

```bash
# Verificar instalación
cordova plugin list

# Reinstalar si es necesario
cordova plugin remove multibrowser
cordova plugin add https://github.com/[tu-usuario]/cordova-outsystems-inappbrowser.git
```

### Error: "Method not found"

- Verificar que `cordova.js` esté incluido
- Verificar que el plugin esté en `cordova plugin list`
- Recompilar la app: `cordova build`

### WebView no se muestra

- Verificar permisos en `config.xml`
- Verificar que la URL sea válida
- Revisar logs de la consola

### Problemas de Compilación

```bash
# Limpiar y reconstruir
cordova clean
cordova prepare
cordova build
```

## 📱 Verificación de Instalación

### 1. Verificar Plugin Instalado

```bash
cordova plugin list | grep multibrowser
```

### 2. Verificar Archivos en Plataforma

```bash
# iOS
ls platforms/ios/[TuApp]/Plugins/multibrowser/

# Android
ls platforms/android/app/src/main/kotlin/com/outsystems/plugins/inappbrowser/osinappbrowser/
```

### 3. Verificar JavaScript

```bash
ls www/plugins/multibrowser/
```

## 🔄 Actualizaciones

Para actualizar el plugin:

```bash
# Remover versión anterior
cordova plugin remove multibrowser

# Instalar nueva versión
cordova plugin add https://github.com/[tu-usuario]/cordova-outsystems-inappbrowser.git

# Recompilar
cordova build
```

## 📞 Soporte

- **Issues**: Crear issue en GitHub
- **Documentación**: Revisar README.md principal
- **OutSystems**: Consultar documentación oficial de plugins

## 🔗 Enlaces Útiles

- [Repositorio del Plugin](https://github.com/[tu-usuario]/cordova-outsystems-inappbrowser)
- [Documentación de Cordova](https://cordova.apache.org/docs/en/latest/)
- [Guía de Plugins de OutSystems](https://success.outsystems.com/Documentation/11/Extensibility_and_Integration/Mobile_App_Development/Developing_Plugins)
