package tests;

import com.interviewcards.BaseTest;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import pages.LoginPage;
import pages.enums.LoginSubmitType;
import utils.DatabaseHelper;

import static config.Config.*;
import static io.qameta.allure.SeverityLevel.CRITICAL;
import static io.qameta.allure.SeverityLevel.NORMAL;
import static org.junit.jupiter.api.Assertions.*;
import static pages.LoginPage.*;

@Epic("Authentication")
@Feature("Login")
public class LoginTest extends BaseTest {

    @Epic("Login")
    @Severity(CRITICAL)
    @Feature("Successful login")
    @Story("User logs in with valid credentials")
    @Description("Test verifies that user can successfully login using email or username")
    @ParameterizedTest(name = "Login with {0} using {1}")
    @MethodSource("utils.LoginTestDataProvider#validLoginData")
    void loginWithValidCredentials(String login, LoginSubmitType submitType) {
        loginPage = new LoginPage(page);
        mainPage = loginPage.login(login, PASSWORD, submitType);

        assertTrue(
                mainPage.isLogoutButtonDisplayed(),
                "Logout button should be visible after successful login"
        );
    }

    @Test
    @Epic("Login")
    @Severity(CRITICAL)
    @Feature("Successful login")
    @Story("User logs in with Leading/Trailing Spaces")
    @Description("Test verifies that user can successfully login with Leading/Trailing Spaces in password")
    void loginWithPasswordLeadingTrailingSpaces() {
        loginPage = new LoginPage(page);
        mainPage = loginPage.login(EMAIL, " " + PASSWORD + " ", LoginSubmitType.CLICK);

        assertTrue(
                mainPage.isLogoutButtonDisplayed(),
                "Logout button should be visible after successful login"
        );
    }

    @Test
    @Epic("Login")
    @Feature("Form Validation")
    @Story("Server-side email validation")
    @Severity(NORMAL)
    @DisplayName("Login with invalid email format should show server error")
    @Description("Test verifies that entering invalid email format (user@) triggers server-side validation error")
    void loginWithInvalidEmailFormatShouldShowServerError() {
        loginPage = new LoginPage(page);
        loginPage = loginPage.loginWithInvalidEmailFormat(EMAIL_INVALID_FORMAT, PASSWORD);

        assertTrue(page.url().contains("/login"), "Should remain on login page");
        assertEquals(
                INVALID_USERNAME_OR_PASSWORD_HINT,
                loginPage.getInvalidEmailOrPasswordHint(),
                "Server-side error message should match expected hint for empty username"
        );
    }

    @Test
    @Epic("Login")
    @Feature("Form Validation")
    @Story("Empty field validation")
    @Severity(NORMAL)
    @DisplayName("Login with empty username should show server error")
    @Description("Test verifies that submitting login form with empty username field triggers server-side validation error")
    void loginWithEmptyLogin() {
        loginPage = new LoginPage(page);
        loginPage = loginPage.loginWithEmptyEmail(PASSWORD);

        assertTrue(
                page.url().contains("/login"),
                "Should remain on login page after validation error"
        );

        assertEquals(
                EMPTY_USERNAME_HINT,
                loginPage.getInvalidUsernameHint(),
                "Server-side error message should match expected hint for empty username"
        );
    }

    @Test
    @Epic("Login")
    @Feature("Form Validation")
    @Story("Empty field validation")
    @Severity(NORMAL)
    @DisplayName("Login with empty password should show server error")
    @Description("Test verifies that submitting login form with empty password field triggers server-side validation error")
    void loginWithEmptyPassword() {
        loginPage = new LoginPage(page);
        loginPage = loginPage.loginWithEmptyPassword(USERNAME);

        assertTrue(
                page.url().contains("/login"),
                "Should remain on login page after validation error"
        );

        assertEquals(
                EMPTY_PASSWORD_HINT,
                loginPage.getInvalidPasswordHint(),
                "Server-side error message should match expected hint for empty username"
        );

    }

    @Test
    @Epic("Login")
    @Feature("Form Validation")
    @Story("Empty field validation")
    @Severity(NORMAL)
    @DisplayName("Login with invalid password should show server error")
    @Description("Test verifies that submitting login form with invalid password field triggers server-side validation error")
    void loginWithIncorrectPassword() {
        loginPage = new LoginPage(page);
        loginPage = loginPage.loginWithIncorrectPassword(USERNAME, "wrong_password");

        assertTrue(
                page.url().contains("/login"),
                "Should remain on login page after validation error"
        );

        assertEquals(
                INVALID_USERNAME_OR_PASSWORD_HINT,
                loginPage.getInvalidEmailOrPasswordHint(),
                "Server-side error message should match expected hint for empty username"
        );

    }

