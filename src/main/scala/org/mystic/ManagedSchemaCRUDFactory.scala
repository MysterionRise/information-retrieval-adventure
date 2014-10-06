package org.mystic

import org.apache.solr.common.SolrInputDocument
import org.apache.solr.request.SolrQueryRequest
import org.apache.solr.response.SolrQueryResponse
import org.apache.solr.schema.IndexSchema
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
      fieldName => updatedDoc.addField(fieldName, doc.getFieldValues(fieldName))
    })
    updatedDoc
  }

}