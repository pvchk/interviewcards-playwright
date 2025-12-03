package com.interviewcards;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
    static Playwright playwright;
    static Browser browser;
    
    // NPM server process
    static Process npmProcess;
    static String APP_DIR;
    static String APP_URL;
    static int SERVER_STARTUP_TIMEOUT;
    
    // Test instance variables
    BrowserContext context;
    Page page;
    
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
        
        // Get server startup timeout
        String timeoutStr = System.getProperty("server.startup.timeout",
            props.getProperty("server.startup.timeout", "30"));
        SERVER_STARTUP_TIMEOUT = Integer.parseInt(timeoutStr);
        
        System.out.println("Configuration loaded:");
        System.out.println("  App directory: " + APP_DIR);
        System.out.println("  App URL: " + APP_URL);
        System.out.println("  Server timeout: " + SERVER_STARTUP_TIMEOUT + "s");
    }
    
    @BeforeAll
    static void setUp() {
        // Start npm app first
        startNpmApp();
        // Then launch browser
        launchBrowser();
    }
    
    private static void startNpmApp() {
        try {
            System.out.println("Starting npm app at " + APP_DIR);
            ProcessBuilder processBuilder = new ProcessBuilder("npm", "start");
            processBuilder.directory(new File(APP_DIR));
            processBuilder.redirectErrorStream(true);
            npmProcess = processBuilder.start();
            
            // Wait for server to be ready
            waitForServer(APP_URL, SERVER_STARTUP_TIMEOUT);
            System.out.println("NPM app started successfully");
            
        } catch (Exception e) {
            System.err.println("Failed to start npm app: " + e.getMessage());
            throw new RuntimeException("Could not start npm app", e);
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
    static void tearDown() {
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
            System.out.println("Stopping npm app...");
            npmProcess.destroy();
            try {
                // Wait for process to terminate, force kill if needed
                boolean terminated = npmProcess.waitFor(5, TimeUnit.SECONDS);
                if (!terminated) {
                    npmProcess.destroyForcibly();
                    System.out.println("Forcefully stopped npm app");
                } else {
                    System.out.println("NPM app stopped successfully");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                npmProcess.destroyForcibly();
            }
        }
    }
    
    /**
     * Wait for the server to be ready by checking if it responds to HTTP requests
     */
    private static void waitForServer(String url, int timeoutSeconds) {
        long startTime = System.currentTimeMillis();
        long timeout = timeoutSeconds * 1000L;
        
        while (System.currentTimeMillis() - startTime < timeout) {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(1000);
                connection.setReadTimeout(1000);
                int responseCode = connection.getResponseCode();
                
                if (responseCode == HttpURLConnection.HTTP_OK || 
                    responseCode == HttpURLConnection.HTTP_MOVED_TEMP) {
                    return; // Server is ready
                }
            } catch (IOException e) {
                // Server not ready yet, continue waiting
            }
            
            try {
                Thread.sleep(500); // Wait 500ms before next check
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting for server", e);
            }
        }
        
        throw new RuntimeException("Server did not become ready within " + timeoutSeconds + " seconds");
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

