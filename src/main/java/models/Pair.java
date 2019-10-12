package models;

/**
 * This class allows us to maintain Pairs of objects.
 *
 * @author Vijay
 * @param <K> The key of the pair
 * @param <V> The value of the pair
 */
public class Pair<K, V> {
  /** This instance variable stores the key, i.e. the generic object K. */
  private K key;
  /** This instance variable stores the value, i.e. the generic object V. */
  private V value;

  /**
   * This constructor initializes the Pair.
   *
   * @param key The key object
   * @param value The value object
   */
  public Pair(K key, V value) {
    this.key = key;
    this.value = value;
  }

  /**
   * This method returns the key object.
   *
   * @return An object which has the key.
   */
  public K getKey() {
    return key;
  }

  /**
   * This method sets the key object for the pair.
   *
   * @param key The key object.
   */
  public void setKey(K key) {
    this.key = key;
  }

  /**
   * This method gets the value for the pair.
   *
   * @return The value object.
   */
  public V getValue() {
    return value;
  }

  /**
   * This method sets the value for the pair.
   *
   * @param value The value object.
   */
  public void setValue(V value) {
    this.value = value;
  }
}
