package config;

public class Config {

    private static String getEnvOrProp(String key) {
        String prop = System.getProperty(key);
        if (prop != null && !prop.isBlank()) {
            return prop;
        }
        String env = System.getenv(key);
        if (env != null && !env.isBlank()) {
            return env;
        }
        return null;
    }

    public static final String BASE_URL =
            getEnvOrProp("BASE_URL") != null ? getEnvOrProp("BASE_URL") : "https://pyavchik.space";

    public static final String USERNAME =
            getEnvOrProp("TEST_USERNAME");

    public static final String PASSWORD =
            getEnvOrProp("TEST_PASSWORD");

    public static final String EMAIL =
            getEnvOrProp("TEST_EMAIL");

    static {
        if (USERNAME == null || PASSWORD == null) {
            throw new RuntimeException(
                    "❌ TEST_USERNAME / TEST_PASSWORD are not set (env vars or -D system properties)"
            );
        }
    }
}
