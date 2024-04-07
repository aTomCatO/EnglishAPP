package com.english;

import com.english.scene.AbstractScene;
import com.english.service.BaseService;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author XYC
 * EnglishApp项目创建时间 -- 2022年3月29日 (大二)
 * 4月3日 -- 数据层,业务层已实现基本地保存,查词,查文集等基本功能
 * 此期间学会使用JavaFx可视化编程
 * 4月8日 -- 将翻译功能实现图形界面化
 * 4月11日 -- 已实现并完善包括背词,挖词填空竞赛,单词慢览等的图形界面化
 */
public class EnglishAppStart extends Application {
    private static final Map<String, AbstractScene<?>> SCENE_MAP = new HashMap<>();
    public static Stage primaryStage;

    public static void convertScene(String sceneName, Object... args) {
        AbstractScene<?> scene;
        if ((scene = SCENE_MAP.get(sceneName)) == null) {
            try {
                Class<? extends AbstractScene> aClass = Class.forName(sceneName).asSubclass(AbstractScene.class);
                scene = aClass.getConstructor().newInstance();
                scene.init();
                SCENE_MAP.put(sceneName, scene);
            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                     InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        if (args == null) {
            primaryStage.setScene(scene.run());
        } else {
            primaryStage.setScene(scene.run(args));
        }
    }
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setX(1556);
        primaryStage.setY(88);
        primaryStage.setMaxWidth(388);
        primaryStage.setMaxHeight(388);
        primaryStage.setWidth(388);
        primaryStage.setHeight(388);
        primaryStage.setTitle("englishApp");
        primaryStage.getIcons().add(new Image("img/小熊.png"));
        primaryStage.show();
        convertScene("com.english.scene.general.MainScene");
    }

    @Override
    public void stop() throws Exception {
        BaseService.THREAD_POOL.shutdownNow();
    }
}
