package me.rabrg.stanfordnlp;

import edu.stanford.nlp.simple.Sentence;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SentenceDatabase {

    private final Connection connection;

    private final PreparedStatement selectStatement;
    private final PreparedStatement insertStatement;

    public SentenceDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:sentences.db");

        final Statement createStatement = connection.createStatement();
        createStatement.executeUpdate("CREATE TABLE IF NOT EXISTS sentences (sentence STRING, UNIQUE(sentence));");

        selectStatement = connection.prepareStatement("select * from sentences");
        insertStatement = connection.prepareStatement("INSERT OR IGNORE INTO sentences('sentence') VALUES(?)");
    }

    public static void main(final String[] args) {
        try {
            final SentenceDatabase database = new SentenceDatabase();
            database.addTriple("The cat jumped over the fence");
            database.addTriple("Grandfather left Rosalita and Raoul all his money");
            database.addTriple("They named their daughter Natasha");

            for (final Triple object : database.getTriples()) {
                System.out.println(object);
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Triple> getTriples() throws SQLException {
        final List<Triple> result = new ArrayList<>();
        final ResultSet rs = selectStatement.executeQuery();
        while (rs.next()) {
            result.add(new Triple(new Sentence(rs.getString("sentence"))));
        }
        return result;
    }

    public int addTriple(final String sentence) throws SQLException {
        insertStatement.setString(1, sentence);
        return insertStatement.executeUpdate();
    }
}
