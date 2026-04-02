package com.editor.service;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * Сервис для применения фильтров к изображениям.
 * Выделен в отдельный класс для соблюдения принципа единственной ответственности (SRP).
 */
public class FilterService {

    /**
     * Применяет фильтр яркости к изображению
     * @param image исходное изображение
     * @param factor коэффициент яркости (0.0 - 2.0, где 1.0 - оригинал)
     * @return новое изображение с примененным фильтром
     */
    public Image applyBrightness(Image image, double factor) {
        if (image == null) return null;
        
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        WritableImage result = new WritableImage(width, height);
        
        PixelReader reader = image.getPixelReader();
        PixelWriter writer = result.getPixelWriter();
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y);
                double r = Math.min(1.0, color.getRed() * factor);
                double g = Math.min(1.0, color.getGreen() * factor);
                double b = Math.min(1.0, color.getBlue() * factor);
                writer.setColor(x, y, new Color(r, g, b, color.getOpacity()));
            }
        }
        
        return result;
    }

    /**
     * Применяет фильтр контраста к изображению
     * @param image исходное изображение
     * @param factor коэффициент контраста (0.0 - 3.0, где 1.0 - оригинал)
     * @return новое изображение с примененным фильтром
     */
    public Image applyContrast(Image image, double factor) {
        if (image == null) return null;
        
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        WritableImage result = new WritableImage(width, height);
        
        PixelReader reader = image.getPixelReader();
        PixelWriter writer = result.getPixelWriter();
        
        double factorCorrection = (259 * (factor + 255)) / (255 * (259 - factor));
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y);
                double r = Math.min(1.0, Math.max(0.0, factorCorrection * (color.getRed() - 0.5) + 0.5));
                double g = Math.min(1.0, Math.max(0.0, factorCorrection * (color.getGreen() - 0.5) + 0.5));
                double b = Math.min(1.0, Math.max(0.0, factorCorrection * (color.getBlue() - 0.5) + 0.5));
                writer.setColor(x, y, new Color(r, g, b, color.getOpacity()));
            }
        }
        
        return result;
    }

    /**
     * Применяет фильтр оттенков серого к изображению
     * @param image исходное изображение
     * @return новое изображение в оттенках серого
     */
    public Image applyGrayscale(Image image) {
        if (image == null) return null;
        
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        WritableImage result = new WritableImage(width, height);
        
        PixelReader reader = image.getPixelReader();
        PixelWriter writer = result.getPixelWriter();
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y);
                double gray = 0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue();
                writer.setColor(x, y, new Color(gray, gray, gray, color.getOpacity()));
            }
        }
        
        return result;
    }

    /**
     * Инвертирует цвета изображения
     * @param image исходное изображение
     * @return новое изображение с инвертированными цветами
     */
    public Image applyInvert(Image image) {
        if (image == null) return null;
        
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        WritableImage result = new WritableImage(width, height);
        
        PixelReader reader = image.getPixelReader();
        PixelWriter writer = result.getPixelWriter();
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y);
                writer.setColor(x, y, new Color(
                    1.0 - color.getRed(),
                    1.0 - color.getGreen(),
                    1.0 - color.getBlue(),
                    color.getOpacity()
                ));
            }
        }
        
        return result;
    }

    /**
     * Поворачивает изображение на 90 градусов по часовой стрелке
     * @param image исходное изображение
     * @return повернутое изображение
     */
    public Image rotate90(Image image) {
        if (image == null) return null;
        
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        WritableImage result = new WritableImage(height, width);
        
        PixelReader reader = image.getPixelReader();
        PixelWriter writer = result.getPixelWriter();
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y);
                writer.setColor(height - 1 - y, x, color);
            }
        }
        
        return result;
    }

    /**
     * Отзеркаливает изображение горизонтально
     * @param image исходное изображение
     * @return отзеркаленное изображение
     */
    public Image flipHorizontal(Image image) {
        if (image == null) return null;
        
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        WritableImage result = new WritableImage(width, height);
        
        PixelReader reader = image.getPixelReader();
        PixelWriter writer = result.getPixelWriter();
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y);
                writer.setColor(width - 1 - x, y, color);
            }
        }
        
        return result;
    }

    /**
     * Отзеркаливает изображение вертикально
     * @param image исходное изображение
     * @return отзеркаленное изображение
     */
    public Image flipVertical(Image image) {
        if (image == null) return null;
        
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        WritableImage result = new WritableImage(width, height);
        
        PixelReader reader = image.getPixelReader();
        PixelWriter writer = result.getPixelWriter();
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y);
                writer.setColor(x, height - 1 - y, color);
            }
        }
        
        return result;
    }
}
