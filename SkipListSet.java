/*
 * SkipListSet
 * 1.0
 * 6/24/2020
 * Work by: Gregory Freitas
 * Reference(s): Docs.oracle.com. 2020. Java Platform SE 7. [online] Available at: <https://docs.oracle.com/javase/7/docs/api/>
 */

//package SkipList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.Random;
import java.lang.Math;
import java.lang.reflect.Array;
import java.util.Set;

public class SkipListSet<E extends Comparable<E>> implements SortedSet<E>
{ 
  private int mMaximumHeight = 0;
  private int mMinimumHeight = 4;
  private final int mBaseLevelHeight = 0;

  private SkipListSetItem mHead;
  private SkipListSetItem mBaseHead;

  private int mNumberOfElements;

  private Random mRandom;
  
  public SkipListSet()
  {
    this.mHead = new SkipListSetItem(null, 0);
    mBaseHead = mHead;
    mRandom = new Random();

    while(mMaximumHeight < mMinimumHeight)
    {
      growMaxHeight();
    }

    mNumberOfElements = 0;
  }
  
  public SkipListSet(Collection<E> newCollection)
  {
    this.mHead = new SkipListSetItem(null, 0);
    mBaseHead = mHead;
    mRandom = new Random();

    while(mMaximumHeight < mMinimumHeight)
    {
      growMaxHeight();
    }

    mNumberOfElements = 0;

    addAll(newCollection);
  }
  
  /*Grow the skip list by one level. */ 
  private void growMaxHeight()
  {    
    SkipListSetItem newHead = new SkipListSetItem(mHead.element, mHead.height + 1);
    
    newHead.down = mHead;
    mHead = newHead;

    mMaximumHeight++;
  }

  /*Decrease the maximum Height of the skiplist by one, doing so removes a level. */
  private void shrinkMaxHeight()
  {
    if(mMaximumHeight == 0)
      return;

    SkipListSetItem t = mHead;
    mHead = mHead.down;

    while(t != null)
    {
      
      t.down = null;
      t = t.right;
    }

    mMaximumHeight--;
  }
  
  /*Returns true or false with a 50% chance */
  private boolean randomRoll() 
  { 
    boolean attempt = (this.mRandom.nextInt() % 2) == 0 ? true : false;
  
    return attempt; 
  }
  
  /*Give a passed in item an oppurtunity to grow to the maximumHeight. */
  private void growingPhase(SkipListSetItem item)
  {
      SkipListSetItem itemToGrow = item;
      
      // while the roll is a success and the height of our item is still below the maximum height...
      while(randomRoll() && itemToGrow.height < mMaximumHeight)
      { 
        itemToGrow = growItem(itemToGrow);
      }
      
      //System.out.println("My Node: " + tempItem.data + "," + tempItem.height);
  }
  
  /*Creates an item above the passed itemToGrow with the same data but with an increased height,
    then links the nodes appropriately. */
  private SkipListSetItem growItem(SkipListSetItem itemToGrow)
  {
      SkipListSetItem currentItem;
      SkipListSetItem upperItem = new SkipListSetItem(itemToGrow.element, (itemToGrow.height + 1));
      
      upperItem.down = itemToGrow;
      
      // Step to where the item will be inserted.
      currentItem = findItem(itemToGrow.element, upperItem.height);
           
      upperItem.right = currentItem.right;
      upperItem.left = currentItem;
           
      // The item will be placed inbetween two items.
      if(currentItem.right != null)
      {
        currentItem.right.left = upperItem;
        currentItem.right = upperItem;
      }
      // Otherwise it will be the end of the list.
      else
      {
        currentItem.right = upperItem;
      }
        
      return upperItem;
  }
  
