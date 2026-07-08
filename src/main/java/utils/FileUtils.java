package utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public final class FileUtils {
    private FileUtils() {}

    public static boolean exists(String path) {
        return Files.exists(toPath(path));
    }

    public static Path createDirectories(String directoryPath) {
        try {
            return Files.createDirectories(toPath(directoryPath));
        } catch (IOException e) {
            throw new IllegalStateException("Unable to create directory: " + directoryPath, e);
        }
    }

    public static boolean deleteFile(String filePath) {
        try {
            return Files.deleteIfExists(toPath(filePath));
        } catch (IOException e) {
            throw new IllegalStateException("Unable to delete file: " + filePath, e);
        }
    }

    public static String getAbsolutePath(String path) {
        return toPath(path).toAbsolutePath().normalize().toString();
    }

    public static Path getResourcePath(String resourcePath) {
        URL resource = Thread.currentThread().getContextClassLoader().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalArgumentException("Resource not found on classpath: " + resourcePath);
        }
        try {
            return Paths.get(resource.toURI()).toAbsolutePath().normalize();
        } catch (URISyntaxException | IllegalArgumentException e) {
            throw new IllegalStateException("Unable to resolve resource path: " + resourcePath, e);
        }
    }

    public static Path waitForDownloadedFile(String downloadDirectory, String fileName, Duration timeout) {
        Objects.requireNonNull(fileName, "fileName must not be null");
        Path directory = toPath(downloadDirectory);
        Instant deadline = Instant.now().plus(timeout);

        while (Instant.now().isBefore(deadline)) {
            Path file = directory.resolve(fileName);
            if (isCompletedDownload(file)) {
                return file.toAbsolutePath().normalize();
            }
            sleepBriefly();
        }

        throw new IllegalStateException("Downloaded file was not found within " + timeout + ": "
                + directory.resolve(fileName));
    }

    public static Path waitForDownloadedFile(String downloadDirectory, String fileName, long timeoutSeconds) {
        return waitForDownloadedFile(downloadDirectory, fileName, Duration.ofSeconds(timeoutSeconds));
    }

    public static Path waitForDownloadedFile(String downloadDirectory, String filePrefix, String fileExtension,
                                             Duration timeout) {
        Path directory = toPath(downloadDirectory);
        Instant deadline = Instant.now().plus(timeout);

        while (Instant.now().isBefore(deadline)) {
            try (Stream<Path> files = Files.list(directory)) {
                Optional<Path> match = files
                        .filter(Files::isRegularFile)
                        .filter(path -> path.getFileName().toString().startsWith(filePrefix))
                        .filter(path -> path.getFileName().toString().endsWith(fileExtension))
                        .filter(FileUtils::isCompletedDownload)
                        .findFirst();
                if (match.isPresent()) {
                    return match.get().toAbsolutePath().normalize();
                }
            } catch (IOException e) {
                throw new IllegalStateException("Unable to inspect download directory: " + downloadDirectory, e);
            }
            sleepBriefly();
        }

        throw new IllegalStateException("Downloaded file matching " + filePrefix + "*" + fileExtension
                + " was not found within " + timeout + " in " + downloadDirectory);
    }

    private static boolean isCompletedDownload(Path file) {
        String name = file.getFileName().toString();
        return Files.exists(file)
                && Files.isRegularFile(file)
                && !name.endsWith(".crdownload")
                && !name.endsWith(".part")
                && !name.endsWith(".tmp");
    }

    private static Path toPath(String path) {
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("Path must not be null or blank");
        }
        return Paths.get(path);
    }

    private static void sleepBriefly() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for downloaded file", e);
        }
    }
}
