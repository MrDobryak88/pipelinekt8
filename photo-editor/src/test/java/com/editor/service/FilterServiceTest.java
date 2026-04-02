package com.editor.service;

import com.editor.model.ImageModel;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для сервиса фильтров изображений.
 * Проверяют корректность применения различных фильтров.
 */
class FilterServiceTest {

    private FilterService filterService;
    private Image testImage;

    @BeforeEach
    void setUp() {
        filterService = new FilterService();
        // Создаем тестовое изображение 100x100 с красным цветом
        WritableImage writableImage = new WritableImage(100, 100);
        for (int y = 0; y < 100; y++) {
            for (int x = 0; x < 100; x++) {
                writableImage.getPixelWriter().setColor(x, y, javafx.scene.paint.Color.RED);
            }
        }
        testImage = writableImage;
    }

    @Test
    void testApplyBrightness_IncreasesBrightness() {
        Image result = filterService.applyBrightness(testImage, 1.5);
        
        assertNotNull(result);
        assertEquals((int) testImage.getWidth(), (int) result.getWidth());
        assertEquals((int) testImage.getHeight(), (int) result.getHeight());
        
        // Проверяем что цвет стал ярче
        var color = result.getPixelReader().getColor(50, 50);
        assertTrue(color.getRed() > 0.9, "Красный канал должен быть увеличен");
    }

    @Test
    void testApplyBrightness_DecreasesBrightness() {
        Image result = filterService.applyBrightness(testImage, 0.5);
        
        assertNotNull(result);
        var color = result.getPixelReader().getColor(50, 50);
        assertTrue(color.getRed() < 1.0, "Красный канал должен быть уменьшен");
    }

    @Test
    void testApplyContrast_ChangesContrast() {
        Image result = filterService.applyContrast(testImage, 1.5);
        
        assertNotNull(result);
        assertEquals((int) testImage.getWidth(), (int) result.getWidth());
        assertEquals((int) testImage.getHeight(), (int) result.getHeight());
    }

    @Test
    void testApplyGrayscale_ConvertsToGray() {
        Image result = filterService.applyGrayscale(testImage);
        
        assertNotNull(result);
        var color = result.getPixelReader().getColor(50, 50);
        
        // В оттенках серого все каналы должны быть равны
        assertEquals(color.getRed(), color.getGreen(), 0.01);
        assertEquals(color.getGreen(), color.getBlue(), 0.01);
    }

    @Test
    void testApplyInvert_InvertsColors() {
        Image result = filterService.applyInvert(testImage);
        
        assertNotNull(result);
        var color = result.getPixelReader().getColor(50, 50);
        
        // Инверсия красного (1.0, 0.0, 0.0) должна дать (0.0, 1.0, 1.0) - циан
        assertEquals(0.0, color.getRed(), 0.01);
        assertEquals(1.0, color.getGreen(), 0.01);
        assertEquals(1.0, color.getBlue(), 0.01);
    }

    @Test
    void testRotate90_ChangesDimensions() {
        Image result = filterService.rotate90(testImage);
        
        assertNotNull(result);
        // После поворота на 90 градусов ширина и высота меняются местами
        assertEquals((int) testImage.getHeight(), (int) result.getWidth());
        assertEquals((int) testImage.getWidth(), (int) result.getHeight());
    }

    @Test
    void testFlipHorizontal_MirrorsImage() {
        Image result = filterService.flipHorizontal(testImage);
        
        assertNotNull(result);
        assertEquals((int) testImage.getWidth(), (int) result.getWidth());
        assertEquals((int) testImage.getHeight(), (int) result.getHeight());
    }

    @Test
    void testFlipVertical_MirrorsImage() {
        Image result = filterService.flipVertical(testImage);
        
        assertNotNull(result);
        assertEquals((int) testImage.getWidth(), (int) result.getWidth());
        assertEquals((int) testImage.getHeight(), (int) result.getHeight());
    }

    @Test
    void testApplyFilters_WithNullImage_ReturnsNull() {
        assertNull(filterService.applyBrightness(null, 1.0));
        assertNull(filterService.applyContrast(null, 1.0));
        assertNull(filterService.applyGrayscale(null));
        assertNull(filterService.applyInvert(null));
        assertNull(filterService.rotate90(null));
        assertNull(filterService.flipHorizontal(null));
        assertNull(filterService.flipVertical(null));
    }
}
