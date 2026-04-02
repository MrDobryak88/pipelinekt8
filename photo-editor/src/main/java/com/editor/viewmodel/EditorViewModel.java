package com.editor.viewmodel;

import com.editor.model.ImageModel;
import com.editor.service.FilterService;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.image.Image;

/**
 * ViewModel для управления состоянием редактора.
 * Связывает модель с представлением, предоставляя удобные методы для UI.
 * Использует Observer pattern через JavaFX Properties.
 */
public class EditorViewModel {
    
    private final ImageModel imageModel;
    private final FilterService filterService;
    
    private final DoubleProperty brightnessProperty;
    private final DoubleProperty contrastProperty;

    public EditorViewModel(ImageModel imageModel, FilterService filterService) {
        this.imageModel = imageModel;
        this.filterService = filterService;
        
        this.brightnessProperty = new SimpleDoubleProperty(1.0);
        this.contrastProperty = new SimpleDoubleProperty(1.0);
        
        // Сбрасываем значения при загрузке нового изображения
        imageModel.originalImageProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                resetAdjustments();
            }
        });
    }

    /**
     * Применяет яркость к текущему изображению
     */
    public void applyBrightness(double factor) {
        Image current = imageModel.getCurrentImage();
        if (current != null) {
            Image result = filterService.applyBrightness(current, factor);
            imageModel.setCurrentImage(result);
        }
    }

    /**
     * Применяет контраст к текущему изображению
     */
    public void applyContrast(double factor) {
        Image current = imageModel.getCurrentImage();
        if (current != null) {
            Image result = filterService.applyContrast(current, factor);
            imageModel.setCurrentImage(result);
        }
    }

    /**
     * Применяет фильтр оттенков серого
     */
    public void applyGrayscale() {
        Image current = imageModel.getCurrentImage();
        if (current != null) {
            Image result = filterService.applyGrayscale(current);
            imageModel.setCurrentImage(result);
        }
    }

    /**
     * Инвертирует цвета
     */
    public void applyInvert() {
        Image current = imageModel.getCurrentImage();
        if (current != null) {
            Image result = filterService.applyInvert(current);
            imageModel.setCurrentImage(result);
        }
    }

    /**
     * Поворачивает изображение на 90 градусов
     */
    public void rotate90() {
        Image current = imageModel.getCurrentImage();
        if (current != null) {
            Image result = filterService.rotate90(current);
            imageModel.setCurrentImage(result);
        }
    }

    /**
     * Отзеркаливает горизонтально
     */
    public void flipHorizontal() {
        Image current = imageModel.getCurrentImage();
        if (current != null) {
            Image result = filterService.flipHorizontal(current);
            imageModel.setCurrentImage(result);
        }
    }

    /**
     * Отзеркаливает вертикально
     */
    public void flipVertical() {
        Image current = imageModel.getCurrentImage();
        if (current != null) {
            Image result = filterService.flipVertical(current);
            imageModel.setCurrentImage(result);
        }
    }

    /**
     * Сбрасывает изображение к оригиналу
     */
    public void resetToOriginal() {
        imageModel.resetToOriginal();
        resetAdjustments();
    }

    /**
     * Сбрасывает настройки яркости и контраста
     */
    public void resetAdjustments() {
        brightnessProperty.set(1.0);
        contrastProperty.set(1.0);
    }

    // Геттеры для свойств
    public ImageModel getImageModel() {
        return imageModel;
    }

    public DoubleProperty brightnessProperty() {
        return brightnessProperty;
    }

    public DoubleProperty contrastProperty() {
        return contrastProperty;
    }

    public double getBrightness() {
        return brightnessProperty.get();
    }

    public void setBrightness(double value) {
        brightnessProperty.set(value);
    }

    public double getContrast() {
        return contrastProperty.get();
    }

    public void setContrast(double value) {
        contrastProperty.set(value);
    }
}
