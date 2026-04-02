package com.editor.view;

import com.editor.service.ImageFileService;
import com.editor.viewmodel.EditorViewModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * Основной вид приложения с панелями инструментов.
 * Использует композицию из различных панелей для создания интуитивного интерфейса.
 */
public class MainView extends BorderPane {

    private final EditorViewModel viewModel;
    private final ImageFileService fileService;
    private final ImageView imageView;
    private final Label statusLabel;
    private final Slider brightnessSlider;
    private final Slider contrastSlider;

    public MainView(EditorViewModel viewModel, ImageFileService fileService) {
        this.viewModel = viewModel;
        this.fileService = fileService;
        this.imageView = new ImageView();
        this.statusLabel = new Label("Нет изображения");
        this.brightnessSlider = new Slider(0.0, 2.0, 1.0);
        this.contrastSlider = new Slider(0.0, 3.0, 1.0);

        initializeUI();
        setupBindings();
    }

    private void initializeUI() {
        // Верхняя панель с меню
        setTop(createMenuBar());

        // Центральная область с изображением
        StackPane imageContainer = new StackPane(imageView);
        imageContainer.setStyle("-fx-background-color: #2b2b2b;");
        imageContainer.setPadding(new Insets(10));
        setCenter(imageContainer);

        // Правая панель с инструментами
        setRight(createToolsPanel());

        // Нижняя панель со статусом
        statusLabel.setStyle("-fx-text-fill: white; -fx-padding: 5px;");
        statusLabel.setAlignment(Pos.CENTER);
        setBottom(statusLabel);
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        // Меню Файл
        Menu fileMenu = new Menu("Файл");
        MenuItem openItem = new MenuItem("Открыть");
        openItem.setOnAction(e -> {
            Stage stage = (Stage) menuBar.getScene().getWindow();
            if (fileService.loadImage(stage, viewModel.getImageModel())) {
                System.out.println("Изображение загружено: " + viewModel.getImageModel().getFilePath());
            }
        });
        
        MenuItem saveItem = new MenuItem("Сохранить");
        saveItem.setOnAction(e -> {
            Stage stage = (Stage) menuBar.getScene().getWindow();
            if (fileService.saveImage(stage, viewModel.getImageModel())) {
                System.out.println("Изображение сохранено: " + viewModel.getImageModel().getFilePath());
            }
        });
        
        MenuItem exitItem = new MenuItem("Выход");
        exitItem.setOnAction(e -> System.exit(0));

        fileMenu.getItems().addAll(openItem, saveItem, new SeparatorMenuItem(), exitItem);

        // Меню Правка
        Menu editMenu = new Menu("Правка");
        MenuItem resetItem = new MenuItem("Сбросить");
        resetItem.setOnAction(e -> viewModel.resetToOriginal());
        editMenu.getItems().add(resetItem);

        // Меню Поворот
        Menu rotateMenu = new Menu("Поворот");
        MenuItem rotate90Item = new MenuItem("Повернуть на 90°");
        rotate90Item.setOnAction(e -> viewModel.rotate90());
        
        MenuItem flipHItem = new MenuItem("Отразить горизонтально");
        flipHItem.setOnAction(e -> viewModel.flipHorizontal());
        
        MenuItem flipVItem = new MenuItem("Отразить вертикально");
        flipVItem.setOnAction(e -> viewModel.flipVertical());

        rotateMenu.getItems().addAll(rotate90Item, flipHItem, flipVItem);

        menuBar.getMenus().addAll(fileMenu, editMenu, rotateMenu);
        return menuBar;
    }

    private VBox createToolsPanel() {
        VBox toolsPanel = new VBox(15);
        toolsPanel.setPadding(new Insets(15));
        toolsPanel.setMinWidth(250);
        toolsPanel.setStyle("-fx-background-color: #3c3f41;");

        // Яркость
        Label brightnessLabel = new Label("Яркость: " + String.format("%.1f", brightnessSlider.getValue()));
        brightnessLabel.setStyle("-fx-text-fill: white;");
        brightnessSlider.setShowTickLabels(true);
        brightnessSlider.setShowTickMarks(true);
        brightnessSlider.setMajorTickUnit(0.5);
        brightnessSlider.setBlockIncrement(0.1);
        
        brightnessSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            brightnessLabel.setText("Яркость: " + String.format("%.1f", newVal.doubleValue()));
        });

        Button applyBrightnessBtn = new Button("Применить яркость");
        applyBrightnessBtn.setOnAction(e -> viewModel.applyBrightness(brightnessSlider.getValue()));

        // Контраст
        Label contrastLabel = new Label("Контраст: " + String.format("%.1f", contrastSlider.getValue()));
        contrastLabel.setStyle("-fx-text-fill: white;");
        contrastSlider.setShowTickLabels(true);
        contrastSlider.setShowTickMarks(true);
        contrastSlider.setMajorTickUnit(0.5);
        contrastSlider.setBlockIncrement(0.1);
        
        contrastSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            contrastLabel.setText("Контраст: " + String.format("%.1f", newVal.doubleValue()));
        });

        Button applyContrastBtn = new Button("Применить контраст");
        applyContrastBtn.setOnAction(e -> viewModel.applyContrast(contrastSlider.getValue()));

        // Фильтры
        Label filtersLabel = new Label("Фильтры");
        filtersLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        Button grayscaleBtn = new Button("Оттенки серого");
        grayscaleBtn.setOnAction(e -> viewModel.applyGrayscale());

        Button invertBtn = new Button("Инвертировать");
        invertBtn.setOnAction(e -> viewModel.applyInvert());

        // Сброс
        Button resetBtn = new Button("Сбросить всё");
        resetBtn.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white;");
        resetBtn.setOnAction(e -> viewModel.resetToOriginal());

        toolsPanel.getChildren().addAll(
            brightnessLabel, brightnessSlider, applyBrightnessBtn,
            new Separator(),
            contrastLabel, contrastSlider, applyContrastBtn,
            new Separator(),
            filtersLabel, grayscaleBtn, invertBtn,
            new Separator(),
            resetBtn
        );

        return toolsPanel;
    }

    private void setupBindings() {
        // Привязка изображения к модели
        viewModel.getImageModel().currentImageProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                imageView.setImage(newVal);
                imageView.setFitWidth(800);
                imageView.setFitHeight(600);
                imageView.setPreserveRatio(true);
                statusLabel.setText(viewModel.getImageModel().getFilePath());
            } else {
                imageView.setImage(null);
                statusLabel.setText("Нет изображения");
            }
        });

        // Привязка пути к файлу
        viewModel.getImageModel().filePathProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty()) {
                statusLabel.setText(newVal);
            }
        });
    }

    public ImageView getImageView() {
        return imageView;
    }

    public EditorViewModel getViewModel() {
        return viewModel;
    }
}
