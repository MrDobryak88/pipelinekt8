package com.editor.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;

/**
 * Модель изображения, хранящая оригинальное и текущее состояние.
 * Использует JavaFX Properties для реактивного обновления UI.
 */
public class ImageModel {
    private final ObjectProperty<Image> originalImage;
    private final ObjectProperty<Image> currentImage;
    private final ObjectProperty<String> filePath;
    private boolean isModified;

    public ImageModel() {
        this.originalImage = new SimpleObjectProperty<>();
        this.currentImage = new SimpleObjectProperty<>();
        this.filePath = new SimpleObjectProperty<>("");
        this.isModified = false;
    }

    public Image getOriginalImage() {
        return originalImage.get();
    }

    public ObjectProperty<Image> originalImageProperty() {
        return originalImage;
    }

    public void setOriginalImage(Image image) {
        this.originalImage.set(image);
        this.currentImage.set(image);
        this.isModified = false;
    }

    public Image getCurrentImage() {
        return currentImage.get();
    }

    public ObjectProperty<Image> currentImageProperty() {
        return currentImage;
    }

    public void setCurrentImage(Image image) {
        this.currentImage.set(image);
        this.isModified = true;
    }

    public String getFilePath() {
        return filePath.get();
    }

    public ObjectProperty<String> filePathProperty() {
        return filePath;
    }

    public void setFilePath(String path) {
        this.filePath.set(path);
    }

    public boolean isModified() {
        return isModified;
    }

    public void resetToOriginal() {
        if (originalImage.get() != null) {
            currentImage.set(originalImage.get());
            isModified = false;
        }
    }

    /**
     * Сбрасывает все состояния модели
     */
    public void clear() {
        originalImage.set(null);
        currentImage.set(null);
        filePath.set("");
        isModified = false;
    }
}
