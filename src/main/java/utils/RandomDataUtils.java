package utils;

import java.util.UUID;

public class RandomDataUtils {
    public static String randomString() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
