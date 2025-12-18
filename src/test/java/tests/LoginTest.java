package tests;

import com.interviewcards.BaseTest;
import config.Config;
import io.qameta.allure.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pages.LoginPage;
import pages.enums.LoginSubmitType;

import java.util.stream.Stream;

import static io.qameta.allure.SeverityLevel.CRITICAL;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        mainPage = loginPage.login(login, Config.PASSWORD, submitType);

        assertTrue(
                mainPage.isLogoutButtonDisplayed(),
                "Logout button should be visible after successful login"
        );
    }

    static Stream<Arguments> validLoginData() {
        return Stream.of(
                Arguments.of(Config.EMAIL, LoginSubmitType.CLICK),
                Arguments.of(Config.EMAIL, LoginSubmitType.ENTER),
                Arguments.of(Config.USERNAME, LoginSubmitType.CLICK),
                Arguments.of(Config.USERNAME, LoginSubmitType.ENTER)
        );
    }
}