#!/bin/bash

# Script to register a test user for loginWithLockedAfterMultipleFailure test
# Usage: ./register-test-user.sh [username] [email] [password]

USERNAME="${1:-testlockuser}"
EMAIL="${2:-testlockuser@test.com}"
PASSWORD="${3:-testpass123}"
BASE_URL="${BASE_URL:-https://pyavchik.space}"

echo "Registering user: $USERNAME"
echo "Email: $EMAIL"
echo "URL: $BASE_URL/api/auth/register"
echo ""

response=$(curl -s -X POST "$BASE_URL/api/auth/register" \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"$USERNAME\",\"email\":\"$EMAIL\",\"password\":\"$PASSWORD\"}" \
  -w "\nHTTP_CODE:%{http_code}")

http_code=$(echo "$response" | grep -o "HTTP_CODE:[0-9]*" | cut -d: -f2)
body=$(echo "$response" | sed '/HTTP_CODE:/d')

if [ "$http_code" = "201" ]; then
    echo "✅ User registered successfully!"
    echo "$body" | python3 -m json.tool 2>/dev/null || echo "$body"
else
    echo "❌ Registration failed (HTTP $http_code)"
    echo "$body"
    exit 1
fi

