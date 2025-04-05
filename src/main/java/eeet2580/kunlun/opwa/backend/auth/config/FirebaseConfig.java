package eeet2580.kunlun.opwa.backend.auth.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

@Component
@Configuration
public class FirebaseConfig {
    @Value("${firebase.bucket-name}")
    private String bucketName;

    @Value("${firebase.project-id}")
    private String projectId;

    @Value("${firebase.credentials-path}")
    private String credentialsPath;

    @Value("${FIREBASE_SERVICE_ACCOUNT_BASE64:}")
    private String firebaseServiceAccountBase64;

    @Bean
    public StorageClient storageClient() throws IOException {
        GoogleCredentials credentials = GoogleCredentials.fromStream(
                new ClassPathResource(credentialsPath).getInputStream());

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .setProjectId(projectId)
                .setStorageBucket(bucketName)
                .build();

        FirebaseApp app;
        try {
            app = FirebaseApp.getInstance();
        } catch (IllegalStateException e) {
            // No app exists, initialize a new one
            app = FirebaseApp.initializeApp(options);
        }

        return StorageClient.getInstance(app);
    }

    @PostConstruct
    public void setup() throws IOException {
        if (firebaseServiceAccountBase64 != null && !firebaseServiceAccountBase64.isEmpty()) {
            byte[] decodedBytes = Base64.getDecoder().decode(firebaseServiceAccountBase64);
            try (FileOutputStream fos = new FileOutputStream("src/main/resources/" + credentialsPath)) {
                fos.write(decodedBytes);
            }
        }
    }
}