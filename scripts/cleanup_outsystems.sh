#!/bin/bash

# 🧹 Script de Limpieza para OutSystems - Eliminar Plugins Conflictivos
# Uso: ./scripts/cleanup_outsystems.sh [ruta_proyecto_outsystems]

set -e

echo "🧹 Iniciando limpieza de plugins conflictivos..."

# Verificar si se proporcionó ruta del proyecto
if [ -z "$1" ]; then
    echo "❌ Error: Debes especificar la ruta del proyecto OutSystems"
    echo "Uso: ./scripts/cleanup_outsystems.sh [ruta_proyecto_outsystems]"
    echo "Ejemplo: ./scripts/cleanup_outsystems.sh /path/to/outsystems/project"
    exit 1
fi

PROJECT_PATH="$1"

# Verificar que el proyecto existe
if [ ! -d "$PROJECT_PATH" ]; then
    echo "❌ Error: El directorio del proyecto no existe: $PROJECT_PATH"
    exit 1
fi

cd "$PROJECT_PATH"

echo "📁 Proyecto: $(pwd)"

# Verificar que es un proyecto Cordova
if [ ! -f "config.xml" ]; then
    echo "❌ Error: No se encontró config.xml. ¿Es un proyecto Cordova?"
    exit 1
fi

echo "🔍 Verificando plugins instalados..."

# Listar plugins actuales
echo "📋 Plugins instalados actualmente:"
cordova plugin list || echo "⚠️ No se pudo listar plugins"

echo ""
echo "🗑️ Eliminando plugins conflictivos..."

# Eliminar plugin original de InAppBrowser
echo "🗑️ Eliminando cordova-plugin-inappbrowser..."
cordova plugin remove cordova-plugin-inappbrowser 2>/dev/null || echo "⚠️ Plugin no encontrado o ya eliminado"

# Eliminar plugin con ID conflictivo
echo "🗑️ Eliminando com.outsystems.plugins.inappbrowser..."
cordova plugin remove com.outsystems.plugins.inappbrowser 2>/dev/null || echo "⚠️ Plugin no encontrado o ya eliminado"

# Eliminar cualquier plugin con "inappbrowser" en el nombre
echo "🗑️ Eliminando plugins con 'inappbrowser' en el nombre..."
cordova plugin list | grep -i inappbrowser | while read plugin; do
    plugin_name=$(echo "$plugin" | awk '{print $1}')
    echo "🗑️ Eliminando: $plugin_name"
    cordova plugin remove "$plugin_name" 2>/dev/null || echo "⚠️ No se pudo eliminar $plugin_name"
done

echo ""
echo "🧹 Limpiando archivos residuales..."

# Eliminar directorios de plugins conflictivos
PLUGIN_DIRS=(
    "platforms/ios/*/Plugins/cordova-plugin-inappbrowser"
    "platforms/ios/*/Plugins/com.outsystems.plugins.inappbrowser"
    "platforms/android/app/src/main/java/org/apache/cordova/inappbrowser"
    "platforms/android/app/src/main/kotlin/com/outsystems/plugins/inappbrowser"
)

for pattern in "${PLUGIN_DIRS[@]}"; do
    for dir in $pattern; do
        if [ -d "$dir" ]; then
            echo "🗑️ Eliminando directorio: $dir"
            rm -rf "$dir"
        fi
    done
done

echo ""
echo "🔍 Verificando plugins restantes..."

# Listar plugins después de la limpieza
echo "📋 Plugins restantes:"
cordova plugin list || echo "⚠️ No se pudo listar plugins"

echo ""
echo "✅ Limpieza completada!"
echo ""
echo "🚀 Próximos pasos:"
echo "1. Instalar nuestro plugin: cordova plugin add [ruta_a_nuestro_plugin]"
echo "2. Verificar instalación: cordova plugin list | grep multibrowser"
echo "3. Recompilar: cordova build"
echo ""
echo "💡 Si sigues teniendo problemas, verifica:"
echo "   - config.xml no tenga referencias a plugins conflictivos"
echo "   - package.json no tenga dependencias del plugin original"
echo "   - platforms/ esté limpio de archivos residuales"
