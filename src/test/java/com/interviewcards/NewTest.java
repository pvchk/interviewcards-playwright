package com.interviewcards;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Example test class demonstrating basic Playwright usage.
 * Extends BaseTest for common browser setup and error handling.
 */
public class NewTest extends BaseTest {
    
    @Test
    void shouldNavigateToExampleSite() {
        getPage().navigate("https://example.com");
        String title = getPage().title();
        assertEquals("Example Domain", title);
    }
    
    @Test
    void shouldCheckPageContent() {
        getPage().navigate("http://localhost:3000/ua");
        getPage().navigate("http://localhost:3000/ru");
        getPage().navigate("http://localhost:3000/en");
        String heading = getPage().locator("h1").textContent();
        assertNotNull(heading);
        assertTrue(heading.contains("Example"));
    }
}

