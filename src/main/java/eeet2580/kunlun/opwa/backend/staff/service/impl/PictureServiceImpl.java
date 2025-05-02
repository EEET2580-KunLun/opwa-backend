package eeet2580.kunlun.opwa.backend.staff.service.impl;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.StorageException;
import com.google.firebase.cloud.StorageClient;
import eeet2580.kunlun.opwa.backend.staff.service.PictureService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PictureServiceImpl implements PictureService {

    private final StorageClient storageClient;

    @Value("${firebase.bucket-name}")
    private String bucketName;

    @Override
    @Transactional
    public String uploadPicture(MultipartFile file, String staffId) throws IOException {
        try {
            String filename = generateFilename(file, staffId);
            BlobId blobId = BlobId.of(bucketName, "picture/" + filename);

            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(file.getContentType())
                    .build();

            Blob blob = storageClient.bucket().create(String.valueOf(blobInfo), file.getBytes());

            // Return public URL with a signed URL that expires after a period
            return blob.signUrl(365, TimeUnit.DAYS).toString();
        } catch (StorageException e) {
            throw new RuntimeException("Failed to upload picture: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void removePicture(String url) {
        if (url == null || url.isEmpty()) {
            return;
        }

        try {
            // Parse the URL to extract the filename
            String pathPart = url.contains("?") ? url.substring(0, url.indexOf("?")) : url;
            String filename = pathPart.substring(pathPart.lastIndexOf("/") + 1);

            // For complex BlobInfo URLs, do additional parsing
            if (filename.contains("%")) {
                try {
                    filename = java.net.URLDecoder.decode(filename, "UTF-8");
                } catch (Exception e) {
                    // Continue with encoded filename if decoding fails
                }
            }

            BlobId blobId = BlobId.of(bucketName, "picture/" + filename);
            Blob blob = storageClient.bucket().get(String.valueOf(blobId));

            if (blob != null) {
                blob.delete();
            } else {
                // Log warning but don't throw exception
                System.out.println("Warning: Blob not found for deletion: " + blobId);
            }
        } catch (StorageException e) {
            throw new RuntimeException("Failed to delete picture: " + e.getMessage(), e);
        }
    }

    private String generateFilename(MultipartFile file, String staffId) {
        String extension = getFileExtension(file.getOriginalFilename());
        return staffId + "-" + UUID.randomUUID() + extension;
    }

    private String getFileExtension(String filename) {
        if (filename == null) return ".jpg";
        int lastDotIndex = filename.lastIndexOf('.');
        return (lastDotIndex >= 0) ? filename.substring(lastDotIndex) : ".jpg";
    }
}