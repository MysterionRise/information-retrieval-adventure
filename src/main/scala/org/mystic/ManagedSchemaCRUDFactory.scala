package org.mystic

import org.apache.lucene.analysis.core.{KeywordTokenizerFactory, LowerCaseFilterFactory}
import org.apache.lucene.analysis.util.TokenFilterFactory
import org.apache.solr.analysis.TokenizerChain
import org.apache.solr.common.SolrInputDocument
import org.apache.solr.request.SolrQueryRequest
import org.apache.solr.response.SolrQueryResponse
import org.apache.solr.schema.{IndexSchema, TextField}
import org.apache.solr.update.AddUpdateCommand
import org.apache.solr.update.processor.{UpdateRequestProcessor, UpdateRequestProcessorFactory}

import scala.collection.JavaConversions


class ManagedSchemaCRUDFactory extends UpdateRequestProcessorFactory {

  def getInstance(req: SolrQueryRequest, rsp: SolrQueryResponse, next: UpdateRequestProcessor): UpdateRequestProcessor = {
    return new ManagedSchemaCRUDProcessor(req.getSchema, next)
  }
}

class ManagedSchemaCRUDProcessor(indexSchema: IndexSchema, next: UpdateRequestProcessor) extends UpdateRequestProcessor(next) {

  override def processAdd(cmd: AddUpdateCommand): Unit = {
    val field: TextField = new TextField()
    val filters: java.util.ArrayList = new java.util.ArrayList[TokenFilterFactory]()
    filters.add(new LowerCaseFilterFactory())
    field.setMultiTermAnalyzer(new TokenizerChain(new KeywordTokenizerFactory(), filters.toArray(new Array[TokenFilterFactory](1))))
    val fieldTypes = indexSchema.getFieldTypes.put("testFieldType", field)
    indexSchema.refreshAnalyzers()
    JavaConversions.mapAsScalaMap(fieldTypes).foreach(println)
    val doc: SolrInputDocument = cmd.getSolrInputDocument
    val updatedDoc = addExtraFields(doc)
    doc
    cmd.solrDoc = updatedDoc
    super.processAdd(cmd)
  }

  def addExtraFields(doc: SolrInputDocument): SolrInputDocument = {
    val updatedDoc = new SolrInputDocument()
    if (doc.hasChildDocuments) {
      val childs = doc.getChildDocuments.toArray(new Array[SolrInputDocument](1))
        .map(child => addExtraFields(child))
      if (!childs.isEmpty) {
        updatedDoc.addChildDocuments(JavaConversions.seqAsJavaList(childs.toSeq))
      }
    }
    val fields = doc.getFieldNames.toArray(new Array[String](1)).map({
      fieldName =>
        updatedDoc.addField(fieldName, doc.getFieldValues(fieldName))
    })
    updatedDoc
  }

}