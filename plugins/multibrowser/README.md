# Multibrowser Plugin

Plugin de Cordova para OutSystems que proporciona funcionalidad de navegador oculto.

## Estructura del Plugin

```
multibrowser/
├── dist/
│   └── plugin.js          # Archivo JavaScript del plugin
├── src/
│   ├── android/
│   │   └── HiddenInAppBrowser.kt  # Implementación Android
│   └── ios/
│       ├── HiddenInAppBrowser.h   # Archivo de cabecera iOS
│       ├── HiddenInAppBrowser.swift  # Implementación iOS
│       └── HiddenInAppBrowserInputArgumentsModel.swift  # Modelo de argumentos
├── plugin.xml             # Configuración del plugin
├── package.json           # Metadatos del plugin
└── README.md              # Este archivo
```

## Instalación

### En un proyecto Cordova:

```bash
cordova plugin add ./plugins/multibrowser
```

### En OutSystems:

1. Copia la carpeta `multibrowser` al directorio `plugins/` de tu proyecto OutSystems
2. Asegúrate de que el `plugin.xml` esté configurado correctamente
3. Reconstruye la aplicación

## Uso

### JavaScript:

```javascript
// Verificar que el plugin esté disponible
if (typeof cordova !== 'undefined' && cordova.plugins && cordova.plugins.multibrowser) {
    console.log('Plugin multibrowser disponible');
}

// Abrir URL en WebView
cordova.plugins.multibrowser.openInWebView(
    'https://example.com',
    '_blank',
    'location=yes,toolbar=yes,hardwareback=yes',
    function(success) {
        console.log('WebView abierto exitosamente');
    },
    function(error) {
        console.error('Error al abrir WebView:', error);
    }
);

// Abrir URL en modo oculto
cordova.plugins.multibrowser.openHidden(
    'https://example.com',
    '_blank',
    '',
    function(success) {
        console.log('Modo oculto activado');
    },
    function(error) {
        console.error('Error en modo oculto:', error);
    }
);

// Abrir URL en navegador externo
cordova.plugins.multibrowser.openInExternalBrowser(
    'https://example.com',
    '_system',
    'location=yes,toolbar=yes',
    function(success) {
        console.log('Navegador externo abierto');
    },
    function(error) {
        console.error('Error al abrir navegador externo:', error);
    }
);
```

### En OutSystems (JavaScript Action):

```javascript
require(["PluginManager"], function (module) {
    function waitForPlugin(callback, maxAttempts = 10) {
        let attempts = 0;
        function checkPlugin() {
            attempts++;
            console.log(`🔍 Checking for plugin (attempt ${attempts}/${maxAttempts})`);
            if (typeof cordova !== "undefined" && cordova.plugins && cordova.plugins.multibrowser) {
                console.log("✅ Plugin found!");
                callback();
            } else if (attempts < maxAttempts) {
                console.log("⏳ Plugin not ready, retrying in 500ms...");
                setTimeout(checkPlugin, 500);
            } else {
                console.log("❌ Plugin not available after maximum attempts");
                $parameters.Success = false;
                $parameters.ErrorMessage = "Plugin not available after timeout";
                $resolve();
            }
        }
        checkPlugin();
    }

    function onSuccess() {
        console.log("✅ Success callback");
        $parameters.Success = true;
        $resolve();
    }

    function onError(error) {
        console.log("❌ Error callback:", error);
        $parameters.Success = false;
        $parameters.ErrorCode = error.code || -1;
        $parameters.ErrorMessage = error.message || "Unknown error";
        $resolve();
    }

    if (!$parameters.URL) {
        console.log("❌ URL parameter is required");
        $parameters.Success = false;
        $parameters.ErrorMessage = "URL parameter is required";
        $resolve();
        return;
    }

    console.log("🚀 Starting multibrowser execution");
    console.log("📋 Parameters:", { URL: $parameters.URL, Target: $parameters.Target });

    waitForPlugin(function() {
        console.log("🚀 Plugin ready, executing function");
        
        if ($parameters.Target === '_blank') {
            console.log("🌐 Opening in WebView (_blank)");
            cordova.plugins.multibrowser.openInWebView(
                $parameters.URL,
                undefined,
                "location=yes,toolbar=yes,hardwareback=yes",
                onSuccess,
                onError
            );
        } else if ($parameters.Target === '_self') {
            console.log("🌐 Opening in WebView (_self)");
            cordova.plugins.multibrowser.openInWebView(
                $parameters.URL,
                undefined,
                "location=yes,toolbar=yes,hardwareback=yes",
                onSuccess,
                onError
            );
        } else {
            console.log("🌐 Opening in external browser (_system)");
            cordova.plugins.multibrowser.openInExternalBrowser(
                $parameters.URL,
                undefined,
                "location=yes,toolbar=yes",
                onSuccess,
                onError
            );
        }
    });
});
```

## Funciones Disponibles

### `openInWebView(url, target, options, successCallback, errorCallback)`
Abre una URL en un WebView interno de la aplicación.

**Parámetros:**
- `url` (string): URL a abrir
- `target` (string): Target del WebView (opcional, por defecto '_blank')
- `options` (string): Opciones del WebView (opcional)
- `successCallback` (function): Callback de éxito
- `errorCallback` (function): Callback de error

### `openHidden(url, target, options, successCallback, errorCallback)`
Abre una URL en modo oculto (sin interfaz visible).

**Parámetros:** Igual que `openInWebView`

### `openInExternalBrowser(url, target, options, successCallback, errorCallback)`
Abre una URL en el navegador externo del dispositivo.

**Parámetros:** Igual que `openInWebView`

## Dependencias

- `cordova-plugin-inappbrowser` (para Android)

## Plataformas Soportadas

- Android (API 30+)
- iOS (11.0+)

## Notas de Implementación

### Android
- Implementado en Kotlin
- Usa WebView nativo de Android
- Soporte para JavaScript habilitado por defecto
- Manejo de errores HTTP con filtrado inteligente

### iOS
- Implementado en Swift 5
- Usa WKWebView nativo
- Compatible con iOS 11.0+
- Navegación con UINavigationController

## Solución de Problemas

### Plugin no disponible
- Verifica que el plugin esté instalado correctamente
- Espera a que Cordova esté listo (`deviceready`)
- Usa la función `waitForPlugin` para verificar disponibilidad

### JavaScript no funciona
- Verifica que JavaScript esté habilitado en el WebView
- Revisa la consola para errores
- Asegúrate de que el plugin se registre como `multibrowser`

### Errores de compilación
- Verifica las versiones de las herramientas de desarrollo
- Android: JDK 17+, Gradle 7.6+
- iOS: Xcode 16+, Swift 5

## Licencia

Apache-2.0
