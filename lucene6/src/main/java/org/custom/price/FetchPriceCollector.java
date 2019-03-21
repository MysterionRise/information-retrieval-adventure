package org.custom.price;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.search.*;

public class FetchPriceCollector extends DelegatingCollector {

  private final ResponseBuilder rb;

  public FetchPriceCollector(ResponseBuilder rb) {
    this.rb = rb;
  }

  public void finish() {
    // TODO call price system
    System.out.println("calling price system");
    try {
      TimeUnit.SECONDS.sleep(1);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    Map<Integer, Integer> res = (Map<Integer, Integer>) rb.rsp.getValues().get("ids");
    rb.rsp.add("res", res.size());
  }
}