  /*Gives every item a chance to grow from the base height. */
  public void reBalance()
  {
    SkipListSetItem newHead = mHead;

    // Sever the upper nodes and traverse down.
    while(newHead.down != null)
    {
      newHead.right = null;
      newHead = newHead.down;
    }
    
    newHead = newHead.right;

    while(newHead != null)
    {
      
      growingPhase(newHead);
      newHead = newHead.right;
    }
  }
    
  /*Returns null to use natural ordering of the set. */
  public Comparator<? super E> comparator(){ return null; }
  
  /*Return the smallest element in the set. */
  public E first()
  {
    if(this.isEmpty())
      throw new NoSuchElementException("There are no elements within " + toString());
    else
      return this.mBaseHead.right.element;
  }
  
  /*Return the largest element in the set. */
  public E last()
  {
    if(this.isEmpty())
      throw new NoSuchElementException("There are no elements within " + toString());

    SkipListSetItem t = mHead;

    while(true)
    {
      while(t.right != null)
      {
        t = t.right;
      }
      
      if(t.down != null)
        t = t.down;
      else
        break;
    }

    return (t.element);
  }
  
  /*Returns a sorted set view of fromElement to toElement. (fromElement <= included <= toElement) */
  public SortedSet<E> subSet(E fromElement, E toElement)
  {
     SortedSet<E> newSubset = new SkipListSet<E>();

    for (E element : this) 
    {
      if(element.compareTo(fromElement) >= 0 && element.compareTo(toElement) <= 0)
      {
        newSubset.add(element);
      }

    }

    return newSubset;
  }
  
  /*Returns a sorted set view of type T. (included <= toElement) */
  public SortedSet<E> headSet(E toElement)
  {
    SortedSet<E> newHeadSet = new SkipListSet<E>();

    for (E element : this) 
    {
      if(toElement.compareTo(element) > 0)
      {
        newHeadSet.add(element);
      }
    }

    return newHeadSet;
  } 
  
  /*Returns a sorted set view of type T. (included >= fromElement) */
  public SortedSet<E> tailSet(E fromElement)
  {
    SortedSet<E> newTailSet = new SkipListSet<E>();

    for (E element : this) 
    {
      if(element.compareTo(fromElement) >= 0)
      {
        newTailSet.add(element);
      }
    }

    return newTailSet;
  } 
  
  /*Takes in an element to search for, and a specified height to look for.
    It then returns the item when its found, or head if it isn't found. */
  private SkipListSetItem findItem(E e, int targetHeight)
  {
    SkipListSetItem currentItem = mHead;
    
    while(true)
    {        

      // If we can move right, and the target element is greater than the element to the right.
      while(currentItem.right != null && e.compareTo(currentItem.right.element) >= 0)
      {   
        // Encountered a duplicate.     
        if(e.compareTo(currentItem.right.element) == 0)
          return currentItem.right;

        // Move right.
        currentItem = currentItem.right;
      }
           

      if(currentItem.height != targetHeight)
      {
        currentItem = currentItem.down;
      }
      // Bottom layer has been reached.
      else
      {  
         break;
      } 
    }  
    
    return currentItem;
  }
  
  /*Takes in an element to add to the SkipListSet. 
    Returns true if the item is added and false if it is not.*/
  public boolean add(E e)
  { 

    SkipListSetItem currentItem = findItem(e, mBaseLevelHeight);
    
    // If there is a duplicate item, then return false.
    if(currentItem.element != null && e.compareTo(currentItem.element) == 0)
      return false;   
    
    
    SkipListSetItem newItem = new SkipListSetItem(e);
    newItem.right = currentItem.right;
    newItem.left = currentItem;
         
    // The item will be placed inbetween two items.
    if(currentItem.right != null)
    {
      currentItem.right.left = newItem;
      currentItem.right = newItem;
    }
    // Otherwise it will be the end of the list.
    else
    {
      currentItem.right = newItem;
    }
         
    mNumberOfElements++;   
    
    growingPhase(newItem);
    
    // If a power of 2 is passed for the size of our list, grow the max height.
    if(mNumberOfElements > (int) (Math.pow(2, mMaximumHeight)))
    {
      growMaxHeight();
    }
    
    return true;
  }
  
