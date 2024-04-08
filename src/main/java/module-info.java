open module com.english {
    requires lombok;
    requires java.sql;
    requires org.slf4j;
    requires javafx.media;
    requires javafx.controls;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires org.apache.httpcomponents.core5.httpcore5;
    requires org.apache.httpcomponents.client5.httpclient5;

    exports com.english;
}
