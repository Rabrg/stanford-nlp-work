package me.rabrg.stanfordnlp;

import edu.stanford.nlp.simple.Sentence;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SentenceDatabase {

    private Connection connection;
    private Statement statement;

    public SentenceDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:sentences.db");
        statement = connection.createStatement();
        statement.setQueryTimeout(30);
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS sentences (sentence STRING, UNIQUE(sentence));");
    }

    public static void main(final String[] args) {
        try {
            final SentenceDatabase database = new SentenceDatabase();
            database.addSentence("The cat jumped over the fence");
            database.addSentence("Grandfather left Rosalita and Raoul all his money");
            database.addSentence("They named their daughter Natasha");

            for (final SubjectPredicateObject object : database.getSentences()) {
                System.out.println(object);
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public List<SubjectPredicateObject> getSentences() throws SQLException {
        final List<SubjectPredicateObject> result = new ArrayList<>();
        final ResultSet rs = statement.executeQuery("select * from sentences");
        while(rs.next()) {
            result.add(new SubjectPredicateObject(new Sentence(rs.getString("sentence"))));
        }
        return result;
    }

    public int addSentence(final String sentence) throws SQLException {
        return statement.executeUpdate("INSERT OR IGNORE INTO sentences VALUES('" + sentence + "')");
    }
}
