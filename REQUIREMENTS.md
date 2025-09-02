# 🔧 Cordova Build Requirements

## 📋 Resumen Rápido

Para compilar aplicaciones Cordova necesitas instalar:

### **🚀 Herramientas Básicas:**
- **Node.js** (versión LTS recomendada)
- **npm** (viene con Node.js)
- **Cordova CLI** (`npm install -g cordova`)

### **🍎 Para iOS (solo macOS):**
- **Xcode** (desde Mac App Store)
- **Xcode Command Line Tools** (`xcode-select --install`)

### **🤖 Para Android:**
- **Java JDK 11** o superior
- **Android Studio** (incluye Android SDK)
- **Android SDK** (API level 22+)

## ⚡ Instalación Automática

### **Script Completo:**
```bash
./scripts/install_requirements.sh
```

### **Instalación Manual:**
```bash
# 1. Instalar Node.js
brew install node  # macOS
# o descargar desde nodejs.org

# 2. Instalar Cordova
npm install -g cordova

# 3. Instalar Xcode (macOS)
# Descargar desde Mac App Store

# 4. Instalar Android Studio
# Descargar desde developer.android.com
```

## 🔍 Verificar Instalación

```bash
# Verificar versiones
node --version
npm --version
cordova --version

# Verificar plataformas disponibles
cordova platform list
```

## 📱 Crear Proyecto de Prueba

```bash
# Crear proyecto
cordova create test_project com.test.app "Test App"

# Agregar plataformas
cd test_project
cordova platform add ios
cordova platform add android

# Instalar nuestro plugin
cordova plugin add ../

# Compilar
cordova build ios
cordova build android
```

## ⚠️ Problemas Comunes

### **Error: "command not found: cordova"**
```bash
npm install -g cordova
# Reiniciar terminal
```

### **Error: "No platforms added to this project"**
```bash
cordova platform add ios
cordova platform add android
```

### **Error: "Xcode not found" (macOS)**
- Instalar Xcode desde Mac App Store
- Ejecutar `xcode-select --install`

### **Error: "Android SDK not found"**
- Instalar Android Studio
- Completar setup wizard
- Configurar variables de entorno

## 🎯 Próximos Pasos

1. **Ejecutar script de instalación:**
   ```bash
   ./scripts/install_requirements.sh
   ```

2. **Compilar plugin:**
   ```bash
   ./scripts/quick_build.sh
   ```

3. **Probar funcionalidad:**
   - Abrir proyecto en Xcode (iOS)
   - Abrir proyecto en Android Studio
   - Ejecutar en simulador/dispositivo

## 📚 Recursos Adicionales

- [Cordova Documentation](https://cordova.apache.org/docs/en/latest/)
- [iOS Development Guide](https://developer.apple.com/develop/)
- [Android Development Guide](https://developer.android.com/)
- [Node.js Documentation](https://nodejs.org/docs/)
