# Архитектурное обоснование Java Photo Editor

## Выбор архитектуры MVVM (Model-View-ViewModel)

### Почему MVVM, а не MVC или MVP?

**1. Реактивное программирование через JavaFX Properties**
```java
// Model использует Observable Properties
private final ObjectProperty<Image> currentImage = new SimpleObjectProperty<>();

// View автоматически обновляется при изменении данных
viewModel.getImageModel().currentImageProperty().addListener((obs, oldVal, newVal) -> {
    imageView.setImage(newVal);
});
```

JavaFX имеет встроенную поддержку реактивности через Properties, что идеально сочетается с MVVM. В MVC пришлось бы вручную обновлять View.

**2. Лучшая тестируемость**
- ViewModel не зависит от UI компонентов JavaFX
- Можно тестировать бизнес-логику без запуска JavaFX Application Thread
- Model полностью изолирована и тестируется отдельно

**3. Разделение ответственности**
- **Model**: Только данные и их состояние (ImageModel хранит изображение, путь, флаг изменений)
- **View**: Только отображение и пользовательский ввод (MainView содержит UI компоненты)
- **ViewModel**: Бизнес-логика представления (EditorViewModel управляет применением фильтров)

### Сравнение с альтернативами

| Критерий | MVVM (выбрано) | MVC | MVP |
|----------|---------------|-----|-----|
| Связь View-Model | Слабая через binding | Средняя | Сильная через интерфейс |
| Тестируемость | Отличная | Хорошая | Отличная |
| Поддержка JavaFX | Нативная | Требует адаптеров | Требует адаптеров |
| Количество кода | Минимально | Средне | Максимально |

## Выбор JavaFX вместо Swing

### Технические преимущества

**1. Современный API и производительность**
```java
// JavaFX - аппаратное ускорение через Prism
ImageView imageView = new ImageView(image);
imageView.setFitWidth(800);
imageView.setPreserveRatio(true);

// vs Swing - программная растеризация
JLabel label = new JLabel(new ImageIcon(image));
```

**2. CSS стилизация**
```css
/* dark-theme.css */
.button {
    -fx-background-color: #4a90d9;
    -fx-text-fill: white;
}
.button:hover {
    -fx-background-color: #5a9fe9;
}
```

В Swing потребовалось бы создавать UIManager для каждой платформы.

**3. Property Binding**
```java
// Автоматическая синхронизация данных
brightnessSlider.valueProperty().bindBidirectional(viewModel.brightnessProperty());
```

В Swing пришлось бы вручную добавлять ActionListener'ы.

### Почему не другие фреймворки?

**Apache Pivot** - мертвый проект, нет поддержки
**SWT** - привязка к конкретной OS, сложная разработка
**Compose Multiplatform** - требует Kotlin, избыточен для desktop-only приложения

## Сервис-ориентированная архитектура

### FilterService

**Почему выделен в отдельный класс:**

1. **Принцип единственной ответственности (SRP)**
   - Только применение фильтров к изображениям
   - Не знает о UI или файловой системе

2. **Переиспользуемость**
   ```java
   // Можно использовать в любом месте
   filterService.applyGrayscale(image);
   filterService.applyBrightness(image, 1.5);
   ```

3. **Тестируемость**
   ```java
   @Test
   void testApplyGrayscale_ConvertsToGray() {
       Image result = filterService.applyGrayscale(testImage);
       // Проверяем результат без UI
   }
   ```

### ImageFileService

**Инкапсуляция работы с файлами:**
- Диалоги выбора файлов
- Конвертация форматов
- Обработка ошибок IO

## Структура пакетов

```
com.editor/
├── model/           # Доменная модель
│   └── ImageModel   # Состояние изображения
├── service/         # Бизнес-логика (без зависимостей на UI)
│   ├── FilterService      # Применение фильтров
│   └── ImageFileService   # Работа с файлами
├── viewmodel/       # Логика представления
│   └── EditorViewModel  # Координация между Model и View
├── view/            # UI компоненты
│   └── MainView     # Главное окно
└── PhotoEditorApp   # Точка входа, DI контейнер
```

