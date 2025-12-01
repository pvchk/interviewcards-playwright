#!/bin/bash

# Script to check if Playwright browsers are installed

echo "Checking Playwright browser installation..."
echo ""

# Check if Playwright browsers directory exists
BROWSER_DIR="$HOME/.cache/ms-playwright"
CHROMIUM_DIR="$BROWSER_DIR/chromium-*"

if [ -d "$BROWSER_DIR" ] && [ -d $CHROMIUM_DIR ] 2>/dev/null; then
    echo "✅ Playwright browsers are installed!"
    echo ""
    echo "Found browsers in: $BROWSER_DIR"
    ls -la "$BROWSER_DIR" 2>/dev/null | grep -E "^d" | awk '{print "  - " $9}'
else
    echo "❌ Playwright browsers are NOT installed!"
    echo ""
    echo "Install browsers using one of these methods:"
    echo ""
    echo "1. Using Maven:"
    echo "   mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args=\"install chromium\""
    echo ""
    echo "2. Using the installation script:"
    echo "   ./install-browsers.sh"
    echo ""
    echo "3. Using Node.js (if installed):"
    echo "   npx playwright install chromium"
    echo ""
    exit 1
fi

