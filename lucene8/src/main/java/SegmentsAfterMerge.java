import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

/**
 * Checking what happens to commits after merge
 */
public class SegmentsAfterMerge {

    public static void main(String[] args) throws IOException, InterruptedException {

        Directory dir =
                new NIOFSDirectory(Files.createTempDirectory(SegmentsAfterMerge.class.getName()));

        indexDocs(dir, "A", 1_000_000);
        printCommits(dir);

        indexDocs(dir, "B", 1_000_000);
        printCommits(dir);

        indexDocs(dir, "C", 100_000_000);
        printCommits(dir);

        indexDocs(dir, "D", 100_000_000);
        printCommits(dir);

        deleteDocs(dir, "C");

        indexDocs(dir, "E", 100_000_000);
        printCommits(dir);

        forceMerge(dir);
        printCommits(dir);

        List<IndexCommit> indexCommits = DirectoryReader.listCommits(dir);
        System.out.println("Number of commits at the end " + indexCommits.size());
        for (IndexCommit commit : indexCommits) {
            DirectoryReader open = DirectoryReader.open(commit);
            IndexSearcher searcher = new IndexSearcher(open);
            Query q = new MatchAllDocsQuery();
            System.out.println("-------Searching-in-commit" + commit);
            TopDocs results = searcher.search(q, 10);
            final ScoreDoc[] scoreDocs = results.scoreDocs;
            for (ScoreDoc scoreDoc : scoreDocs) {
                System.out.println(
                        scoreDoc.doc + " " + open.document(scoreDoc.doc) + " " + scoreDoc.score);
            }
        }


    }

    private static void deleteDocs(Directory dir, String category) throws IOException {
        IndexWriterConfig iwc = new IndexWriterConfig(new StandardAnalyzer());
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        IndexWriter writer = new IndexWriter(dir, iwc);
        writer.deleteDocuments(new TermQuery(new Term("category", category)));
        writer.close();
    }

    private static void forceMerge(Directory dir) throws IOException {
        IndexWriterConfig iwc = new IndexWriterConfig(new StandardAnalyzer());
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        IndexWriter writer = new IndexWriter(dir, iwc);
        writer.forceMergeDeletes();
        writer.close();
    }

    private static void printCommits(Directory dir) throws IOException {
        System.out.println("-------Printing-commits-----------------");
        List<IndexCommit> commits = DirectoryReader.listCommits(dir);
        System.out.println("Number of commits " + commits.size());
        for (IndexCommit commit : commits) {
            System.out.println("Segment count " + commit.getSegmentCount());
            System.out.println("Generation " + commit.getGeneration());
            System.out.println("Segments file name " + commit.getSegmentsFileName());
        }
    }

    private static void indexDocs(Directory dir, String category, int size) throws IOException {
        IndexWriterConfig iwc = new IndexWriterConfig(new StandardAnalyzer())
                .setMaxBufferedDocs(1_000_000)
                .setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND)
                .setIndexDeletionPolicy(NoDeletionPolicy.INSTANCE);
        IndexWriter writer = new IndexWriter(dir, iwc);
        for (int i = 0; i < size; ++i) {
            Document doc = new Document();
            doc.add(new StringField("id", UUID.randomUUID().toString(), Field.Store.YES));
            doc.add(new TextField("category", category, Field.Store.YES));
            writer.addDocument(doc);
        }
        writer.commit();
        writer.flush();
        writer.close();
    }
}
