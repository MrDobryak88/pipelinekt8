# Java Photo Editor - Быстрый старт

## Требования

- **Java 17** или выше
- **Maven 3.6+**

## Установка зависимостей

### Ubuntu/Debian
```bash
sudo apt-get update
sudo apt-get install openjdk-17-jdk maven
```

### macOS (Homebrew)
```bash
brew install openjdk@17 maven
```

### Windows
Скачайте и установите:
- [JDK 17](https://adoptium.net/)
- [Maven](https://maven.apache.org/download.cgi)

## Сборка проекта

```bash
cd photo-editor
mvn clean package
```

## Запуск приложения

### Через Maven
```bash
mvn javafx:run
```

### Через JAR файл
```bash
java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml,javafx.swing -jar target/photo-editor-1.0-SNAPSHOT.jar
```

**Примечание:** Для запуска JAR файла требуется указать путь к библиотекам JavaFX. 
Путь зависит от вашей операционной системы:

- **Linux**: `/usr/share/openjfx/lib`
- **macOS**: `/Library/Java/Extensions`
- **Windows**: `C:\Program Files\Java\javafx-sdk-21\lib`

## Запуск тестов

```bash
mvn test
```

## Структура проекта

```
photo-editor/
├── src/
│   ├── main/
│   │   ├── java/com/editor/
│   │   │   ├── model/          # Модели данных
│   │   │   ├── service/        # Бизнес-логика
│   │   │   ├── viewmodel/      # ViewModel для MVVM
│   │   │   ├── view/           # UI компоненты
│   │   │   └── PhotoEditorApp.java
│   │   └── resources/styles/
│   │       └── dark-theme.css
│   └── test/java/com/editor/
│       ├── model/
│       └── service/
├── docs/
│   ├── README.md              # Основная документация
│   └── ARCHITECTURE.md        # Архитектурное обоснование
├── pom.xml
└── target/                    # Скомпилированные файлы
```

## Основные возможности

1. **Открытие изображений** - Файл → Открыть (Ctrl+O)
2. **Сохранение** - Файл → Сохранить (Ctrl+S)
3. **Фильтры**:
   - Яркость (регулировка ползунком)
   - Контраст (регулировка ползунком)
   - Оттенки серого
   - Инверсия цветов
4. **Трансформации**:
   - Поворот на 90°
   - Отражение горизонтально
   - Отражение вертикально
5. **Сброс** - возврат к оригинальному изображению

## Поддерживаемые форматы

- JPEG/JPG
- PNG
- GIF
- BMP
- WebP

## Расширение функциональности

### Добавление нового фильтра

1. Откройте `src/main/java/com/editor/service/FilterService.java`
2. Добавьте новый метод:
```java
public Image applySepia(Image image) {
    if (image == null) return null;
    
    int width = (int) image.getWidth();
    int height = (int) image.getHeight();
    WritableImage result = new WritableImage(width, height);
    
    PixelReader reader = image.getPixelReader();
    PixelWriter writer = result.getPixelWriter();
    
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            Color color = reader.getColor(x, y);
            double r = color.getRed();
            double g = color.getGreen();
            double b = color.getBlue();
            
            double tr = Math.min(1.0, (r * .393) + (g * .769) + (b * .189));
            double tg = Math.min(1.0, (r * .349) + (g * .686) + (b * .168));
            double tb = Math.min(1.0, (r * .272) + (g * .534) + (b * .131));
            
            writer.setColor(x, y, new Color(tr, tg, tb, color.getOpacity()));
        }
    }
    
    return result;
}
```

3. Откройте `src/main/java/com/editor/viewmodel/EditorViewModel.java`
4. Добавьте метод:
```java
public void applySepia() {
    Image current = imageModel.getCurrentImage();
    if (current != null) {
        Image result = filterService.applySepia(current);
        imageModel.setCurrentImage(result);
    }
}
```

5. Откройте `src/main/java/com/editor/view/MainView.java`
6. Добавьте кнопку в панель инструментов (в методе `createToolsPanel()`):
```java
Button sepiaBtn = new Button("Сепия");
sepiaBtn.setOnAction(e -> viewModel.applySepia());
toolsPanel.getChildren().addAll(new Separator(), sepiaBtn);
```

## Устранение проблем

### Ошибка "Location is required"
Убедитесь, что файл `dark-theme.css` существует в `src/main/resources/styles/`

### Ошибка "Glass Error: No toolkit found"
Установите JavaFX:
```bash
# Ubuntu/Debian
sudo apt-get install openjfx

# Или добавьте зависимости в pom.xml
```

### Тесты падают с "IllegalStateException"
Некоторые тесты требуют JavaFX Application Thread. Используйте:
```bash
mvn test -Dtest=FilterServiceTest,ImageModelTest
```

## Дополнительная документация

- [README.md](docs/README.md) - Полная документация
- [ARCHITECTURE.md](docs/ARCHITECTURE.md) - Архитектурное обоснование

## Лицензия

Этот проект создан в образовательных целях.
