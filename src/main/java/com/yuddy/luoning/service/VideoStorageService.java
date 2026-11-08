package com.yuddy.luoning.service;

import com.yuddy.luoning.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

// Luu file video tren o dia cua server, ngoai thu muc cong khai
@Service
public class VideoStorageService {

    private static final Map<String, String> ALLOWED_TYPES = Map.of(
            "video/mp4", ".mp4",
            "video/webm", ".webm");

    private final Path videoDir;

    public VideoStorageService(@Value("${app.storage.video-dir}") String videoDir) {
        this.videoDir = Path.of(videoDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.videoDir);
        } catch (IOException e) {
            throw new UncheckedIOException("Khong tao duoc thu muc video: " + this.videoDir, e);
        }
    }

    // Tra ve ten file da luu. Ten la UUID nen khong trung va khong doan duoc
    public String store(MultipartFile file) {
        if (file.isEmpty()) {
            throw ApiException.badRequest("File video rong");
        }
        String extension = ALLOWED_TYPES.get(file.getContentType());
        if (extension == null) {
            throw ApiException.badRequest("Chi chap nhan video mp4 hoac webm");
        }

        String fileName = UUID.randomUUID() + extension;
        try {
            file.transferTo(videoDir.resolve(fileName));
        } catch (IOException e) {
            throw new UncheckedIOException("Khong luu duoc file video", e);
        }
        return fileName;
    }

    public Resource load(String fileName) {
        Resource resource = new FileSystemResource(videoDir.resolve(fileName));
        if (!resource.exists()) {
            throw ApiException.notFound("Khong tim thay file video");
        }
        return resource;
    }

    // Chi xoa file sau khi transaction commit thanh cong.
    // Neu DB rollback ma file da bi xoa thi bai giang se tro toi file khong con ton tai.
    public void deleteAfterCommit(String fileName) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                delete(fileName);
            }
        });
    }

    public void delete(String fileName) {
        try {
            Files.deleteIfExists(videoDir.resolve(fileName));
        } catch (IOException e) {
            throw new UncheckedIOException("Khong xoa duoc file video", e);
        }
    }
}
