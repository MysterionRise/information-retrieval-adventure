import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.elasticsearch.Version;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.*;
import org.elasticsearch.indices.analysis.AnalysisModule;
import org.elasticsearch.indices.analysis.PreBuiltAnalyzers;
import org.elasticsearch.plugins.AnalysisPlugin;

/** @see https://stackoverflow.com/q/45987194/2663985 */
public class MyAnalyzerPlugin implements AnalysisPlugin {

  @Override
  public Map<String, AnalysisModule.AnalysisProvider<AnalyzerProvider<? extends Analyzer>>>
      getAnalyzers() {
    final Map<String, AnalysisModule.AnalysisProvider<AnalyzerProvider<? extends Analyzer>>>
        objectObjectHashMap = new HashMap<>();
    objectObjectHashMap.put("my_analyzer", new MyAnalyzerProviderFactory());
    return objectObjectHashMap;
  }

  class MyAnalyzerProviderFactory implements AnalysisModule.AnalysisProvider<AnalyzerProvider<?>> {

    private final MyAnalyzerProvider analyzerProvider;

    public MyAnalyzerProviderFactory() {
      analyzerProvider = new MyAnalyzerProvider(AnalyzerScope.INDICES);
    }

    public AnalyzerProvider<?> create(String name, Settings settings) {
      Version indexVersion = Version.indexCreated(settings);
      if (!Version.CURRENT.equals(indexVersion)) {
        PreBuiltAnalyzers preBuiltAnalyzers = PreBuiltAnalyzers.getOrDefault(name, null);
        if (preBuiltAnalyzers != null) {
          Analyzer analyzer = preBuiltAnalyzers.getAnalyzer(indexVersion);
          return new MyAnalyzerProvider(AnalyzerScope.INDICES);
        }
      }

      return analyzerProvider;
    }

    @Override
    public AnalyzerProvider<?> get(
        IndexSettings indexSettings, Environment environment, String name, Settings settings)
        throws IOException {
      return create(name, settings);
    }

    public Analyzer analyzer() {
      return analyzerProvider.get();
    }
  }

  class MyAnalyzerProvider implements AnalyzerProvider<StandardAnalyzer> {

    private final StandardAnalyzer analyzer;
    private final AnalyzerScope scope;

    public MyAnalyzerProvider(AnalyzerScope scope) {
      // we create the named analyzer here so the resources associated with it will be shared
      // and we won't wrap a shared analyzer with named analyzer each time causing the resources
      // to not be shared...
      this.scope = scope;
      this.analyzer = new StandardAnalyzer();
    }

    @Override
    public String name() {
      return "my-standard-analyzer";
    }

    @Override
    public AnalyzerScope scope() {
      return scope;
    }

    @Override
    public StandardAnalyzer get() {
      return analyzer;
    }
  }
}
