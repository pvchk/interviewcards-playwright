#!/bin/bash
# Script to unlock a test user after Playwright tests

USERNAME="${1:-testlockuser}"

echo "🔓 Unlocking user: $USERNAME"

mysql -u root interviewcards << EOF
UPDATE users 
SET account_locked = FALSE,
    failed_login_attempts = 0,
    lock_expiration_time = NULL
WHERE username = '$USERNAME';

SELECT 
    username,
    account_locked,
    failed_login_attempts,
    lock_expiration_time
FROM users
WHERE username = '$USERNAME';
EOF

echo "✅ User unlocked"