    @Test
    @Epic("Login")
    @Feature("Form Validation")
    @Story("Empty field validation")
    @Severity(NORMAL)
    @DisplayName("Login with empty login and password should show server error")
    @Description("Test verifies that submitting login form with empty login and empty password field triggers server-side validation error")
    void loginWithEmptyLoginAndPassword() {
        loginPage = new LoginPage(page);
        loginPage = loginPage.loginWithEmptyLoginAndPassword();

        assertTrue(
                page.url().contains("/login"),
                "Should remain on login page after validation error"
        );

        assertEquals(
                EMPTY_PASSWORD_HINT,
                loginPage.getInvalidPasswordHint(),
                "Server-side error message should match expected hint for empty username"
        );

        assertEquals(
                EMPTY_USERNAME_HINT,
                loginPage.getInvalidUsernameHint(),
                "Server-side error message should match expected hint for empty username"
        );

    }

    @Test
    @Epic("Login")
    @Feature("Form Validation")
    @Story("Empty field validation")
    @Severity(NORMAL)
    @DisplayName("Login with Non Existent login and password should show server error")
    @Description("Test verifies that submitting login form with Non Existent login and empty password field triggers server-side validation error")
    void loginWithNonExistentLogin() {
        loginPage = new LoginPage(page);
        loginPage = loginPage.loginWithNonExistentLoginAndPassword("NonExistentUser", "NonExistentPassword");

        assertTrue(page.url().contains("/login"), "Should remain on login page");
        assertEquals(
                INVALID_USERNAME_OR_PASSWORD_HINT,
                loginPage.getInvalidEmailOrPasswordHint(),
                "Server-side error message should match expected hint for empty username"
        );

    }

    @Test
    @Epic("Login")
    @Feature("Security")
    @Story("SQL Injection prevention")
    @Severity(CRITICAL)
    @DisplayName("Login with SQL Injection attempt should be rejected")
    @Description("Test verifies that SQL Injection attempts in login credentials are properly rejected")
    void loginWithSQLInjectionShouldBeRejected() {
        loginPage = new LoginPage(page);
        loginPage = loginPage.loginWithInjectionLoginAndPassword("' OR '1'='1", "' OR '1'='1' --");

        assertTrue(
                page.url().contains("/login"),
                "SQL injection attempt should be rejected - user should remain on login page"
        );

        assertEquals(
                INVALID_USERNAME_OR_PASSWORD_HINT,
                loginPage.getInvalidEmailOrPasswordHint(),
                "Error message should indicate invalid credentials (SQL injection rejected)"
        );

    }

    @Test
    @Epic("Login")
    @Feature("Security")
    @Story("SQL Injection prevention")
    @Severity(CRITICAL)
    @DisplayName("Login with XSS Injection attempt should be rejected")
    @Description("Test verifies that XSS Injection attempts in login credentials are properly rejected")
    void loginWithXSSInjectionShouldBeRejected() {
        loginPage = new LoginPage(page);
        loginPage = loginPage.loginWithInjectionLoginAndPassword("<script>alert('x')</script>", "<script>alert('x')</script>");

        assertTrue(
                page.url().contains("/login"),
                "Login with XSS injection attempt should be rejected - user should remain on login page"
        );

        assertEquals(
                INVALID_USERNAME_OR_PASSWORD_HINT,
                loginPage.getInvalidEmailOrPasswordHint(),
                "Error message should indicate invalid credentials (SQL injection rejected)"
        );

        assertFalse(
                page.getByRole(AriaRole.BUTTON,
                        new Page.GetByRoleOptions().setName("Logout")).isVisible(),
                "User should not be logged in after SQL injection attempt"
        );
    }

    @Test
    @Epic("Login")
    @Feature("Security")
    @Story("Locked-After-Multiple-Failure")
    @Severity(CRITICAL)
    @DisplayName("Login with Locked-After-Multiple-Failure account should be rejected")
    @Description("Test verifies that Locked-After-Multiple-Failure attempts in login credentials are properly rejected")
    void loginWithLockedAfterMultipleFailure() {
        String testUser = "testlockuser";

        try {
            loginPage = new LoginPage(page);
            loginPage = loginPage.lockUserAfterMultipleFailureAttempts(testUser, "wrong_password");

            assertTrue(
                    page.url().contains("/login"),
                    "Login locked user should be rejected - user should remain on login page"
            );

            assertEquals(
                    TOO_MANY_FAILED_ATTEMPTS_HINT,
                    loginPage.getLockedUserHint(),
                    "Error message should indicate account is locked"
            );
        } finally {
            DatabaseHelper.unlockUser(testUser);
        }
    }

    @Test
    @Epic("Login")
    @Feature("Session")
    @Story("Session persistence")
    @Severity(CRITICAL)
    @DisplayName("User remains logged in after page refresh")
    @Description("Verifies that user session persists after browser page refresh")
    void userRemainsLoggedInAfterRefresh() {
        loginPage = new LoginPage(page);
        mainPage = loginPage.login(EMAIL, PASSWORD, LoginSubmitType.CLICK);

        assertTrue(
                mainPage.isLogoutButtonDisplayed(),
                "User should be logged in before refresh"
        );

        page.reload();

        assertTrue(
                mainPage.isLogoutButtonDisplayed(),
                "User should remain logged in after page refresh"
        );
    }
}