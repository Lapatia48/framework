package servlet;

/**
 * Résultat d'un upload de fichier.
 * Géré automatiquement par le framework pour les contrôleurs utilisant Part ou byte[].
 */
public class UploadResult {
    private boolean ok;
    private String message;
    private String fileName;
    private String contentType;
    private long size;
    private String savedTo;

    public UploadResult() {}

    public UploadResult(boolean ok, String message) {
        this.ok = ok;
        this.message = message;
    }

    public static UploadResult success(String fileName, String contentType, long size, String savedTo) {
        UploadResult r = new UploadResult();
        r.ok = true;
        r.message = "Fichier uploadé avec succès";
        r.fileName = fileName;
        r.contentType = contentType;
        r.size = size;
        r.savedTo = savedTo;
        return r;
    }

    public static UploadResult error(String message) {
        return new UploadResult(false, message);
    }

    // Getters (nécessaires pour la sérialisation JSON par réflexion)
    public boolean isOk() { return ok; }
    public String getMessage() { return message; }
    public String getFileName() { return fileName; }
    public String getContentType() { return contentType; }
    public long getSize() { return size; }
    public String getSavedTo() { return savedTo; }

    // Setters
    public void setOk(boolean ok) { this.ok = ok; }
    public void setMessage(String message) { this.message = message; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public void setSize(long size) { this.size = size; }
    public void setSavedTo(String savedTo) { this.savedTo = savedTo; }
}
