package com.javainuse;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.GenericFile;
import org.apache.camel.component.sql.SqlComponent;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SimpleRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        SqlComponent component = getContext().getComponent("sql", SqlComponent.class);
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/filemanager");
        dataSource.setUsername("root");
        component.setDataSource(dataSource);
        final Connection connection = dataSource.getConnection();
        final Statement statement = connection.createStatement();
        final List<String> files = new ArrayList<String>();
        from("file:C:/Users/Oana/Desktop/from")
                .process(new Processor() {
                    public void process(Exchange exchange) {
                        String fileName = exchange.getIn().getBody(GenericFile.class).getFileName();
                        exchange.getIn().setBody(fileName);
                      //  files.add(fileName);
                    }
                })

                .choice()
                .when(body().contains(".txt"))
                .to("file:C:/Users/Oana/Desktop/txt")
                .when(body().contains(".xml"))
                .to("file:C:/Users/Oana/Desktop/xml")
                .otherwise()
                .to("file:C:/Users/Oana/Desktop/to")
                .endChoice()
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        Date date = new Date();
                        exchange.getIn().setBody("INSERT INTO `file`(`name`, `date`) VALUES('" + exchange.getIn().getBody(String.class) + "','" + date.toString()+ "')");
                    }
                })
                .split().body()
                .recipientList(simple("sql:${in.body}"));
    }

}
