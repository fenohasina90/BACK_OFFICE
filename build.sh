#!/usr/bin/env bash
set -euo pipefail

# Chemins du projet
ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
SRC_DIR="$ROOT_DIR/src/main/java"
WEBAPP_DIR="$ROOT_DIR/src/main/webapp"
WEB_INF_DIR="$ROOT_DIR/WEB-INF"
LIB_DIR="$ROOT_DIR/lib"
TARGET_DIR="$ROOT_DIR/target"
CLASSES_DIR="$TARGET_DIR/classes"
WAR_FILE="$TARGET_DIR/BackOffice.war"

# Destination Tomcat
TOMCAT_HOME="/opt/tomcat"
TOMCAT_WEBAPPS="$TOMCAT_HOME/webapps"
APP_NAME="BackOffice"

echo "========================================="
echo "  Build et déploiement BackOffice"
echo "========================================="

# Vérification des outils
command -v javac >/dev/null 2>&1 || { echo "Erreur: javac non trouvé. Installez JDK." >&2; exit 1; }
command -v jar >/dev/null 2>&1 || { echo "Erreur: jar non trouvé. Installez JDK." >&2; exit 1; }

# Préparation des répertoires
echo "Nettoyage et préparation des répertoires..."
rm -rf "$TARGET_DIR"
mkdir -p "$CLASSES_DIR"

# Construction du classpath
echo "Construction du classpath..."
CP=""
for jar in "$LIB_DIR"/*.jar; do
    if [[ -f "$jar" ]]; then
        if [[ -z "$CP" ]]; then
            CP="$jar"
        else
            CP="$CP:$jar"
        fi
    fi
done

if [[ -z "$CP" ]]; then
    echo "Erreur: Aucun JAR trouvé dans $LIB_DIR" >&2
    exit 1
fi

echo "Classpath: $CP"

# Compilation des sources Java
echo "Compilation des sources Java..."
if ! javac -d "$CLASSES_DIR" -cp "$CP" $(find "$SRC_DIR" -name "*.java"); then
    echo "Erreur lors de la compilation" >&2
    exit 1
fi

echo "Compilation réussie!"

# Création de la structure WAR
echo "Création de la structure WAR..."
WAR_ROOT="$TARGET_DIR/war-root"
mkdir -p "$WAR_ROOT/WEB-INF/classes"
mkdir -p "$WAR_ROOT/WEB-INF/lib"

# Copie des classes compilées
cp -r "$CLASSES_DIR"/* "$WAR_ROOT/WEB-INF/classes/"

# Copie des librairies
cp "$LIB_DIR"/*.jar "$WAR_ROOT/WEB-INF/lib/"

# Copie du web.xml
if [[ -f "$WEB_INF_DIR/web.xml" ]]; then
    cp "$WEB_INF_DIR/web.xml" "$WAR_ROOT/WEB-INF/"
else
    echo "Attention: web.xml non trouvé dans $WEB_INF_DIR"
fi

# Copie des ressources webapp (JSP, HTML, CSS, etc.)
if [[ -d "$WEBAPP_DIR" ]]; then
    cp -r "$WEBAPP_DIR"/* "$WAR_ROOT/"
fi

# Création du WAR
echo "Création du fichier WAR..."
cd "$WAR_ROOT"
jar -cvf "$WAR_FILE" .
cd "$ROOT_DIR"

echo "WAR créé: $WAR_FILE"

# Déploiement sur Tomcat
if [[ -d "$TOMCAT_WEBAPPS" ]]; then
    echo "Déploiement sur Tomcat..."
    
    # Suppression de l'ancien déploiement
    if [[ -d "$TOMCAT_WEBAPPS/$APP_NAME" ]]; then
        echo "Suppression de l'ancien déploiement..."
        rm -rf "$TOMCAT_WEBAPPS/$APP_NAME"
    fi
    
    if [[ -f "$TOMCAT_WEBAPPS/$APP_NAME.war" ]]; then
        rm -f "$TOMCAT_WEBAPPS/$APP_NAME.war"
    fi
    
    # Copie du nouveau WAR
    cp "$WAR_FILE" "$TOMCAT_WEBAPPS/$APP_NAME.war"
    
    echo "========================================="
    echo "  Déploiement réussi!"
    echo "========================================="
    echo "Application: http://localhost:8080/$APP_NAME/"
    echo "Réservation: http://localhost:8080/$APP_NAME/reservation/"
else
    echo "========================================="
    echo "  Build réussi!"
    echo "========================================="
    echo "WAR disponible: $WAR_FILE"
    echo "Tomcat non trouvé à $TOMCAT_WEBAPPS"
    echo "Déployez manuellement le WAR sur votre serveur."
fi
