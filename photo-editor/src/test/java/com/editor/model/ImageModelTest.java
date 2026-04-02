package com.editor.model;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для модели изображения.
 */
class ImageModelTest {

    private ImageModel imageModel;
    private Image testImage;

    @BeforeEach
    void setUp() {
        imageModel = new ImageModel();
        // Создаем тестовое изображение
        WritableImage writableImage = new WritableImage(100, 100);
        for (int y = 0; y < 100; y++) {
            for (int x = 0; x < 100; x++) {
                writableImage.getPixelWriter().setColor(x, y, javafx.scene.paint.Color.BLUE);
            }
        }
        testImage = writableImage;
    }

    @Test
    void testConstructor_InitializesWithDefaultValues() {
        assertNull(imageModel.getOriginalImage());
        assertNull(imageModel.getCurrentImage());
        assertEquals("", imageModel.getFilePath());
        assertFalse(imageModel.isModified());
    }

    @Test
    void testSetOriginalImage_SetsBothImages() {
        imageModel.setOriginalImage(testImage);
        
        assertNotNull(imageModel.getOriginalImage());
        assertNotNull(imageModel.getCurrentImage());
        assertEquals(imageModel.getOriginalImage(), imageModel.getCurrentImage());
        assertFalse(imageModel.isModified());
    }

    @Test
    void testSetCurrentImage_MarksAsModified() {
        imageModel.setOriginalImage(testImage);
        imageModel.setCurrentImage(testImage);
        
        assertTrue(imageModel.isModified());
    }

    @Test
    void testSetFilePath_UpdatesPath() {
        String testPath = "/path/to/image.jpg";
        imageModel.setFilePath(testPath);
        
        assertEquals(testPath, imageModel.getFilePath());
    }

    @Test
    void testResetToOriginal_ResetsToOriginalImage() {
        imageModel.setOriginalImage(testImage);
        
        // Создаем другое изображение как текущее
        WritableImage differentImage = new WritableImage(50, 50);
        imageModel.setCurrentImage(differentImage);
        
        assertTrue(imageModel.isModified());
        
        imageModel.resetToOriginal();
        
        assertEquals(imageModel.getOriginalImage(), imageModel.getCurrentImage());
        assertFalse(imageModel.isModified());
    }

    @Test
    void testClear_ResetsAllProperties() {
        imageModel.setOriginalImage(testImage);
        imageModel.setFilePath("/test/path.jpg");
        
        imageModel.clear();
        
        assertNull(imageModel.getOriginalImage());
        assertNull(imageModel.getCurrentImage());
        assertEquals("", imageModel.getFilePath());
        assertFalse(imageModel.isModified());
    }

    @Test
    void testPropertyBindings_FireChangeEvents() {
        boolean[] originalChanged = {false};
        boolean[] currentChanged = {false};
        boolean[] pathChanged = {false};
        
        imageModel.originalImageProperty().addListener((obs, oldVal, newVal) -> originalChanged[0] = true);
        imageModel.currentImageProperty().addListener((obs, oldVal, newVal) -> currentChanged[0] = true);
        imageModel.filePathProperty().addListener((obs, oldVal, newVal) -> pathChanged[0] = true);
        
        imageModel.setOriginalImage(testImage);
        imageModel.setFilePath("/test.jpg");
        
        assertTrue(originalChanged[0]);
        assertTrue(currentChanged[0]);
        assertTrue(pathChanged[0]);
    }
}
