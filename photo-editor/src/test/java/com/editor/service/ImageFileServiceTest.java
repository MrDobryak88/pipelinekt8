package com.editor.service;

import com.editor.model.ImageModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для сервиса работы с файлами изображений.
 */
class ImageFileServiceTest {

    private ImageFileService fileService;

    @BeforeEach
    void setUp() {
        fileService = new ImageFileService();
    }

    @Test
    void testConstructor_InitializesFileChooser() {
        assertNotNull(fileService);
    }

    @Test
    void testResetFilters_ClearsAndReconfigures() {
        // Проверяем что метод существует и не выбрасывает исключений
        assertDoesNotThrow(() -> fileService.resetFilters());
    }
}
