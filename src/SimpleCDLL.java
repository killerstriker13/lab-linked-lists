import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;


/**
 * Simple circular doubly-linked lists with a dummy node.
 * @author Shibam Mukhopadhyay
 * @author Samuel A. Rebelsky
 */
public class SimpleCDLL<T> implements SimpleList<T> {
  // +--------+------------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * The dummy node for the circular doubly linked list.
   */
  private Node2<T> dummy;
  /**
   * The front of the list
   */
  Node2<T> front;

  /**
   * The number of values in the list.
   */
  int size;

  /**
   * number of changes made (useful for fail fast)!
   */
  int numChanges = 0;
  // +--------------+------------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create an empty list with a dummy node.
   */
  public SimpleCDLL() {
    this.dummy = new Node2<>(null);
    this.dummy.next = this.dummy;
    this.dummy.prev = this.dummy;
    this.front = this.dummy;
    this.size = 0;
  } // SimpleCDLL

  // +-----------+---------------------------------------------------------
  // | Iterators |
  // +-----------+

  @Override
  public Iterator<T> iterator() {
    return listIterator();
  } // iterator()

  @Override
  public ListIterator<T> listIterator() {
    return new ListIterator<T>() {
      // +--------+--------------------------------------------------------
      // | Fields |
      // +--------+

      /**
       * The position in the list of the next value to be returned.
       * Included because ListIterators must provide nextIndex and
       * prevIndex.
       */
      int pos = 0;

      /**
       * The cursor is between neighboring values, so we start links
       * to the previous and next value with dummy node.
       */
      Node2<T> prev = SimpleCDLL.this.dummy;
      Node2<T> next = SimpleCDLL.this.dummy.next;

      /**
       * number of Changes for each iterator for fail fast strategy
       */
      int numChanges = SimpleCDLL.this.numChanges;
      /**
       * The node to be updated by remove or set.  Has a value of
       * null when there is no such value.
       */
      Node2<T> update = null;

      // +---------+-------------------------------------------------------
      // | Methods |
      // +---------+

      public void add(T val) {
        if(this.numChanges != SimpleCDLL.this.numChanges){
            throw new ConcurrentModificationException("Invalid Iterator");
        }
        //Node2<T> addNode = new Node2<>(val);
        this.prev = this.prev.insertAfter(val);

        // Increase the size
        ++SimpleCDLL.this.size;

        // Update the position
        ++this.pos;

        //Update number of changes made by each iterator
        ++this.numChanges;
        ++SimpleCDLL.this.numChanges;
      } // add(T)

      public boolean hasNext() {
        if(this.numChanges != SimpleCDLL.this.numChanges){
            throw new ConcurrentModificationException("Invalid Iterator");
        }
        return (this.next != SimpleCDLL.this.dummy);
      } // hasNext()

      public boolean hasPrevious() {
        if(this.numChanges != SimpleCDLL.this.numChanges){
            throw new ConcurrentModificationException("Invalid Iterator");
        }
        return (this.prev != SimpleCDLL.this.dummy);
      } // hasPrevious()

      public T next() {
        if(this.numChanges != SimpleCDLL.this.numChanges){
            throw new ConcurrentModificationException("Invalid Iterator");
        }
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        } // if
        // Identify the node to update
        this.update = this.next;
        // Advance the cursor
        this.prev = this.next;
        this.next = this.next.next;
        // Note the movement
        ++this.pos;
        // And return the value
        return this.update.value;
      } // next()

      public int nextIndex() {
        if(this.numChanges != SimpleCDLL.this.numChanges){
            throw new ConcurrentModificationException("Invalid Iterator");
        }
        return this.pos;
      } // nextIndex()

      public int previousIndex() {
        if(this.numChanges != SimpleCDLL.this.numChanges){
            throw new ConcurrentModificationException("Invalid Iterator");
        }
        return this.pos - 1;
      } // prevIndex

      public T previous() {
        if(this.numChanges != SimpleCDLL.this.numChanges){
            throw new ConcurrentModificationException("Invalid Iterator");
        }
        if (!this.hasPrevious()) {
          throw new NoSuchElementException();
        } // if
        // Identify the node to update
        this.update = this.prev;
        // Advance the cursor
        this.next = this.prev;
        this.prev = this.prev.prev;
        // Note the movement
        --this.pos;
        // And return the value
        return this.update.value;
      } // previous()

      public void remove() {
        if(this.numChanges != SimpleCDLL.this.numChanges){
            throw new ConcurrentModificationException("Invalid Iterator");
        }
        // Sanity check
        if (this.update == null) {
          throw new IllegalStateException();
        } // if

        // Update the cursor
        if (this.next == this.update) {
            this.next = this.update.next;
          } // if
          if (this.prev == this.update) {
            this.prev = this.update.prev;
            --this.pos;
          } // if

        // Update the front if needed
        if (SimpleCDLL.this.front == this.update) {
          if(this.update.next == SimpleCDLL.this.dummy){
            SimpleCDLL.this.front = null;
          } else {
            SimpleCDLL.this.front = this.update.next;
          } // if else
        } // if

        // Do the real work
        this.update.remove();

        // Decrease the size
        --SimpleCDLL.this.size;

        // Note that no more updates are possible
        this.update = null;

        //Update number of changes made by each iterator
        ++this.numChanges;
        ++SimpleCDLL.this.numChanges;
      } // remove()

      public void set(T val) {
        // Sanity check
        if (this.update == null) {
          throw new IllegalStateException();
        } // if
        // Do the real work
        this.update.value = val;
      } // set(T)
    };
  } // listIterator()
} // class SimpleCDLL<T>