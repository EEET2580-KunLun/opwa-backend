package eeet2580.kunlun.opwa.backend.staff.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PictureService {
    /**
     * Uploads an avatar image for a staff member
     *
     * @param file    The avatar image file
     * @param staffId The ID of the staff member
     * @return URL to the uploaded avatar
     * @throws IOException If file cannot be processed
     */
    String uploadPicture(MultipartFile file, String staffId) throws IOException;

    void removePicture(String url);
}