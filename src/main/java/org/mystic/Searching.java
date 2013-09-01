package org.mystic;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;

/**
 * @author kperikov
 */
public class Searching {

    private static final String PATH_TO_INDEX = "/home/mysterion/Downloads/solr-4.4.0/example/solr/collection1/data/index";

    public static void main(String[] args) {
        try {
            FSDirectory fsDirectory = FSDirectory.open(new File(PATH_TO_INDEX));
            IndexReader indexReader = DirectoryReader.open(fsDirectory);
            IndexSearcher searcher = new IndexSearcher(indexReader);
            System.out.println(indexReader.numDocs());
        } catch (IOException e) {
            System.out.println("Error while opening index" + e.getCause());
        }

    }
}
