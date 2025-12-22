package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/**
 * Utility class for database operations in tests.
 * Provides methods for test data cleanup and maintenance.
 */
public class DatabaseHelper {

    private static final String DB_USER = System.getenv().getOrDefault("DB_USER", "root");
    private static final String DB_NAME = System.getenv().getOrDefault("DB_NAME", "interviewcards");
    private static final String DB_PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "");

    /**
     * Unlocks a user by resetting their failed login attempts and lock status.
     * This method is used for test cleanup after locking tests.
     *
     * @param username the username to unlock
     */
    public static void unlockUser(String username) {
        try {
            String mysqlPath = findMysqlPath();
            if (mysqlPath == null) {
                System.err.println("⚠️ MySQL client not found in PATH. Skipping unlock for user '" + username + "'");
                return;
            }

            ProcessBuilder pb = new ProcessBuilder(
                mysqlPath, "-u", DB_USER, DB_NAME, "-e",
                String.format(
                    "UPDATE users SET account_locked = FALSE, " +
                    "failed_login_attempts = 0, " +
                    "lock_expiration_time = NULL " +
                    "WHERE username = '%s';",
                    username
                )
            );

            // Add mysql directory to PATH for any subprocesses
            String path = System.getenv("PATH");
            String mysqlDir = new File(mysqlPath).getParent();
            if (mysqlDir != null && path != null && !path.contains(mysqlDir)) {
                pb.environment().put("PATH", mysqlDir + ":" + path);
            }

            // Add password if provided
            if (!DB_PASSWORD.isEmpty()) {
                pb.environment().put("MYSQL_PWD", DB_PASSWORD);
            }

            Process process = pb.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("✅ User '" + username + "' unlocked successfully");
            } else {
                System.err.println("⚠️ Failed to unlock user '" + username + "' (exit code: " + exitCode + ")");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error unlocking user '" + username + "': " + e.getMessage());
        }
    }

    /**
     * Finds mysql executable in system PATH.
     * Checks common installation locations and searches PATH environment variable.
     *
     * @return Full path to mysql executable, or null if not found
     */
    private static String findMysqlPath() {
        // Common mysql locations
        String[] commonPaths = {
            "/usr/bin/mysql",
            "/usr/local/bin/mysql",
            "/opt/homebrew/bin/mysql",  // macOS Homebrew
            "/snap/bin/mysql"            // Snap packages
        };

        // Check common locations first
        for (String path : commonPaths) {
            File mysqlFile = new File(path);
            if (mysqlFile.exists() && mysqlFile.canExecute()) {
                return path;
            }
        }

        // Search in PATH
        String pathEnv = System.getenv("PATH");
        if (pathEnv != null) {
            String pathSeparator = System.getProperty("path.separator", ":");
            String[] paths = pathEnv.split(pathSeparator);
            for (String dir : paths) {
                File mysqlFile = new File(dir, "mysql");
                if (mysqlFile.exists() && mysqlFile.canExecute()) {
                    return mysqlFile.getAbsolutePath();
                }
            }
        }

        // Try 'which mysql' command (Unix-like systems)
        try {
            Process process = new ProcessBuilder("which", "mysql").start();
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
            );
            String line = reader.readLine();
            process.waitFor();
            if (line != null && !line.trim().isEmpty()) {
                File mysqlFile = new File(line.trim());
                if (mysqlFile.exists() && mysqlFile.canExecute()) {
                    return mysqlFile.getAbsolutePath();
                }
            }
        } catch (Exception e) {
            // which command not available or failed, continue
        }

        return null;
    }
}

