package it.rate.webapp.services;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
  String saveImage(MultipartFile image) throws IOException;

  byte[] getImageById(String imageId);
}
