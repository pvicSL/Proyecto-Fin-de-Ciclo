package proyecto.upload;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.upload")
public class UploadConfig {
	private String dir = "uploads/";
    private String url = "http://localhost:8080/uploads/";
    private String maxFileSize = "5MB";
    private String maxRequestSize = "20MB";
    
    // Getters y Setters
    public String getDir() { return dir; }
    public void setDir(String dir) { this.dir = dir; }
    
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    
    public String getMaxFileSize() { return maxFileSize; }
    public void setMaxRequestSize(String maxRequestSize) { this.maxRequestSize = maxRequestSize; }
    
    public String getMaxRequestSize() { return maxRequestSize; }
    public void setMaxFileSize(String maxFileSize) { this.maxFileSize = maxFileSize; }
}
