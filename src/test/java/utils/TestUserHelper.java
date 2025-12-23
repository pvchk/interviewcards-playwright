package utils;

import config.Config;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/**
 * Utility class for test user generation and registration.
 * Provides methods to create test users via API for automated testing.
 */
public class TestUserHelper {

    /**
     * Registers a new user via the API using curl.
     * Generates a unique username with timestamp to avoid conflicts.
     * Format: testlockuser{timestamp}
     *
     * @return The registered username, or null if registration failed
     */
    public static String registerTestUser() {
        long timestamp = System.currentTimeMillis();
        String username = "testlockuser" + timestamp;
        String email = "testlockuser" + timestamp + "@test.com";
        String password = "testpass123";

        return registerTestUser(username, email, password);
    }

    /**
     * Registers a new user via the API using curl with specified credentials.
     *
     * @param username the username to register
     * @param email the email to register
     * @param password the password to register
     * @return The registered username, or null if registration failed
     */
    public static String registerTestUser(String username, String email, String password) {
        try {
            // Find the script in the project root
            File scriptFile = new File("register-test-user.sh");
            if (!scriptFile.exists()) {
                // Try absolute path from workspace
                String workspacePath = System.getProperty("user.dir");
                scriptFile = new File(workspacePath, "register-test-user.sh");
            }

            if (!scriptFile.exists() || !scriptFile.canExecute()) {
                // Fallback: use curl directly
                return registerUserViaCurl(username, email, password);
            }

            ProcessBuilder pb = new ProcessBuilder(
                scriptFile.getAbsolutePath(),
                username,
                email,
                password
            );

            // Set working directory to script location
            pb.directory(scriptFile.getParentFile());

            Process process = pb.start();

            // Read output
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
            );
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("✅ User '" + username + "' registered successfully");
                return username;
            } else {
                System.err.println("⚠️ Failed to register user '" + username + "' (exit code: " + exitCode + ")");
                System.err.println("Output: " + output.toString());
                return null;
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error registering user '" + username + "': " + e.getMessage());
            // Try fallback method
            return registerUserViaCurl(username, email, password);
        }
    }

    /**
     * Registers a user directly via curl command (fallback method).
     *
     * @param username the username to register
     * @param email the email to register
     * @param password the password to register
     * @return The registered username, or null if registration failed
     */
    private static String registerUserViaCurl(String username, String email, String password) {
        try {
            String baseUrl = Config.BASE_URL;
            String jsonPayload = String.format(
                "{\"username\":\"%s\",\"email\":\"%s\",\"password\":\"%s\"}",
                username, email, password
            );

            ProcessBuilder pb = new ProcessBuilder(
                "curl", "-s", "-X", "POST",
                baseUrl + "/api/auth/register",
                "-H", "Content-Type: application/json",
                "-d", jsonPayload,
                "-w", "\nHTTP_CODE:%{http_code}"
            );

            Process process = pb.start();

            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
            );
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            String responseStr = response.toString();

            // Check HTTP status code
            if (responseStr.contains("HTTP_CODE:201")) {
                System.out.println("✅ User '" + username + "' registered successfully via API");
                return username;
            } else if (responseStr.contains("HTTP_CODE:400") && responseStr.contains("already taken")) {
                System.err.println("⚠️ Username '" + username + "' is already taken");
                return null;
            } else {
                System.err.println("⚠️ Failed to register user '" + username + "' (exit code: " + exitCode + ")");
                System.err.println("Response: " + responseStr);
                return null;
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error registering user via curl '" + username + "': " + e.getMessage());
            return null;
        }
    }

    /**
     * Deletes a test user via the API using curl.
     *
     * @param username the username of the user to delete
     */
    public static void deleteUser(String username) {
        if (username == null || username.isEmpty()) {
            System.err.println("⚠️ Cannot delete user: username is null or empty");
            return;
        }

        try {
            // Try to find a delete script first (if it exists)
            File scriptFile = new File("delete-test-user.sh");
            if (!scriptFile.exists()) {
                String workspacePath = System.getProperty("user.dir");
                scriptFile = new File(workspacePath, "delete-test-user.sh");
            }

            if (scriptFile.exists() && scriptFile.canExecute()) {
                ProcessBuilder pb = new ProcessBuilder(
                    scriptFile.getAbsolutePath(),
                    username
                );

                pb.directory(scriptFile.getParentFile());

                Process process = pb.start();

                // Read output
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
                );
                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }

                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    System.out.println("✅ User '" + username + "' deleted successfully");
                    return;
                } else {
                    System.err.println("⚠️ Failed to delete user '" + username + "' via script (exit code: " + exitCode + ")");
                    System.err.println("Output: " + output.toString());
                    // Fallback to curl
                }
            }

            // Fallback: use curl directly
            deleteUserViaCurl(username);
        } catch (Exception e) {
            System.err.println("⚠️ Error deleting user '" + username + "': " + e.getMessage());
            // Try fallback method
            deleteUserViaCurl(username);
        }
    }

    /**
     * Deletes a user directly via curl command (fallback method).
     *
     * @param username the username of the user to delete
     */
    private static void deleteUserViaCurl(String username) {
        try {
            String baseUrl = Config.BASE_URL;
            String jsonPayload = String.format("{\"username\":\"%s\"}", username);

            ProcessBuilder pb = new ProcessBuilder(
                "curl", "-s", "-X", "DELETE",
                baseUrl + "/api/auth/delete",
                "-H", "Content-Type: application/json",
                "-d", jsonPayload,
                "-w", "\nHTTP_CODE:%{http_code}"
            );

            Process process = pb.start();

            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
            );
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            String responseStr = response.toString();

            // Check HTTP status code - 200 or 204 typically indicate success
            if (responseStr.contains("HTTP_CODE:200") || responseStr.contains("HTTP_CODE:204")) {
                System.out.println("✅ User '" + username + "' deleted successfully via API");
            } else if (responseStr.contains("HTTP_CODE:404")) {
                System.out.println("ℹ️ User '" + username + "' not found (may have already been deleted)");
            } else {
                System.err.println("⚠️ Failed to delete user '" + username + "' (exit code: " + exitCode + ")");
                System.err.println("Response: " + responseStr);
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error deleting user via curl '" + username + "': " + e.getMessage());
        }
    }
}

