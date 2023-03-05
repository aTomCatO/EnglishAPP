open module com.english {
    requires javafx.controls;
    requires lombok;
    requires org.slf4j;
    requires reflections;
    requires io.netty.all;
    requires io.netty.codec;
    requires io.netty.buffer;
    requires io.netty.handler;
    requires io.netty.transport;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires io.netty.transport.unix.common;


    exports com.english;
}
