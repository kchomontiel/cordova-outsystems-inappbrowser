# 🚨 Solución de Problemas - Plugin MultiBrowser en OutSystems

## ❌ Error Común: Plugin Original Conflictivo

### **Síntoma:**

```
SwiftCompile normal arm64 Compiling OSInAppBrowser.swift
/Plugins/com.outsystems.plugins.inappbrowser/OSInAppBrowser.swift
```

### **Causa:**

El plugin original `cordova-plugin-inappbrowser` está instalado y compitiendo con nuestro plugin.

## 🔧 Solución Paso a Paso

### **Paso 1: Limpiar Plugins Conflictivos**

```bash
# En tu proyecto OutSystems
cd [ruta_proyecto_outsystems]

# Eliminar plugin original
cordova plugin remove cordova-plugin-inappbrowser

# Eliminar plugin conflictivo
cordova plugin remove com.outsystems.plugins.inappbrowser

# Verificar que no queden plugins con "inappbrowser"
cordova plugin list | grep -i inappbrowser
```

### **Paso 2: Limpiar Archivos Residuales**

```bash
# Eliminar directorios residuales
rm -rf platforms/ios/*/Plugins/cordova-plugin-inappbrowser
rm -rf platforms/ios/*/Plugins/com.outsystems.plugins.inappbrowser
rm -rf platforms/android/app/src/main/java/org/apache/cordova/inappbrowser
rm -rf platforms/android/app/src/main/kotlin/com/outsystems/plugins/inappbrowser
```

### **Paso 3: Instalar Nuestro Plugin**

```bash
# Instalar desde el repositorio
cordova plugin add https://github.com/[tu-usuario]/cordova-outsystems-inappbrowser.git

# O instalar localmente
cordova plugin add ../cordova-outsystems-inappbrowser
```

### **Paso 4: Verificar Instalación**

```bash
# Verificar que solo esté nuestro plugin
cordova plugin list | grep multibrowser

# Debería mostrar algo como:
# multibrowser 1.5.0 "multibrowserplugin"
```

### **Paso 5: Recompilar**

```bash
# Limpiar y recompilar
cordova clean
cordova build
```

## 🧹 Script Automatizado de Limpieza

### **Usar nuestro script de limpieza:**

```bash
# Desde el directorio del plugin
chmod +x scripts/cleanup_outsystems.sh
./scripts/cleanup_outsystems.sh [ruta_proyecto_outsystems]
```

## 📋 Verificación de Configuración

### **En config.xml (NO debe tener):**

```xml
<!-- ❌ ELIMINAR ESTO -->
<plugin name="cordova-plugin-inappbrowser" spec="~5.0.0" />
<plugin name="com.outsystems.plugins.inappbrowser" spec="file:../..." />
```

### **En config.xml (SÍ debe tener):**

```xml
<!-- ✅ SOLO ESTO -->
<plugin name="multibrowser" spec="file:../cordova-outsystems-inappbrowser" />
```

## 🔍 Diagnóstico Avanzado

### **Verificar Dependencias:**

```bash
# Buscar archivos del plugin original
find . -name "*OSInAppBrowser*" -o -name "*InAppBrowser*"

# Verificar package.json del proyecto
cat package.json | grep -i inappbrowser

# Verificar config.xml
grep -i inappbrowser config.xml
```

### **Verificar Estructura de Plugins:**

```bash
# Ver estructura de plugins
ls -la plugins/
ls -la platforms/ios/*/Plugins/
ls -la platforms/android/app/src/main/kotlin/
```

## 🚨 Casos Especiales

### **Si usas CocoaPods:**

```bash
# Limpiar CocoaPods
cd platforms/ios
pod deintegrate
pod install
```

### **Si usas Gradle:**

```bash
# Limpiar Gradle
cd platforms/android
./gradlew clean
```

## ✅ Verificación Final

### **Después de la limpieza, deberías ver:**

1. **Solo un plugin**: `multibrowser`
2. **Sin archivos conflictivos**: No `OSInAppBrowser.swift`
3. **Compilación exitosa**: Sin errores de Swift
4. **Funcionalidad completa**: `openInWebView` funcionando

## 📞 Si el Problema Persiste

### **Verificar:**

1. **Versión de Cordova**: `cordova --version`
2. **Versión de iOS**: `cordova platform list`
3. **Versión de Android**: `cordova platform list`
4. **Logs completos**: `cordova build --verbose`

### **Contactar:**

- Revisar logs completos del build
- Verificar que no haya dependencias ocultas
- Confirmar que el plugin se instaló correctamente
