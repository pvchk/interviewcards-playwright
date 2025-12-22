package utils;

import config.Config;
import org.junit.jupiter.params.provider.Arguments;
import pages.enums.LoginSubmitType;

import java.util.stream.Stream;

/**
 * Test data providers for login-related tests.
 * Provides reusable test data for parameterized tests.
 */
public class LoginTestDataProvider {

    /**
     * Provides valid login credentials with different combinations of:
     * - Email/Username (with and without whitespaces)
     * - Submit methods (ENTER key vs CLICK button)
     *
     * @return Stream of Arguments for parameterized login tests
     */
    public static Stream<Arguments> validLoginData() {
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

