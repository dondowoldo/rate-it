package it.rate.webapp.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;

@Component
public class ImageEditor {

  private final int MEGABYTE = 1024 * 1024;

  
  public void reduceImageSize(Path imagePath) throws IOException {
    
    long fileSizeBytes = Files.size(imagePath);

    if (fileSizeBytes <= MEGABYTE) {
      return;
    }
    
    double reductionRate = calculateReductionRate(fileSizeBytes);

    Thumbnails.of(imagePath.toFile())
            .outputQuality(reductionRate)
            .scale(1)
            .toFile(imagePath.toFile());
  }

  private double calculateReductionRate(long fileSizeByte) {

    if (fileSizeByte < 2 * MEGABYTE) {
      return 0.5;
    } else if (fileSizeByte < 3 * MEGABYTE) {
      return 0.3;
    } else if (fileSizeByte < 4 * MEGABYTE) {
      return 0.25;
    } else {
      return 0.2;
    }
  }
}