  /*Takes in a collection to add any of its elements to this SkipListSet. 
    Returns true on successful modification and false otherwise.*/
  public boolean addAll(Collection<? extends E> col)
  { 
    int failures = 0;

    for (E itemT : col) 
    {
      if(add(itemT) == false)
        failures++;
    }
    
    // Every item has failed to be added.
    if(failures == col.size())
      return false;
    else 
      return true;
  }
  
  /*Removes all the elements from this set. */
  public void clear() 
  {
    SkipListSetItem t = mHead;
    
    // Cut the link to all right items of each head at each level.
    while(t != null)
    {
      t.right = null;
      t = t.down;
    }
    
    mNumberOfElements = 0;
    mMaximumHeight = mMinimumHeight;
  }
  
  /*Takes in an element to search for. Returns true if the element exists in the set
    and false otherwise. */
  @SuppressWarnings("unchecked")
  public boolean contains(Object obj) 
  { 
    if(obj == null)
      return false;

    SkipListSetItem item = findItem((E) obj, mBaseLevelHeight);

    if(item.element != null && item.element.compareTo((E) obj) == 0)
      return true;
    else
      return false;
  }
  
  /*Takes in a collection to compare it contents to this SkipListSet.
    Returns true if the collection is a subset of this SkipListSet, and false otherwise. */
  @SuppressWarnings("unchecked")
  public boolean containsAll(Collection<?> col) 
  { 
    for (Object e : col) 
    {
      if(!contains((E) e))
        return false;
    }

    return true;
  }
  
  /*Takes in an object to compare to. Returns true if the specifed object is
    a Set and the size of the set is equivalent, and if every element contained
    in this Set exists within the passed Set. */
  @Override
  public boolean equals(Object obj)
  {
    Set<?> set;

    // If the passed object is of type set, cast it.
    if(obj instanceof Set)
    {
      set = (Set<?>) obj;
    }
    else 
      return false;

    if(this.size() != set.size())
      return false;

    // If even one element is not contained, then the contract is not held.
    if(!containsAll(set))
      return false;

    return true;
  }
  
  /*Returns a hashcode value for this set, which is defined as
    the sum of hashcodes of each element within the set. */
  @Override
  public int hashCode() 
  {
    int sumOfHashCodes = 0;
    
    for (E item : this)
    {
      sumOfHashCodes += item.hashCode();
    }

    return sumOfHashCodes;
  }
  
  
  /*Return true if the list is empty. */
  public boolean isEmpty() { return (mNumberOfElements == 0); }
  
  /*Returns an Iterator for this SkipListSet. */
  public Iterator<E> iterator()
  {
    final SkipListSetIterator newIterator = new SkipListSetIterator(mBaseHead);

    return newIterator;
  }
  
  /*Takes in an element to remove from this SkipListSet.
    Returns true on successful remove and false otherwise. */
  @SuppressWarnings("unchecked")
  public boolean remove(Object obj) 
  {
    SkipListSetItem t = findItem((E) obj, mBaseLevelHeight);

    // If the element found is not equivalent to desired element, then return false.
    if(t.element.compareTo((E) obj) != 0)
      return false;

    // Handle Item links.
    while(t != null)
    {
      t.left.right = t.right;

      if(t.right != null)
        t.right.left = t.left;
      
      t = t.down;
    }

    mNumberOfElements--;

    // If a power of 2 is passed for the size of our list, shrink the list...
    // ...only if it would not exceed the minimumHeight.
    if(mNumberOfElements < (int) (Math.pow(2, mMaximumHeight)) && (mMaximumHeight - 1 >= mMinimumHeight))
    {
      shrinkMaxHeight();
    }

    return true;
  }
  