### Почему такая структура?

1. **По слоям (Layered Architecture)** - четкое разделение ответственности
2. **Легко масштабировать** - можно добавлять новые сервисы без изменения существующих
3. **Понятно новичкам** - стандартная структура для Java приложений

## Dependency Injection

### Почему ручной DI, а не фреймворк (Spring, Guice)?

```java
// В PhotoEditorApp.java
imageModel = new ImageModel();
filterService = new FilterService();
fileService = new ImageFileService();
viewModel = new EditorViewModel(imageModel, filterService);
mainView = new MainView(viewModel, fileService);
```

**Причины:**

1. **Мало зависимостей** - всего 5 классов, Spring избыточен
2. **Прозрачность** - явно видно какие зависимости у каждого класса
3. **Нет runtime магии** - все ошибки видны при компиляции
4. **Быстрый старт** - не нужно настраивать контекст

## Обработка изображений

### Почему PixelReader/PixelWriter, а не BufferedImage?

```java
// JavaFX нативный подход
PixelReader reader = image.getPixelReader();
PixelWriter writer = result.getPixelWriter();
for (int y = 0; y < height; y++) {
    for (int x = 0; x < width; x++) {
        Color color = reader.getColor(x, y);
        // обработка
        writer.setColor(x, y, newColor);
    }
}
```

**Преимущества:**

1. **Нет конвертации** - работаем напрямую с JavaFX Image
2. **Производительность** - минимальные накладные расходы
3. **Типобезопасность** - Color класс предоставляет типизированные каналы

### Альтернативы и почему они не выбраны

**OpenCV через Java bindings:**
- Плюсы: Мощные алгоритмы, оптимизировано
- Минусы: Native библиотеки, сложная сборка, избыточно для базовых фильтров

**ImgScalr:**
- Плюсы: Простой API для ресайза
- Минусы: Только трансформации, нет фильтров цвета

**Marvin Framework:**
- Плюсы: Много готовых плагинов
- Минусы: Мертвый проект, нет поддержки Java 17+

## Тестирование

### Стратегия тестирования

**Unit тесты (JUnit 5):**
- Model - проверка состояния и properties
- Service - проверка бизнес-логики
- ViewModel - проверка координации

**Интеграционные тесты (TestFX):**
- Проверка UI взаимодействий
- E2E сценарии

### Почему Mockito только частично?

```java
// Тестируем только то, что можно замокать
// ImageFileService не мокаем полностью т.к. FileChooser требует FX thread
@Test
void testResetFilters_ClearsAndReconfigures() {
    assertDoesNotThrow(() -> fileService.resetFilters());
}
```

FileChooser JavaFX нельзя замокать без TestFX, поэтому некоторые тесты упрощены.

## Расширяемость

### Добавление нового фильтра

1. Добавить метод в FilterService:
```java
public Image applySepia(Image image) {
    // реализация
}
```

2. Добавить команду в ViewModel:
```java
public void applySepia() {
    Image result = filterService.applySepia(imageModel.getCurrentImage());
    imageModel.setCurrentImage(result);
}
```

3. Добавить кнопку в View:
```java
Button sepiaBtn = new Button("Сепия");
sepiaBtn.setOnAction(e -> viewModel.applySepia());
```

**Никаких изменений в других классах!** - соблюдение Open/Closed Principle.

## Заключение

Выбранная архитектура обеспечивает:
- ✅ Четкое разделение ответственности
- ✅ Высокую тестируемость
- ✅ Легкость расширения
- ✅ Поддержку современного Java (17+)
- ✅ Хорошую производительность
- ✅ Понятный код для команды

Это баланс между избыточной инженерией (микросервисы для desktop приложения) и недостаточной (весь код в одном классе).
