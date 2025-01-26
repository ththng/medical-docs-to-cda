package it.unisa.medical_docs_to_cda.controller;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

public class DicomConverter {

    public static void convertDicomSeriesToBinaryJpeg(String dicomFilePath, String outputDirPath) throws IOException {
        File dicomFile = new File(dicomFilePath);

        if (!dicomFile.exists()) {
            throw new IOException("DICOM file not found: " + dicomFilePath);
        }

        try (ImageInputStream iis = ImageIO.createImageInputStream(dicomFile)) {
            if (iis == null) {
                throw new IOException("Cannot create ImageInputStream from file: " + dicomFilePath);
            }

            Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("DICOM");
            if (!readers.hasNext()) {
                throw new IOException("No DICOM ImageReader found");
            }

            ImageReader reader = readers.next();
            reader.setInput(iis);

            int numImages = reader.getNumImages(true);
            for (int i = 0; i < numImages; i++) {
                BufferedImage image = reader.read(i);

                if (!isImageRelevant(image)) {
                    continue; // Skip irrelevant images
                }

                BufferedImage equalizedImage = adaptiveHistogramEqualization(image);
                BufferedImage binaryImage = adaptiveThresholding(equalizedImage);

                File outputDir = new File(outputDirPath);
                if (!outputDir.exists() && !outputDir.mkdirs()) {
                    throw new IOException("Failed to create output directory: " + outputDirPath);
                }

                File outputFile = new File(outputDir, "image_" + i + ".jpeg");
                if (!ImageIO.write(binaryImage, "JPEG", outputFile)) {
                    throw new IOException("Failed to write JPEG image to file: " + outputFile.getPath());
                }

                System.out.println("Image " + i + " conversion successful. Output saved at: " + outputFile.getPath());
            }
        }
    }

    private static boolean isImageRelevant(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int totalPixels = width * height;

        int sum = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = image.getRGB(x, y) & 0xFF;
                sum += pixel;
            }
        }

        double meanIntensity = sum / (double) totalPixels;
        return meanIntensity > 75 && meanIntensity < 220;
    }

    private static BufferedImage adaptiveHistogramEqualization(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage equalizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Implement adaptive histogram equalization
        int[] histogram = new int[256];
        int[] lut = new int[256];
        int totalPixels = width * height;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = image.getRGB(x, y) & 0xFF;
                histogram[pixel]++;
            }
        }

        int sum = 0;
        for (int i = 0; i < 256; i++) {
            sum += histogram[i];
            lut[i] = (int) ((sum * 255.0) / totalPixels);
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = image.getRGB(x, y) & 0xFF;
                int equalizedPixel = lut[pixel];
                int rgb = (equalizedPixel << 16) | (equalizedPixel << 8) | equalizedPixel;
                equalizedImage.setRGB(x, y, rgb);
            }
        }

        return equalizedImage;
    }

    private static BufferedImage adaptiveThresholding(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage smoothedImage = applyMedianFilter(image, 3); // Applica filtro mediano con kernel 3x3

        // Procedi con il thresholding adattivo
        BufferedImage binaryImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);

        int[][] integralImage = new int[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = smoothedImage.getRGB(x, y) & 0xFF;
                integralImage[y][x] = pixel +
                        (x > 0 ? integralImage[y][x - 1] : 0) +
                        (y > 0 ? integralImage[y - 1][x] : 0) -
                        (x > 0 && y > 0 ? integralImage[y - 1][x - 1] : 0);
            }
        }

        int blockSize = 25; // Dimensione del blocco locale (più piccolo per dettagli migliori)
        int c = 8; // Costante per ridurre il rumore

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int x1 = Math.max(x - blockSize / 2, 0);
                int y1 = Math.max(y - blockSize / 2, 0);
                int x2 = Math.min(x + blockSize / 2, width - 1);
                int y2 = Math.min(y + blockSize / 2, height - 1);

                int area = (x2 - x1 + 1) * (y2 - y1 + 1);
                int sum = integralImage[y2][x2]
                        - (y1 > 0 ? integralImage[y1 - 1][x2] : 0)
                        - (x1 > 0 ? integralImage[y2][x1 - 1] : 0)
                        + (x1 > 0 && y1 > 0 ? integralImage[y1 - 1][x1 - 1] : 0);

                int localMean = sum / area;
                int pixel = smoothedImage.getRGB(x, y) & 0xFF;
                int binaryValue = pixel > (localMean - c) ? 0xFFFFFF : 0x000000;
                binaryImage.setRGB(x, y, binaryValue);
            }
        }

        return applyMorphologicalOpening(binaryImage); // Applica postprocessing morfologico
    }

    private static BufferedImage applyMedianFilter(BufferedImage image, int kernelSize) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage filteredImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int halfKernel = kernelSize / 2;
        for (int y = halfKernel; y < height - halfKernel; y++) {
            for (int x = halfKernel; x < width - halfKernel; x++) {
                int[] neighborhood = new int[kernelSize * kernelSize];
                int index = 0;

                for (int ky = -halfKernel; ky <= halfKernel; ky++) {
                    for (int kx = -halfKernel; kx <= halfKernel; kx++) {
                        int pixel = image.getRGB(x + kx, y + ky) & 0xFF;
                        neighborhood[index++] = pixel;
                    }
                }

                Arrays.sort(neighborhood);
                int median = neighborhood[neighborhood.length / 2];
                int rgb = (median << 16) | (median << 8) | median;
                filteredImage.setRGB(x, y, rgb);
            }
        }

        return filteredImage;
    }

    private static BufferedImage applyMorphologicalOpening(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage dilatedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);

        // Esegui dilatazione
        int[][] kernel = {
                { 0, 1, 0 },
                { 1, 1, 1 },
                { 0, 1, 0 }
        };
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                boolean whitePixel = false;
                for (int ky = -1; ky <= 1; ky++) {
                    for (int kx = -1; kx <= 1; kx++) {
                        if (kernel[ky + 1][kx + 1] == 1 && (image.getRGB(x + kx, y + ky) & 0xFF) == 0xFF) {
                            whitePixel = true;
                            break;
                        }
                    }
                }
                dilatedImage.setRGB(x, y, whitePixel ? 0xFFFFFF : 0x000000);
            }
        }

        // Esegui erosione
        BufferedImage erodedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                boolean blackPixel = true;
                for (int ky = -1; ky <= 1; ky++) {
                    for (int kx = -1; kx <= 1; kx++) {
                        if (kernel[ky + 1][kx + 1] == 1 && (dilatedImage.getRGB(x + kx, y + ky) & 0xFF) == 0x00) {
                            blackPixel = false;
                            break;
                        }
                    }
                }
                erodedImage.setRGB(x, y, blackPixel ? 0xFFFFFF : 0x000000);
            }
        }

        return erodedImage;
    }

}