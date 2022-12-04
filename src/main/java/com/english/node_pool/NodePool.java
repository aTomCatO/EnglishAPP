package com.english.node_pool;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;

/**
 * @author XYC
 */
public class NodePool {
    public static final ObjectPool<TextField> TEXT_FIELD_POOL;

    public static final ObjectPool<Label> LABEL_POOL;


    static {
        TEXT_FIELD_POOL = new GenericObjectPool<>(new TextFieldPoolFactory());
        LABEL_POOL = new GenericObjectPool<>(new LabelPoolFactory());
    }

    static class TextFieldPoolFactory implements PooledObjectFactory<TextField> {
        /**
         * 这个方法是用来创建一个对象。
         * 当在GenericObjectPool类中调用borrowObject方法时，如果当前对象池中没有空闲的对象，
         * GenericObjectPool会调用这个方法，创建一个对象，并把这个对象封装到PooledObject类中，并交给对象池管理。
         */
        @Override
        public PooledObject<TextField> makeObject() throws Exception {
            TextField textField = new TextField();
            textField.setAlignment(Pos.CENTER);
            return new DefaultPooledObject<>(textField);
        }

        /**
         * 销毁对象。
         * 当对象池检测到某个对象的空闲时间(idle)超时，或使用完对象归还到对象池之前被检测到对象已经无效时，就会调用这个方法销毁对象。
         * 对象的销毁一般和业务相关，但必须明确的是，当调用这个方法之后，对象的生命周期必须结果。
         */
        @Override
        public void destroyObject(PooledObject<TextField> p) throws Exception {

        }

        /**
         * 检测一个对象是否有效。
         * 在对象池中的对象必须是有效的，这个有效的概念是，从对象池中拿出的对象是可用的。
         * 在从对象池获取对象或归还对象到对象池时，会调用这个方法，判断对象是否有效，如果无效就会销毁。
         */
        @Override
        public boolean validateObject(PooledObject<TextField> p) {
            return false;
        }

        /**
         * 激活一个对象或者说启动对象的某些操作。
         * 它会在检测空闲对象的时候，如果设置了测试空闲对象是否可以用，就会调用这个方法，在borrowObject的时候也会调用。
         * 另外，如果对象是一个包含参数的对象，可以在这里进行初始化，让使用者感觉这是一个新创建的对象一样。
         */
        @Override
        public void activateObject(PooledObject<TextField> p) throws Exception {

        }

        /**
         * 在向对象池归还一个对象是会调用这个方法。
         * 这里可以对对象做一些清理操作，比如清理掉过期的数据，下次获得对象时，不受旧数据的影响。
         */
        @Override
        public void passivateObject(PooledObject<TextField> p) throws Exception {
            TextField textField = p.getObject();
            textField.clear();
            textField.setStyle(null);
        }
    }

    static class LabelPoolFactory implements PooledObjectFactory<Label> {

        @Override
        public PooledObject<Label> makeObject() throws Exception {
            Label label = new Label();
            label.setWrapText(true);
            label.setAlignment(Pos.CENTER);
            return new DefaultPooledObject<>(label);
        }

        @Override
        public void destroyObject(PooledObject<Label> p) throws Exception {

        }

        @Override
        public boolean validateObject(PooledObject<Label> p) {
            return false;
        }

        @Override
        public void activateObject(PooledObject<Label> p) throws Exception {

        }

        @Override
        public void passivateObject(PooledObject<Label> p) throws Exception {
            Label label = p.getObject();
            label.setFont(null);
            label.setText(null);
        }
    }
}
