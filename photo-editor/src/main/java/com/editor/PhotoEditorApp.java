package com.editor;

import com.editor.model.ImageModel;
import com.editor.service.FilterService;
import com.editor.service.ImageFileService;
import com.editor.view.MainView;
import com.editor.viewmodel.EditorViewModel;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

/**
 * Главное приложение фоторедактора.
 * Точка входа в приложение, инициализирует все компоненты.
 */
public class PhotoEditorApp extends Application {

    private ImageModel imageModel;
    private FilterService filterService;
    private ImageFileService fileService;
    private EditorViewModel viewModel;
    private MainView mainView;

    @Override
    public void start(Stage primaryStage) {
        // Инициализация компонентов
        initializeComponents();
        
        // Создание главного окна
        setupPrimaryStage(primaryStage);
        
        // Настройка обработчиков событий
        setupEventHandlers(primaryStage);
        
        primaryStage.show();
    }

    private void initializeComponents() {
        // Создаем модель
        imageModel = new ImageModel();
        
        // Создаем сервисы
        filterService = new FilterService();
        fileService = new ImageFileService();
        
        // Создаем ViewModel
        viewModel = new EditorViewModel(imageModel, filterService);
        
        // Создаем главный вид
        mainView = new MainView(viewModel, fileService);
    }

    private void setupPrimaryStage(Stage stage) {
        Scene scene = new Scene(mainView, 1200, 800);
        
        // Применяем темную тему (если файл существует)
        if (getClass().getResource("/styles/dark-theme.css") != null) {
            scene.getStylesheets().add(getClass().getResource("/styles/dark-theme.css").toExternalForm());
        }
        
        stage.setTitle("Java Photo Editor");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
    }

    private void setupEventHandlers(Stage stage) {
        // Обработка закрытия с проверкой несохраненных изменений
        stage.setOnCloseRequest(event -> {
            if (imageModel.isModified()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Подтверждение");
                alert.setHeaderText("Есть несохраненные изменения");
                alert.setContentText("Вы хотите сохранить изменения перед выходом?");
                
                alert.getButtonTypes().clear();
                alert.getButtonTypes().addAll(
                    ButtonType.YES,
                    ButtonType.NO,
                    ButtonType.CANCEL
                );
                
                var result = alert.showAndWait();
                if (result.isPresent()) {
                    if (result.get() == ButtonType.YES) {
                        if (!fileService.saveImage(stage, imageModel)) {
                            event.consume();
                        }
                    } else if (result.get() == ButtonType.CANCEL) {
                        event.consume();
                    }
                }
            }
        });
    }

    /**
     * Открывает изображение через диалог выбора файла
     */
    public void openImage() {
        Stage stage = (Stage) mainView.getScene().getWindow();
        if (fileService.loadImage(stage, imageModel)) {
            System.out.println("Изображение загружено: " + imageModel.getFilePath());
        }
    }

    /**
     * Сохраняет текущее изображение
     */
    public void saveImage() {
        Stage stage = (Stage) mainView.getScene().getWindow();
        if (fileService.saveImage(stage, imageModel)) {
            System.out.println("Изображение сохранено: " + imageModel.getFilePath());
        }
    }

    /**
     * Возвращает главную сцену для тестирования
     */
    public Scene getMainScene() {
        return mainView.getScene();
    }

    /**
     * Возвращает ViewModel для тестирования
     */
    public EditorViewModel getViewModel() {
        return viewModel;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
