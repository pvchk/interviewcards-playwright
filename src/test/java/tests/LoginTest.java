package tests;

import com.interviewcards.BaseTest;
import config.Config;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pages.LoginPage;
import pages.enums.LoginSubmitType;

import java.util.stream.Stream;

import static config.Config.EMAIL_INVALID_FORMAT;
import static config.Config.PASSWORD;
import static io.qameta.allure.SeverityLevel.CRITICAL;
import static io.qameta.allure.SeverityLevel.NORMAL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static pages.LoginPage.INVALID_USERNAME_OR_PASSWORD_HINT;

@Epic("Authentication")
@Feature("Login")
public class LoginTest extends BaseTest {

    @Epic("Login")
    @Severity(CRITICAL)
    @Feature("Successful login")
    @Story("User logs in with valid credentials")
    @Description("Test verifies that user can successfully login using email or username")
    @ParameterizedTest(name = "Login with {0} using {1}")
    @MethodSource("validLoginData")
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
    @Feature("Form Validation")
    @Story("Server-side email validation")
    @Severity(NORMAL)
    @DisplayName("Login with invalid email format should show server error")
    @Description("Test verifies that entering invalid email format (user@) triggers server-side validation error")
    void loginWithInvalidEmailFormat_ShouldShowServerError() {
        loginPage = new LoginPage(page);
        loginPage = loginPage.loginWithInvalidEmailFormat(EMAIL_INVALID_FORMAT, PASSWORD);

        assertTrue(page.url().contains("/login"), "Should remain on login page");
        assertEquals(
                INVALID_USERNAME_OR_PASSWORD_HINT,
                loginPage.getInvalidUsernameOrPasswordHint(),
                "Server-side error message should be displayed"
        );
    }

    static Stream<Arguments> validLoginData() {
        return Stream.of(
                Arguments.of(Config.EMAIL_WHITESPACES, LoginSubmitType.ENTER),
                Arguments.of(Config.EMAIL_WHITESPACES, LoginSubmitType.CLICK),
                Arguments.of(Config.USERNAME_WHITESPACES, LoginSubmitType.ENTER),
                Arguments.of(Config.USERNAME_WHITESPACES, LoginSubmitType.CLICK),
                Arguments.of(Config.EMAIL, LoginSubmitType.CLICK),
                Arguments.of(Config.EMAIL, LoginSubmitType.ENTER),
                Arguments.of(Config.USERNAME, LoginSubmitType.CLICK),
                Arguments.of(Config.USERNAME, LoginSubmitType.ENTER)
        );
    }
}