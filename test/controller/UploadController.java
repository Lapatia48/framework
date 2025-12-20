package controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import annotation.Api;
import annotation.Controller;
import annotation.Get;
import annotation.Post;
import annotation.Url;
import modelAndView.ModelAndView;
import servlet.FrameworkConfig;

@Controller
public class UploadController {

    @Url("/fichier-attach")
    @Get
    public ModelAndView showUploadForm() {
        return new ModelAndView("fichier-attach.jsp");
    }

    @Url("/fichier-attach")
    @Post
    @Api
    public Map<String, Object> saveUpload(Map<String, byte[]> files, Map<String, Object> formData) {
        List<String> savedFiles = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        String uploadDir = FrameworkConfig.getUploadDir();
        
        // Créer le dossier s'il n'existe pas
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            return Map.of(
                "success", false,
                "error", "Impossible de créer le dossier d'upload: " + e.getMessage()
            );
        }
        
        // Sauvegarder chaque fichier
        for (Map.Entry<String, byte[]> entry : files.entrySet()) {
            String fileName = entry.getKey();
            byte[] fileContent = entry.getValue();
            
            try {
                Path filePath = Paths.get(uploadDir, fileName);
                Files.write(filePath, fileContent);
                savedFiles.add(fileName + " (" + fileContent.length + " bytes)");
            } catch (IOException e) {
                errors.add(fileName + ": " + e.getMessage());
            }
        }
        
        return Map.of(
            "success", errors.isEmpty(),
            "savedFiles", savedFiles,
            "errors", errors,
            "formData", formData,
            "uploadDir", uploadDir
        );
    }
}
