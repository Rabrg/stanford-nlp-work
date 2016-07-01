package me.rabrg.stanfordnlp;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.naturalli.OpenIE;
import edu.stanford.nlp.naturalli.SentenceFragment;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.PropertiesUtils;

import java.util.Collection;
import java.util.List;
import java.util.Properties;

/**
 * A demo illustrating how to call the OpenIE system programmatically.
 */
public class OpenIEDemo {

  private OpenIEDemo() {} // static main

  public static void main(String[] args) throws Exception {
    // Create the Stanford CoreNLP pipeline
    Properties props = PropertiesUtils.asProperties(
            "annotators", "tokenize,ssplit,pos,lemma,depparse,natlog,openie"
            // , "depparse.model", "edu/stanford/nlp/models/parser/nndep/english_SD.gz"
            // "annotators", "tokenize,ssplit,pos,lemma,parse,natlog,openie"
            // , "parse.originalDependencies", "true"
    );
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

    // Annotate an example document.
    String text;
    if (args.length > 0) {
      text = IOUtils.slurpFile(args[0]);
    } else {
      text = "The cat jumped over the fence.";
    }
    Annotation doc = new Annotation(text);
    pipeline.annotate(doc);

    // Loop over sentences in the document
    int sentNo = 0;
    for (CoreMap sentence : doc.get(CoreAnnotations.SentencesAnnotation.class)) {
      System.out.println("Sentence #" + ++sentNo + ": " + sentence.get(CoreAnnotations.TextAnnotation.class));

      // Print SemanticGraph
      System.out.println(sentence.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class).toString(SemanticGraph.OutputFormat.LIST));

      // Get the OpenIE triples for the sentence
      Collection<RelationTriple> triples = sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);

      // Print the triples
      for (RelationTriple triple : triples) {
        System.out.println("GOOD LINE: " + triple.confidence + "\t" +
            triple.subjectLemmaGloss() + "\t" +
            triple.relationLemmaGloss() + "\t" +
            triple.objectLemmaGloss());
      }
    }
  }

}