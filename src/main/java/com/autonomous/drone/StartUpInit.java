package com.autonomous.drone;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Stream;

@Component
public class StartUpInit {
    @Value("${spring.datasource.url}")
    String url;

    @Value("${spring.datasource.username}")
    String username;

    @Value("${spring.datasource.password}")
    String pass;

    /*
    * Init sql data from data.sql (Hibernate cannot init trigger function and set triggers)
    * */
    @PostConstruct
    public void init(){
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("data.sql").getFile());
        String sqlStatement = fileToString(file.getAbsolutePath());
        try {
            Connection connection = DriverManager.getConnection(url, username, pass);
            Statement statement = connection.createStatement();
            statement.execute(sqlStatement);
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String fileToString(String filePath)
    {
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines( Paths.get(filePath), StandardCharsets.UTF_8))
        {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }
}
