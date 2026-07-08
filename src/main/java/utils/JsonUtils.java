package utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public final class JsonUtils {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    private JsonUtils() {}

    public static Map<String, Object> read(String resourcePath) {
        try (InputStream inputStream = getResourceAsStream(resourcePath)) {
            return OBJECT_MAPPER.readValue(inputStream, new TypeReference<>() {});
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read JSON resource: " + resourcePath, e);
        }
    }

    public static <T> T readJson(String filePath, Class<T> type) {
        try {
            return OBJECT_MAPPER.readValue(toPath(filePath).toFile(), type);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read JSON file as " + type.getSimpleName() + ": " + filePath, e);
        }
    }

    public static <T> T readJsonResource(String resourcePath, Class<T> type) {
        try (InputStream inputStream = getResourceAsStream(resourcePath)) {
            return OBJECT_MAPPER.readValue(inputStream, type);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read JSON resource as " + type.getSimpleName() + ": "
                    + resourcePath, e);
        }
    }

    public static <T> List<T> readJsonList(String filePath, Class<T> elementType) {
        try {
            return OBJECT_MAPPER.readValue(toPath(filePath).toFile(),
                    OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, elementType));
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read JSON file as List<" + elementType.getSimpleName()
                    + ">: " + filePath, e);
        }
    }

    public static <T> List<T> readJsonListResource(String resourcePath, Class<T> elementType) {
        try (InputStream inputStream = getResourceAsStream(resourcePath)) {
            return OBJECT_MAPPER.readValue(inputStream,
                    OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, elementType));
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read JSON resource as List<" + elementType.getSimpleName()
                    + ">: " + resourcePath, e);
        }
    }

    public static void writeJson(String filePath, Object data) {
        Path path = toPath(filePath);
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            OBJECT_MAPPER.writeValue(path.toFile(), data);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to write JSON file: " + filePath, e);
        }
    }

    public static String prettyPrint(Object data) {
        try {
            Object json = data instanceof String text ? OBJECT_MAPPER.readValue(text, Object.class) : data;
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Unable to pretty print JSON", e);
        }
    }

    private static InputStream getResourceAsStream(String resourcePath) {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new IllegalArgumentException("JSON resource not found: " + resourcePath);
        }
        return inputStream;
    }

    private static Path toPath(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("JSON file path must not be null or blank");
        }
        return Paths.get(filePath);
    }
}
