open module com.english {
    requires lombok;
    requires java.sql;
    requires org.slf4j;
    requires io.netty.codec;
    requires io.netty.buffer;
    requires io.netty.handler;
    requires io.netty.transport;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires org.apache.httpcomponents.httpcore;
    requires org.apache.httpcomponents.httpclient;

    exports com.english;
}
