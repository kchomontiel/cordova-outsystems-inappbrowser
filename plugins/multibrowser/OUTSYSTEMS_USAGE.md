# Uso del Plugin Multibrowser en OutSystems

## Instalación en OutSystems

### 1. Copiar el Plugin
Copia la carpeta `multibrowser` al directorio `plugins/` de tu proyecto OutSystems.

### 2. Verificar Configuración
Asegúrate de que el `plugin.xml` esté configurado correctamente y que las dependencias estén disponibles.

### 3. Reconstruir la Aplicación
Después de agregar el plugin, reconstruye la aplicación para que OutSystems reconozca el nuevo plugin.

## Implementación en JavaScript Actions

### JavaScript Action: OpenInWebView

```javascript
require(["PluginManager"], function (module) {
    // Parámetros esperados:
    // $parameters.URL: string (URL a abrir)
    // $parameters.Target: string (_blank, _self, _system)
    // $parameters.Options: string (opciones del WebView)
    
    function waitForPlugin(callback, maxAttempts = 10) {
        let attempts = 0;
        function checkPlugin() {
            attempts++;
            console.log(`🔍 Checking for plugin (attempt ${attempts}/${maxAttempts})`);
            if (typeof cordova !== "undefined" && cordova.plugins && cordova.plugins.multibrowser) {
                console.log("✅ Plugin multibrowser found!");
                callback();
            } else if (attempts < maxAttempts) {
                console.log("⏳ Plugin not ready, retrying in 500ms...");
                setTimeout(checkPlugin, 500);
            } else {
                console.log("❌ Plugin not available after maximum attempts");
                $parameters.Success = false;
                $parameters.ErrorMessage = "Plugin multibrowser not available after timeout";
                $resolve();
            }
        }
        checkPlugin();
    }

    function onSuccess(result) {
        console.log("✅ WebView opened successfully:", result);
        $parameters.Success = true;
        $parameters.Result = result || "WebView opened successfully";
        $resolve();
    }

    function onError(error) {
        console.error("❌ Error opening WebView:", error);
        $parameters.Success = false;
        $parameters.ErrorCode = error.code || -1;
        $parameters.ErrorMessage = error.message || "Unknown error occurred";
        $resolve();
    }

    // Validar parámetros
    if (!$parameters.URL) {
        console.log("❌ URL parameter is required");
        $parameters.Success = false;
        $parameters.ErrorMessage = "URL parameter is required";
        $resolve();
        return;
    }

    console.log("🚀 Starting multibrowser WebView execution");
    console.log("📋 Parameters:", { 
        URL: $parameters.URL, 
        Target: $parameters.Target || '_blank',
        Options: $parameters.Options || 'location=yes,toolbar=yes,hardwareback=yes'
    });

    waitForPlugin(function() {
        console.log("🚀 Plugin ready, executing openInWebView");
        
        const target = $parameters.Target || '_blank';
        const options = $parameters.Options || 'location=yes,toolbar=yes,hardwareback=yes';
        
        if (target === '_blank' || target === '_self') {
            console.log("🌐 Opening in WebView:", target);
            cordova.plugins.multibrowser.openInWebView(
                $parameters.URL,
                target,
                options,
                onSuccess,
                onError
            );
        } else {
            console.log("🌐 Opening in external browser");
            cordova.plugins.multibrowser.openInExternalBrowser(
                $parameters.URL,
                '_system',
                options,
                onSuccess,
                onError
            );
        }
    });
});
```

### JavaScript Action: OpenHidden

```javascript
require(["PluginManager"], function (module) {
    // Parámetros esperados:
    // $parameters.URL: string (URL a abrir)
    // $parameters.Target: string (opcional)
    // $parameters.Options: string (opcional)
    
    function waitForPlugin(callback, maxAttempts = 10) {
        let attempts = 0;
        function checkPlugin() {
            attempts++;
            if (typeof cordova !== "undefined" && cordova.plugins && cordova.plugins.multibrowser) {
                callback();
            } else if (attempts < maxAttempts) {
                setTimeout(checkPlugin, 500);
            } else {
                $parameters.Success = false;
                $parameters.ErrorMessage = "Plugin not available";
                $resolve();
            }
        }
        checkPlugin();
    }

    function onSuccess(result) {
        $parameters.Success = true;
        $parameters.Result = result || "Hidden mode activated";
        $resolve();
    }

    function onError(error) {
        $parameters.Success = false;
        $parameters.ErrorCode = error.code || -1;
        $parameters.ErrorMessage = error.message || "Unknown error";
        $resolve();
    }

    if (!$parameters.URL) {
        $parameters.Success = false;
        $parameters.ErrorMessage = "URL parameter is required";
        $resolve();
        return;
    }

    waitForPlugin(function() {
        cordova.plugins.multibrowser.openHidden(
            $parameters.URL,
            $parameters.Target || '_blank',
            $parameters.Options || '',
            onSuccess,
            onError
        );
    });
});
```

