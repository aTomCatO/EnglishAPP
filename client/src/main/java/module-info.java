open module com.english {
    requires javafx.controls;
    requires lombok;
    requires org.slf4j;
    requires io.netty.all;
    requires io.netty.codec;
    requires io.netty.buffer;
    requires io.netty.handler;
    requires io.netty.transport;
    requires io.netty.transport.unix.common;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    exports com.english;
}
