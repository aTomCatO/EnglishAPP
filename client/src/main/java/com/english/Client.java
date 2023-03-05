package com.english;

import com.english.netty.NettyService;
import com.english.scene.AbstractScene;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
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
public class Client extends Application {
    /**
     * save scene instance
     */
    private static final Map<String, AbstractScene> SCENE_MAP;
    /**
     * save scene class
     */
    private static final Map<String, Class<? extends AbstractScene>> SCENE_CLASS_MAP;
    public static Stage primaryStage;

    static {
        Reflections reflections = new Reflections("com/english/scene");
        Set<Class<? extends AbstractScene>> classes = reflections.getSubTypesOf(AbstractScene.class);
        int sceneSize = classes.size();
        SCENE_CLASS_MAP = new HashMap<>(sceneSize);
        SCENE_MAP = new HashMap<>();
        for (Class<? extends AbstractScene> aClass : classes) {
            if (Modifier.isAbstract(aClass.getModifiers())) {
                continue;
            }
            String sceneName = aClass.getSimpleName();
            SCENE_CLASS_MAP.put(sceneName, aClass);
            SCENE_MAP.put(sceneName, null);
        }
    }

    public static void convertScene(String sceneName, Object... args) {
        AbstractScene scene;
        if ((scene = SCENE_MAP.get(sceneName)) == null) {
            Class<? extends AbstractScene> aClass = SCENE_CLASS_MAP.get(sceneName);
            try {
                scene = aClass.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                     InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        if (args == null) {
            primaryStage.setScene(scene.run());
        } else {
            primaryStage.setScene(scene.run(args));
        }
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
        convertScene("MainScene");
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        NettyService.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
