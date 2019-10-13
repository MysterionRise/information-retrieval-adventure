package price;

import org.apache.lucene.search.Query;

public class CustomPriceQuery extends Query {

  @Override
  public String toString(String s) {
    return null;
  }

  @Override
  public boolean equals(Object o) {
    return false;
  }

  @Override
  public int hashCode() {
    return 0;
  }
}
