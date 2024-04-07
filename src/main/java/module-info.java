open module com.english {
    requires lombok;
    requires java.sql;
    requires org.slf4j;
    requires javafx.controls;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires org.apache.httpcomponents.httpcore;
    requires org.apache.httpcomponents.httpclient;
    requires javafx.media;

    exports com.english;
}
