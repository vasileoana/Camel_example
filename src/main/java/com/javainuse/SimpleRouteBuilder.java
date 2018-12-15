package com.javainuse;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.GenericFile;

public class SimpleRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("file:C:/Users/Oana/Desktop/from?noop=true")
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        FileCustom file = new FileCustom();
                        String fileName = exchange.getIn().getBody(GenericFile.class).getFileName();
                        exchange.getIn().setBody(fileName);
                    }
                })
                .choice()
                .when(body().contains(".txt"))
                .to("file:C:/Users/Oana/Desktop/txt")
                .when(body().contains(".xml"))
                .to("file:C:/Users/Oana/Desktop/xml")
                .otherwise()
                .to("file:C:/Users/Oana/Desktop/to");

    }

}
