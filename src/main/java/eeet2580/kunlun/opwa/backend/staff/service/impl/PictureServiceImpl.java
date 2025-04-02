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