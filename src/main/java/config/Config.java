package config;

public class Config {

    public static final String BASE_URL =
            System.getenv().getOrDefault("BASE_URL", "https://pyavchik.space");

    public static final String USERNAME =
            System.getenv("TEST_USERNAME");

    public static final String PASSWORD =
            System.getenv("TEST_PASSWORD");

    public static final String EMAIL =
            System.getenv("TEST_EMAIL");

    public static final String EMAIL_WHITESPACES =
            " " + System.getenv("TEST_EMAIL") + " ";

    public static final String USERNAME_WHITESPACES =
            " " + System.getenv("TEST_USERNAME") + " ";

    public static final String EMAIL_INVALID_FORMAT = "user@";

    static {
        if (USERNAME == null || PASSWORD == null) {
            throw new RuntimeException(
                    "❌ Env vars TEST_USERNAME / TEST_PASSWORD are not set"
            );
        }
    }
}
