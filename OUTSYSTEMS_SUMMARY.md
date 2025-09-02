# 📱 Resumen de Instalación - Plugin MultiBrowser para OutSystems

## 🎯 Instalación Rápida

### 1. **Instalar desde el Repositorio**

```bash
cordova plugin add https://github.com/[tu-usuario]/cordova-outsystems-inappbrowser.git
```

### 2. **Verificar Instalación**

```bash
cordova plugin list | grep multibrowser
```

### 3. **Recompilar la App**

```bash
cordova build
```

## 🔧 Configuración Mínima

### En `config.xml`:

```xml
<feature name="multibrowser">
    <param name="ios-package" value="HiddenInAppBrowser" />
    <param name="android-package" value="com.outsystems.plugins.inappbrowser.osinappbrowser.HiddenInAppBrowser" />
</feature>
```

### En tu HTML:

```html
<script src="cordova.js"></script>
<script src="plugins/multibrowser/plugin.js"></script>
```

## 🚀 Uso Básico

```javascript
// Abrir WebView visible
window.HiddenInAppBrowser.openInWebView("https://www.google.com", {
  success: function () {
    console.log("WebView abierto");
  },
  error: function (error) {
    console.error("Error:", error);
  },
});

// Abrir WebView oculto
window.HiddenInAppBrowser.open("https://www.google.com");

// Abrir en navegador externo
window.HiddenInAppBrowser.openInExternalBrowser("https://www.google.com");
```

## 📋 Archivos Clave del Plugin

- **`plugin.xml`** - Configuración del plugin
- **`src/ios/`** - Código nativo iOS (Swift)
- **`src/android/`** - Código nativo Android (Kotlin)
- **`dist/`** - JavaScript compilado del plugin

## 🔗 Enlaces Importantes

- **Instalación Detallada**: [INSTALL_OUTSYSTEMS.md](INSTALL_OUTSYSTEMS.md)
- **README Principal**: [README.md](README.md)
- **Requisitos**: [REQUIREMENTS.md](REQUIREMENTS.md)

## ✅ Verificación

Después de la instalación, verifica que:

1. ✅ Plugin aparece en `cordova plugin list`
2. ✅ Archivos están en las carpetas de plataforma
3. ✅ App compila sin errores
4. ✅ WebView se abre correctamente

## 🆘 Soporte

- Crear issue en GitHub para bugs
- Revisar logs de consola para errores
- Verificar que Cordova esté en `deviceready`
