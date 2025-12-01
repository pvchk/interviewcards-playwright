#!/bin/bash

# Playwright Browser Installation Script
# This script helps install Playwright browsers with error handling

echo "Installing Playwright browsers..."
echo ""

# Clean install dependencies first
echo "Step 1: Cleaning and installing Maven dependencies..."
mvn clean install -DskipTests

if [ $? -ne 0 ]; then
    echo "Error: Failed to install Maven dependencies"
    exit 1
fi

echo ""
echo "Step 2: Installing Playwright browsers..."
echo ""

# Try installing browsers
mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install"

if [ $? -ne 0 ]; then
    echo ""
    echo "Error: Browser installation failed. Trying alternative method..."
    echo ""
    echo "Attempting to install only Chromium..."
    mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install chromium"
    
    if [ $? -ne 0 ]; then
        echo ""
        echo "Installation failed. Please try:"
        echo "1. Check your internet connection"
        echo "2. Clear Maven cache: rm -rf ~/.m2/repository/com/microsoft/playwright"
        echo "3. Try again later if CDN is unavailable"
        exit 1
    fi
fi

echo ""
echo "✅ Playwright browsers installed successfully!"

