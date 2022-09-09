package me.petrolingus.unn.psr;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ParticleSystemResearchApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(ParticleSystemResearchApplication.class.getResource("view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setTitle("Investigation of the simple transport properties of a particle system");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.showingProperty().addListener((observable, oldValue, newValue) -> System.exit(0));
    }
}