  /*Takes in a collection and removes all elements that exist in the collection from
    this SkipListSet. Returns true on successful modification of SkipList, and false otherwise. */
  @SuppressWarnings("unchecked")
  public boolean removeAll(Collection<?> col) 
  {
    int failures = 0;

    for (Object obj : col) 
    {
      if(!remove((E) obj))
        failures++;
    }

    if(failures == col.size())
      return false;
    else
      return true;
  }
  
  /*Takes in a collection and removes all elements that dont exist within the collection from
    this SkipListSet. Returns true on successful modifiaction of SkipList, and false otherwise. */
  public boolean retainAll(Collection<?> col) 
  { 
    Iterator<E> it = this.iterator();
    int failures = 0;

    while(it.hasNext())
    {
      if(col.contains(it.next()))
        failures++;
      else
        it.remove();
    }

    if(failures == col.size() || this.size() == 0)
      return false;
    else
      return true;
  }
  
  /*Return the number of elements within this SkipList. */
  public int size() { return (this.mNumberOfElements); }
  
  /*Returns an Object array of all the elements within this SkipListSet. */
  public Object[] toArray() 
  {
    int i = 0;
    final Iterator<E> it = this.iterator();
    final Object[] newArray = new Object[this.size()];

    while(it.hasNext())
    {
      newArray[i] = it.next();
      i++;
    }

    return newArray;
  }
  
  /*Takes in an a generic array to be modified.
    Returns the array with the elements within the SkipListSet. */
  @SuppressWarnings("unchecked")
  public <T> T[] toArray(T[] a) 
  {
    if (a.length < this.size()) 
    { 
      a = (T[]) Array.newInstance(a.getClass().getComponentType(), this.mNumberOfElements);
    } 
    else if (a.length > this.size()) 
    {
      a[this.size()] = null;
    }
    
    Iterator<E> it = this.iterator();

    for(int i = 0; i < this.mNumberOfElements; i++)
    {
      if(it.hasNext())
        a[i] = (T) it.next();
    }

    return a;
  }

  /*Prints the skip list and each item as many times as they appear at their height */
  public void printListLayout()
  {
    SkipListSetItem tempItem = mHead;
    SkipListSetItem tempItem2 = mHead;

    // Traverse vertically to move down a level.
    while(tempItem2 != null)
    {
      // Print each element horizontally among that level.
      while(tempItem != null)
      {
        System.out.print(tempItem.element + " ");
        tempItem = tempItem.right;
      }
      System.out.println("");
      tempItem2 = tempItem2.down;
      tempItem = tempItem2;
    }
  }

  /*Return the current maximum height of the tree. */
  public int getHeight()
  {
    return this.mMaximumHeight;
  }
  
  /*Iterator for the SkipListSet. */
  private class SkipListSetIterator implements Iterator<E>
  {
    SkipListSetItem currentItem;

    public SkipListSetIterator(SkipListSetItem currentItem)
    {
      this.currentItem = currentItem;
    }   
    
    /*Return true if there are more items to iterate over. */
    public boolean hasNext(){ return (currentItem.right != null); }
    
    /*Move to the right and then return the element */
    public E next()
    {
      currentItem = currentItem.right;
      
      return currentItem.element;
    }
    
    /*Remove the last returned item in the iteration. */
    public void remove()
    {
        SkipListSet.this.remove(currentItem.element);
    }   
  } 
  
  /*Represents the container for the elements within the SkipList */
  private class SkipListSetItem 
  {

    SkipListSetItem right, left, down;
    int height;
    E element;

    public SkipListSetItem(E data)
    {
      this.element = data;
      this.height = 0;
      this.right = left = down = null;
    }
    
    public SkipListSetItem(E data, int height)
    {  
      this.element = data;
      this.height = height;
      this.right = left = down = null;
    }

    @Override
    public String toString()
    {
      return ("(Data, Height)" + element + "," + height);
    }
  }

}

