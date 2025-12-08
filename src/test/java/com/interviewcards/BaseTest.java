package com.interviewcards;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Base test class for Playwright tests.
 * Handles browser setup and teardown with better error handling.
 */
public abstract class BaseTest {

    // Shared Playwright instance
    protected static Playwright playwright;
    protected static Browser browser;

    // Test instance variables
    protected BrowserContext context;
    protected Page page;

    // NPM server process
    static Process npmProcess;
    static String APP_DIR;
    static String APP_URL;
    static String NPM_COMMAND;
    static int SERVER_STARTUP_TIMEOUT;

    /**
     * Load configuration from test.properties file
     */
    static {
        loadConfiguration();
    }

    private static void loadConfiguration() {
        Properties props = new Properties();
        try (InputStream input = BaseTest.class.getClassLoader().getResourceAsStream("test.properties")) {
            if (input != null) {
                props.load(input);
            } else {
                System.out.println("Warning: test.properties not found, using defaults");
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not load test.properties: " + e.getMessage());
        }

        // Get app directory - check system property first, then properties file, then default
        String appDirProperty = System.getProperty("app.dir");
        if (appDirProperty == null || appDirProperty.isEmpty()) {
            appDirProperty = props.getProperty("app.dir", "../interviewcards");
        }

        // Resolve relative paths relative to project root
        Path appDirPath = Paths.get(appDirProperty);
        if (!appDirPath.isAbsolute()) {
            // Get project root (parent of target directory or current working directory)
            String projectRoot = System.getProperty("user.dir");
            appDirPath = Paths.get(projectRoot).resolve(appDirPath).normalize();
        }
        APP_DIR = appDirPath.toString();

        // Get app URL - check system property first, then properties file, then default
        APP_URL = System.getProperty("app.url",
            props.getProperty("app.url", "http://localhost:3000"));

        // Get npm command - check system property first, then properties file, then default
        NPM_COMMAND = System.getProperty("npm.command",
            props.getProperty("npm.command", "npm install && npm run init-db && npm run migrate-auth && npm run build && npm start"));

        // Get server startup timeout
        String timeoutStr = System.getProperty("server.startup.timeout",
            props.getProperty("server.startup.timeout", "60"));
        SERVER_STARTUP_TIMEOUT = Integer.parseInt(timeoutStr);

        System.out.println("Configuration loaded:");
        System.out.println("  App directory: " + APP_DIR);
        System.out.println("  App URL: " + APP_URL);
        System.out.println("  NPM command: " + NPM_COMMAND);
        System.out.println("  Server timeout: " + SERVER_STARTUP_TIMEOUT + "s");
    }

    @BeforeAll
    static void globalSetup() {
        // Setup Angular app: install dependencies, init DB, migrate auth, build, then start server
        runNpmInstall();
        runInitDb();
        runMigrateAuth();
        runBuild();
        startNpmApp();
        // Then launch browser
        launchBrowser();
    }

    /**
     * Run npm install if node_modules doesn't exist
     */
    private static void runNpmInstall() {
        File appDir = new File(APP_DIR);
        File nodeModules = new File(appDir, "node_modules");

        if (nodeModules.exists() && nodeModules.isDirectory()) {
            System.out.println("✓ node_modules already exists, skipping npm install");
            return;
        }

        System.out.println("Installing npm dependencies...");
        runNpmCommand(new String[]{"npm", "install"}, "npm install");
    }

    /**
     * Run npm run init-db to initialize the database
     */
    private static void runInitDb() {
        System.out.println("Initializing database...");
        runNpmCommand(new String[]{"npm", "run", "init-db"}, "npm run init-db");
    }

    /**
     * Run npm run migrate-auth to migrate authentication
     */
    private static void runMigrateAuth() {
        System.out.println("Running auth migration...");
        runNpmCommand(new String[]{"npm", "run", "migrate-auth"}, "npm run migrate-auth");
    }

    /**
     * Run npm run build to build the application
     */
    private static void runBuild() {
        System.out.println("Building application...");
        runNpmCommand(new String[]{"npm", "run", "build"}, "npm run build");
    }

    /**
     * Run an npm command and wait for it to complete
     * @param command The command to run (e.g., ["npm", "install"])
     * @param commandName Human-readable name for logging
     */
    private static void runNpmCommand(String[] command, String commandName) {
        try {
            File appDir = new File(APP_DIR);
            if (!appDir.exists() || !appDir.isDirectory()) {
                throw new RuntimeException("App directory does not exist: " + APP_DIR);
            }

            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.directory(appDir);
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            // Read output in a separate thread
            StringBuilder output = new StringBuilder();
            Thread outputReader = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n");
                        System.out.println("[npm] " + line);
                    }
                } catch (IOException e) {
                    // Ignore if process ends
                }
            });
            outputReader.setDaemon(true);
            outputReader.start();

            // Wait for process to complete
            int exitCode = process.waitFor();
            outputReader.join(1000); // Wait a bit for output to finish

            if (exitCode != 0) {
                throw new RuntimeException(
                    commandName + " failed with exit code " + exitCode +
                    (output.length() > 0 ? "\nOutput:\n" + output.toString() : "")
                );
            }

            System.out.println("✓ " + commandName + " completed successfully");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(commandName + " was interrupted", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to run " + commandName + ": " + e.getMessage(), e);
        }
    }

    private static void startNpmApp() {
        try {
            System.out.println("Starting Angular app at " + APP_DIR);
            
            // Extract the actual start command from NPM_COMMAND
            // If NPM_COMMAND contains "&&", extract the last command after the last "&&"
            // Otherwise, use the command as-is
            String startCommand = NPM_COMMAND;
            if (NPM_COMMAND.contains("&&")) {
                String[] parts = NPM_COMMAND.split("&&");
                startCommand = parts[parts.length - 1].trim();
            }
            
            System.out.println("Using start command: " + startCommand);

            // Parse npm command (e.g., "npm start" -> ["npm", "start"] or "npm run dev" -> ["npm", "run", "dev"])
            String[] commandParts = startCommand.split("\\s+");

            ProcessBuilder processBuilder = new ProcessBuilder(commandParts);
            processBuilder.directory(new File(APP_DIR));
            processBuilder.redirectErrorStream(true);

            // Set environment variables for better Angular dev server compatibility
            processBuilder.environment().put("NODE_ENV", "development");

            npmProcess = processBuilder.start();

            // Monitor process output to detect early failures
            Thread outputMonitor = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(npmProcess.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("[npm] " + line);
                        // Check for common error patterns
                        if (line.toLowerCase().contains("error") && 
                            (line.toLowerCase().contains("eaddrinuse") || 
                             line.toLowerCase().contains("port") && line.toLowerCase().contains("already"))) {
                            System.err.println("⚠️  Warning: Port may already be in use");
                        }
                    }
                } catch (IOException e) {
                    // Process ended or stream closed
                }
            });
            outputMonitor.setDaemon(true);
            outputMonitor.start();

            // Check if process is still alive after a short delay
            Thread.sleep(2000);
            if (!npmProcess.isAlive()) {
                int exitCode = npmProcess.exitValue();
                throw new RuntimeException(
                    "NPM process exited immediately with code " + exitCode + 
                    ". Check the output above for errors."
                );
            }

            // Wait for server to be ready
            System.out.println("Waiting for Angular dev server to start...");
            waitForServer(APP_URL, SERVER_STARTUP_TIMEOUT);
            System.out.println("✓ Angular app started successfully at " + APP_URL);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            if (npmProcess != null && npmProcess.isAlive()) {
                npmProcess.destroyForcibly();
            }
            throw new RuntimeException("Interrupted while starting Angular app", e);
        } catch (Exception e) {
            System.err.println("❌ Failed to start Angular app: " + e.getMessage());
            if (npmProcess != null && npmProcess.isAlive()) {
                npmProcess.destroyForcibly();
            }
            throw new RuntimeException("Could not start Angular app. Make sure the start command is correct in npm.command", e);
        }
    }

    private static void launchBrowser() {
        try {
            playwright = Playwright.create();
            
            // Try to launch browser with error handling
            // Check system property for headless mode, default to false (headed mode)
            String headlessProperty = System.getProperty("headless", "false");
            boolean headless = Boolean.parseBoolean(headlessProperty);
            
            BrowserType.LaunchOptions options = new BrowserType.LaunchOptions()
                    .setHeadless(headless);
            
            browser = playwright.chromium().launch(options);
            
        } catch (PlaywrightException e) {
            String errorMessage = e.getMessage();
            
            if (errorMessage != null && errorMessage.contains("Executable doesn't exist")) {
                System.err.println("\n❌ ERROR: Playwright browsers are not installed!");
                System.err.println("\nPlease install browsers before running tests:");
                System.err.println("  mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args=\"install chromium\"");
                System.err.println("\nOr use the installation script:");
                System.err.println("  ./install-browsers.sh\n");
            } else if (errorMessage != null && (errorMessage.contains("timed out") || errorMessage.contains("Download"))) {
                System.err.println("\n❌ ERROR: Browser download timed out or failed!");
                System.err.println("\nPlease install browsers manually:");
                System.err.println("  mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args=\"install chromium\"");
                System.err.println("\nOr check your internet connection and try again.\n");
            }
            
            throw new AssertionError("Failed to launch browser. See error messages above for installation instructions.", e);
        }
    }
    
    @AfterAll
    static void globalTearDown() {
        // Close browser first
        closeBrowser();
        // Then stop npm app
        stopNpmApp();
    }

    private static void closeBrowser() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    private static void stopNpmApp() {
        if (npmProcess != null && npmProcess.isAlive()) {
            System.out.println("Stopping Angular dev server...");
            npmProcess.destroy();
            try {
                // Wait for process to terminate, force kill if needed
                // Angular dev server may need a moment to clean up
                boolean terminated = npmProcess.waitFor(10, TimeUnit.SECONDS);
                if (!terminated) {
                    System.out.println("Angular dev server did not stop gracefully, forcing termination...");
                    npmProcess.destroyForcibly();
                    npmProcess.waitFor(2, TimeUnit.SECONDS);
                }
                System.out.println("✓ Angular dev server stopped");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                npmProcess.destroyForcibly();
            }
        }
    }

    /**
     * Wait for the Angular dev server to be ready by checking if it responds to HTTP requests
     */
    private static void waitForServer(String url, int timeoutSeconds) {
        long startTime = System.currentTimeMillis();
        long timeout = timeoutSeconds * 1000L;
        int checkCount = 0;

        System.out.print("Waiting for server");

        while (System.currentTimeMillis() - startTime < timeout) {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(2000);
                connection.setReadTimeout(2000);
                connection.setInstanceFollowRedirects(false);

                int responseCode = connection.getResponseCode();

                // Angular dev server typically returns 200 OK or 304 Not Modified
                // Also accept redirects (301, 302) as server is running
                if (responseCode == HttpURLConnection.HTTP_OK ||
                    responseCode == HttpURLConnection.HTTP_NOT_MODIFIED ||
                    responseCode == HttpURLConnection.HTTP_MOVED_TEMP ||
                    responseCode == HttpURLConnection.HTTP_MOVED_PERM) {
                    System.out.println(); // New line after dots
                    return; // Server is ready
                }
            } catch (IOException e) {
                // Server not ready yet, continue waiting
            }

            // Show progress every 2 seconds
            checkCount++;
            if (checkCount % 4 == 0) {
                System.out.print(".");
            }

            try {
                Thread.sleep(500); // Wait 500ms before next check
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting for server", e);
            }
        }

        System.out.println(); // New line after dots
        throw new RuntimeException(
            "Angular dev server did not become ready within " + timeoutSeconds + " seconds.\n" +
            "Please check:\n" +
            "  1. The app directory is correct: " + APP_DIR + "\n" +
            "  2. 'npm run dev' command exists in package.json\n" +
            "  3. Dependencies are installed (run 'npm install')\n" +
            "  4. Port " + APP_URL.replace("http://localhost:", "") + " is not already in use"
        );
    }

    @BeforeEach
    void createContextAndPage() {
        if (browser == null) {
            throw new IllegalStateException("Browser not initialized. Check @BeforeAll setup.");
        }
        context = browser.newContext();
        page = context.newPage();
    }
    
    @AfterEach
    void closeContext() {
        if (context != null) {
            context.close();
        }
    }
    
    /**
     * Override this method to change headless mode for specific tests
     * @return true to run in headless mode, false to show browser
     */
    protected boolean isHeadless() {
        // Check system property, default to false (headed mode)
        String headless = System.getProperty("headless", "false");
        return Boolean.parseBoolean(headless);
    }
    
    /**
     * Get the current page instance
     * @return the Playwright Page instance
     */
    protected Page getPage() {
        return page;
    }
}