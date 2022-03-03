package ma.octo.environment.abstraction.database;

import ma.octo.environment.abstraction.entity.Language;
import ma.octo.environment.abstraction.util.Logger;
import ma.octo.environment.abstraction.util.impl.LoggerImpl;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.core.env.Environment;

import org.h2.jdbcx.JdbcDataSource;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class H2DataBaseConfig implements InitializingBean {

    private final String Url;
    private final String UserName;
    private final String password;
    private Connection connection;


    PreparedStatement preparedStatement;
    ResultSet resultSet;
    Language language = null;

    public H2DataBaseConfig(Environment environment) {
        Url = environment.getProperty("app.db.url");
        UserName = environment.getProperty("app.db.username");
        password = environment.getProperty("app.db.password");
    }

    @Override
    public void afterPropertiesSet() {
        JdbcDataSource ds = createH2DataBase();
        try{
            connection = ds.getConnection();
            System.out.println("Connected to H2 DataBase");
            initH2DataBase();
            System.out.println("Languages has created and inserted with data");

        } catch (SQLException throwables) {
            System.out.println("Not connected to H2 DataBase "+throwables.getMessage());
        }
    }

    private JdbcDataSource createH2DataBase(){
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL(Url);
        ds.setUser(UserName);
        ds.setPassword(password);
        return ds;
    }
    private void initH2DataBase() throws SQLException {
        var stm = connection.createStatement();
        stm.execute("DROP TABLE IF EXISTS LANGUAGES;"+
                        "CREATE TABLE LANGUAGES(id VARCHAR(20), nom VARCHAR(20), author VARCHAR(40), fileExtension VARCHAR(40));"+
                        "INSERT INTO LANGUAGES(id, nom, author, fileExtension) VALUES" +
                            "('java', 'Java', 'James Gosling', 'java'),"+
                            "('cpp', 'C++', 'Bjarne Stroustrup', 'cpp'),"+
                            "('csharp', 'C#', 'Andres Hejlsberg', 'cs'),"+
                            "('perl', 'Perl', 'Larry Wall', 'pl'),"+
                            "('haskel', 'Haskell', 'Simon Peyton', 'hs'),"+
                            "('lua', 'Lua', 'Luiz Henrique', 'lua'),"+
                            "('python', 'Python', 'Guido van Rossum', 'py');");
    }

    public List<Language> getAllLanguages() {
        List languages = new ArrayList();
        try {
            preparedStatement = connection.prepareStatement("select * from LANGUAGES");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                languages.add( new Language(
                        resultSet.getString("id"),
                        resultSet.getString("nom"),
                        resultSet.getString("author"),
                        resultSet.getString("fileExtension")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error when execute getAllLanguages "+e.getMessage());
        }
        return languages;
    }

    public Optional<Language> getLanguageById(String id) {
        try {
            preparedStatement = connection.prepareStatement("select * from LANGUAGES where id = ?");
            preparedStatement.setString(1, id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                language = new Language(
                        resultSet.getString("id"),
                        resultSet.getString("nom"),
                        resultSet.getString("author"),
                        resultSet.getString("fileExtension")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error when execute getLanguageById "+e.getMessage());
        }

        if(language == null) return Optional.empty();
        else return Optional.of(language);
    }

    public Optional<Language> getLanguageByExtension(String fileExtension) {
        try {
            preparedStatement = connection.prepareStatement("select * from LANGUAGES where fileExtension = ?");
            preparedStatement.setString(1, fileExtension);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                language = new Language(
                        resultSet.getString("id"),
                        resultSet.getString("nom"),
                        resultSet.getString("author"),
                        resultSet.getString("fileExtension")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error when execute getLanguageById "+e.getMessage());
        }
        if(language == null) return Optional.empty();
        else return Optional.of(language);
    }


}
