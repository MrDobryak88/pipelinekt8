package com.editor.service;

import com.editor.model.ImageModel;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Сервис для работы с файлами изображений (загрузка/сохранение).
 * Выделен отдельно для инкапсуляции логики работы с файловой системой.
 */
public class ImageFileService {

    private final FileChooser fileChooser;

    public ImageFileService() {
        this.fileChooser = new FileChooser();
        configureFileChooser();
    }

    private void configureFileChooser() {
        fileChooser.setTitle("Выберите изображение");
        
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter(
            "Изображения",
            "*.jpg", "*.jpeg", "*.png", "*.gif", "*.bmp", "*.webp"
        );
        fileChooser.getExtensionFilters().add(imageFilter);
        fileChooser.setSelectedExtensionFilter(imageFilter);
    }

    /**
     * Открывает диалог выбора файла и загружает изображение
     * @param stage окно приложения
     * @param model модель для обновления
     * @return true если изображение успешно загружено
     */
    public boolean loadImage(Stage stage, ImageModel model) {
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                Image image = new Image(file.toURI().toString());
                model.setOriginalImage(image);
                model.setFilePath(file.getAbsolutePath());
                return true;
            } catch (Exception e) {
                System.err.println("Ошибка загрузки изображения: " + e.getMessage());
                return false;
            }
        }
        return false;
    }

    /**
     * Сохраняет текущее изображение в файл
     * @param stage окно приложения
     * @param model модель с изображением
     * @return true если изображение успешно сохранено
     */
    public boolean saveImage(Stage stage, ImageModel model) {
        if (model.getCurrentImage() == null) {
            return false;
        }

        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                // Добавляем расширение если нет
                String path = file.getAbsolutePath();
                if (!path.toLowerCase().endsWith(".png") && 
                    !path.toLowerCase().endsWith(".jpg") &&
                    !path.toLowerCase().endsWith(".jpeg")) {
                    path += ".png";
                    file = new File(path);
                }

                BufferedImage bufferedImage = convertToBufferedImage(model.getCurrentImage());
                String format = path.toLowerCase().endsWith(".jpg") || path.toLowerCase().endsWith(".jpeg") 
                    ? "jpg" : "png";
                
                ImageIO.write(bufferedImage, format, file);
                model.setFilePath(file.getAbsolutePath());
                model.setOriginalImage(model.getCurrentImage());
                return true;
            } catch (IOException e) {
                System.err.println("Ошибка сохранения изображения: " + e.getMessage());
                return false;
            }
        }
        return false;
    }

    /**
     * Конвертирует JavaFX Image в BufferedImage для сохранения
     */
    private BufferedImage convertToBufferedImage(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        
        javafx.embed.swing.SwingFXUtils.fromFXImage(image, bufferedImage);
        
        return bufferedImage;
    }

    /**
     * Сбрасывает фильтр файлов для показа всех типов
     */
    public void resetFilters() {
        fileChooser.getExtensionFilters().clear();
        configureFileChooser();
    }
}
