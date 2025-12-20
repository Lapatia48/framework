package servlet;

public final class FrameworkConfig {
    private static String uploadDir;

    private FrameworkConfig() {
    }

    public static String getUploadDir() {
        return uploadDir;
    }

    public static void setUploadDir(String uploadDir) {
        FrameworkConfig.uploadDir = uploadDir;
    }
}
