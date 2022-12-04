package com.english;

import com.english.scene.AbstractScene;
import com.english.service.BaseService;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.reflections.Reflections;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author XYC
 * EnglishApp项目创建时间 -- 2022年3月29日 (大二)
 * 4月3日 -- 数据层,业务层已实现基本地保存,查词,查文集等基本功能
 * 此期间学会使用JavaFx可视化编程
 * 4月8日 -- 将翻译功能实现图形界面化
 * 4月11日 -- 已实现并完善包括背词,挖词填空竞赛,单词慢览等的图形界面化
 */
@SpringBootApplication
public class EnglishAppStart extends Application {
    public static final Map<String, AbstractScene> SCENE_MAP;
    public static Stage primaryStage;

    static {
        Reflections reflections = new Reflections("com/english/scene");

        Set<Class<? extends AbstractScene>> classes = reflections.getSubTypesOf(AbstractScene.class);
        SCENE_MAP = new HashMap<>(classes.size());
        for (Class<? extends AbstractScene> aClass : classes) {
            try {
                if (Modifier.isAbstract(aClass.getModifiers())) {
                    continue;
                }
                Field sceneName = aClass.getField("sceneName");
                AbstractScene scene = aClass.newInstance();
                SCENE_MAP.put((String) sceneName.get(scene), scene);
            } catch (NoSuchFieldException | IllegalAccessException | InstantiationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void convertScene(String sceneName) {
        primaryStage.setScene(SCENE_MAP.get(sceneName).run());
    }

    public static void convertScene(String sceneName, Object... args) {
        primaryStage.setScene(SCENE_MAP.get(sceneName).run(args));
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        EnglishAppStart.primaryStage = primaryStage;
        EnglishAppStart.primaryStage.setAlwaysOnTop(true);
        EnglishAppStart.primaryStage.setX(1556);
        EnglishAppStart.primaryStage.setY(88);
        EnglishAppStart.primaryStage.setMaxWidth(388);
        EnglishAppStart.primaryStage.setMaxHeight(388);
        EnglishAppStart.primaryStage.setWidth(388);
        EnglishAppStart.primaryStage.setHeight(388);
        EnglishAppStart.primaryStage.setTitle("englishApp");
        EnglishAppStart.primaryStage.getIcons().add(new Image("img/小熊.png"));
        EnglishAppStart.primaryStage.show();

        convertScene("主场景");
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        BaseService.THREAD_POOL.shutdownNow();
    }
}