### JavaScript Action: OpenExternalBrowser

```javascript
require(["PluginManager"], function (module) {
    // Parámetros esperados:
    // $parameters.URL: string (URL a abrir)
    // $parameters.Options: string (opcional)
    
    function waitForPlugin(callback, maxAttempts = 10) {
        let attempts = 0;
        function checkPlugin() {
            attempts++;
            if (typeof cordova !== "undefined" && cordova.plugins && cordova.plugins.multibrowser) {
                callback();
            } else if (attempts < maxAttempts) {
                setTimeout(checkPlugin, 500);
            } else {
                $parameters.Success = false;
                $parameters.ErrorMessage = "Plugin not available";
                $resolve();
            }
        }
        checkPlugin();
    }

    function onSuccess(result) {
        $parameters.Success = true;
        $parameters.Result = result || "External browser opened";
        $resolve();
    }

    function onError(error) {
        $parameters.Success = false;
        $parameters.ErrorCode = error.code || -1;
        $parameters.ErrorMessage = error.message || "Unknown error";
        $resolve();
    }

    if (!$parameters.URL) {
        $parameters.Success = false;
        $parameters.ErrorMessage = "URL parameter is required";
        $resolve();
        return;
    }

    waitForPlugin(function() {
        cordova.plugins.multibrowser.openInExternalBrowser(
            $parameters.URL,
            '_system',
            $parameters.Options || 'location=yes,toolbar=yes',
            onSuccess,
            onError
        );
    });
});
```

## Uso en Flows

### 1. Crear JavaScript Actions
Crea las tres JavaScript Actions anteriores en tu módulo OutSystems.

### 2. Configurar Parámetros de Entrada
- **URL**: string (obligatorio)
- **Target**: string (opcional, por defecto '_blank')
- **Options**: string (opcional)

### 3. Configurar Parámetros de Salida
- **Success**: boolean
- **Result**: string
- **ErrorCode**: integer
- **ErrorMessage**: string

### 4. Usar en Flows
```javascript
// Ejemplo de uso en un botón
function openWebView() {
    // Llamar a la JavaScript Action
    OpenInWebView({
        URL: "https://example.com",
        Target: "_blank",
        Options: "location=yes,toolbar=yes,hardwareback=yes"
    });
}
```

## Manejo de Errores

### Verificar Disponibilidad del Plugin
```javascript
function isPluginAvailable() {
    return typeof cordova !== "undefined" && 
           cordova.plugins && 
           cordova.plugins.multibrowser;
}
```

### Manejo de Timeouts
```javascript
function waitForPluginWithTimeout(callback, timeout = 5000) {
    const startTime = Date.now();
    
    function checkPlugin() {
        if (isPluginAvailable()) {
            callback();
        } else if (Date.now() - startTime < timeout) {
            setTimeout(checkPlugin, 100);
        } else {
            console.error("Plugin timeout");
            // Manejar timeout
        }
    }
    
    checkPlugin();
}
```

## Debugging

### Logs en Consola
```javascript
// Habilitar logs detallados
cordova.plugins.multibrowser.setLogLevel('debug');

// Verificar funciones disponibles
console.log('Available functions:', Object.keys(cordova.plugins.multibrowser));
```

### Verificar Estado del Plugin
```javascript
function checkPluginStatus() {
    console.log('Cordova available:', typeof cordova !== 'undefined');
    console.log('Plugins available:', cordova.plugins ? 'yes' : 'no');
    console.log('Multibrowser available:', cordova.plugins?.multibrowser ? 'yes' : 'no');
}
```

## Consideraciones de Seguridad

### Validación de URLs
```javascript
function isValidURL(url) {
    try {
        new URL(url);
        return true;
    } catch {
        return false;
    }
}

// Usar en JavaScript Actions
if (!isValidURL($parameters.URL)) {
    $parameters.Success = false;
    $parameters.ErrorMessage = "Invalid URL format";
    $resolve();
    return;
}
```

### Whitelist de Dominios
```javascript
const ALLOWED_DOMAINS = ['example.com', 'trusted-site.com'];

function isDomainAllowed(url) {
    try {
        const domain = new URL(url).hostname;
        return ALLOWED_DOMAINS.some(allowed => domain.endsWith(allowed));
    } catch {
        return false;
    }
}
```

## Testing

### Verificar en Simulador/Emulador
1. Ejecuta la aplicación en el simulador iOS o emulador Android
2. Verifica que los logs aparezcan en la consola
3. Prueba las diferentes funciones del plugin

### Verificar en Dispositivo Físico
1. Instala la aplicación en un dispositivo físico
2. Verifica que el plugin funcione correctamente
3. Prueba diferentes tipos de URLs y configuraciones
