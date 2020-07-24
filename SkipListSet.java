/*
 * SkipListSet
 * 1.0
 * 6/24/2020
 */
//package SkipList;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.Random;
import java.lang.Math;

// need a reference to java docs
@SuppressWarnings("unchecked")
public class SkipListSet<T extends Comparable<T>> implements SortedSet<T>
{ 
  private final int mMinimumHeight = 4;
  private final int mBaseLevelHeight = 0;

  private SkipListSetItem mHead;
  private int mNumberOfElements;

  private int mMaximumHeight = 0;
  private Random mRandom;
  
  public SkipListSet()
  {
    this.mHead = new SkipListSetItem(null, 0);
    mRandom = new Random();

    
    while(mMaximumHeight < mMinimumHeight)
    {
      growHeight();
    }

    mNumberOfElements = 0;
  }
  
  public SkipListSet(Collection<T> newCollection)
  {
    mRandom = new Random(2);

    while(mMaximumHeight < mMinimumHeight)
    {
      growHeight();
    }
    
    addAll(newCollection);
  }
  
  // Return the current maximum height of the tree.
  public int GetHeight()
  {
    return this.mMaximumHeight;
  }

  // Grow the list by one level.
  private void growHeight()
  {    
    SkipListSetItem newHead = new SkipListSetItem(mHead.element, mHead.height + 1);
    
    newHead.down = mHead;
    mHead = newHead;

    mMaximumHeight++;
  }

  private void shrinkHeight()
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
  
  // 50% chance to return true or false.
  private boolean randomRoll() 
  { 
    boolean attempt = (this.mRandom.nextInt() % 2) == 0 ? true : false;
  
    return attempt; 
  }
  
  // Give an item an oppurtunity to grow to the maximumHeight
  private void growingPhase(SkipListSetItem item)
  {
      SkipListSetItem tempItem = item;
      
      // while the roll is a success and the height of our item is still below the maximum height...
      while(randomRoll() && tempItem.height < mMaximumHeight)
      { 
        tempItem = growItem(tempItem);
      }
      
      //System.out.println("My Node: " + tempItem.data + "," + tempItem.height);
  }
  
  private SkipListSetItem getBaseHead()
  {
    SkipListSetItem t = mHead;

    while(t.down != null)
    {
      t = t.down;
    }

    return t;
  }

  // create an item above the item to grow with the same data but an increased height, then link the nodes appropriately.
  private SkipListSetItem growItem(SkipListSetItem itemToGrow)
  {
      SkipListSetItem t;
      SkipListSetItem upperItem = new SkipListSetItem(itemToGrow.element, (itemToGrow.height + 1));
      
      upperItem.down = itemToGrow;
      
      // Step to where the item will be inserted.
      t = findItem(itemToGrow.element, upperItem.height);
           
      upperItem.right = t.right;
      upperItem.left = t;
           
      // The item will be placed inbetween two items.
      if(t.right != null)
      {
        t.right.left = upperItem;
        t.right = upperItem;
      }
      // Otherwise it will be the end of the list.
      else
      {
        t.right = upperItem;
      }
        
      return upperItem;
  }
  
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
    
  // returns null to use natural ordering of the set.
  public Comparator<? super T> comparator(){ return null; }
  
  // Return the smallest element in the set.
  public T first()
  {
    SkipListSetItem t = mHead;

    // Move t down to the base level.
    while(t.down != null)
    {
      t = t.down;
    }

    // If the list is not empty, return the element stored in the item to the right.
    if(t.right != null)
      return (t.right.element);
    else
      throw new NoSuchElementException("There are no elements within " + toString());
    
  }
  
  // Return the largest element in the set.
  public T last()
  {
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
  
  // Returns a set view of fromElement - toElement.
  public SortedSet<T> subSet(T fromElement, T toElement)
  {
    SortedSet<T> newSubset = new SkipListSet<T>();

    for (T element : this) 
    {
      if(element.compareTo(fromElement) >= 0 && element.compareTo(toElement) <= 0)
      {
        newSubset.add(element);
      }

    }

    return newSubset;
  }
  
  // Returns a set view of all elements < toElement.
  public SortedSet<T> headSet(T toElement)
  {
    SortedSet<T> newHeadSet = new SkipListSet<T>();

    for (T element : this) 
    {
      if(toElement.compareTo(element) > 0)
      {
        newHeadSet.add(element);
      }
    }

    return newHeadSet;
  } 
  
  // Return a set view of all elements >= fromElement.
  public SortedSet<T> tailSet(T fromElement)
  {
    SortedSet<T> newTailSet = new SkipListSet<T>();

    for (T element : this) 
    {
      if(element.compareTo(fromElement) >= 0)
      {
        newTailSet.add(element);
      }
    }

    return newTailSet;
  } 
  
  public void printListLayout()
  {
    SkipListSetItem tempItem = mHead;
    SkipListSetItem tempItem2 = mHead;

    while(tempItem2 != null)
    {
      while(tempItem != null)
      {
        System.out.print(tempItem.element + " ");
        tempItem =tempItem.right;
      }
      System.out.println("");
      tempItem2 = tempItem2.down;
      tempItem = tempItem2;
    }
  }

  // Accepts an element to search for at a specified height...
  // ...and returns that item or the closest element.
  private SkipListSetItem findItem(T e, int targetHeight)
  {
    SkipListSetItem currentItem = mHead;
    
    while(true)
    {        
      while(currentItem.right != null && e.compareTo(currentItem.right.element) >= 0)
      {   
        // Encountered a duplicate     
        if(e.compareTo(currentItem.right.element) == 0)
          return currentItem.right;

        currentItem = currentItem.right;
      }
           
      if(currentItem.height != targetHeight)
      {
        currentItem = currentItem.down;
      }
      // We are on bottom layer and can now insert the new item.
      else
      {  
         break;
      } 
    }  
    
    return currentItem;
  }
  
  // Inserts the element into the set, returning true on a successful add...
  // ...otherwise return false for any duplicates.
  public boolean add(T e)
  { 
    SkipListSetItem t = findItem(e, mBaseLevelHeight);
    
    if(t.element != null && e.compareTo(t.element) == 0)
      return false;   
    
    SkipListSetItem newItem = new SkipListSetItem(e);
         
    newItem.right = t.right;
    newItem.left = t;
         
    // The item will be placed inbetween two items.
    if(t.right != null)
    {
      t.right.left = newItem;
      t.right = newItem;
    }
    // Otherwise it will be the end of the list.
    else
    {
      t.right = newItem;
    }
         
    mNumberOfElements++;   
    
    growingPhase(newItem);
    
    // If a power of 2 is passed for the size of our list, grow the max height.
    if(mNumberOfElements > (int) (Math.pow(2, mMaximumHeight)))
    {
      growHeight();
    }
    
    return true;
  }
  
  // Add all elements from the supplied collection to this SkipListSet...
  // ...then return true on a successful modification, otherwise false.
  public boolean addAll(Collection<? extends T> col)
  { 
    int failures = 0;

    for (T itemT : col) 
    {
      if(add(itemT) == false)
        failures++;
    }
    
    if(failures == col.size())
      return false;
    else 
      return true;
  }
  
  // Remove all the elements from this set.
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
  }
  
  // Search for the supplied element within this set...
  // ...return true if the element is found, otherwise false.
  public boolean contains(Object obj) 
  { 
    if(obj == null)
      return false;

    SkipListSetItem item = findItem((T) obj, mBaseLevelHeight);

    if(item.element != null && item.element.compareTo((T) obj) == 0)
      return true;
    else
      return false;
  }
  
  // Check if all the elements within the collection are within the skipListSet...
  // ...return true if the collections is a subset of SkipListSet, otherwise false.
  public boolean containsAll(Collection<?> col) 
  { 
    int failures = 0;

    for (Object e : col) 
    {
      if(!contains((T) e))
        failures++;
    }

    if(failures > 0)
      return false;
    else
      return true;
  }
  
  public boolean equals(Object obj)
  {
    if(this.hashCode() == obj.hashCode())
      return true;
    else
      return false;
  }
  
  public int hashCode() 
  {
    int hashCodeSum = 0;
    Iterator<T> it = this.iterator();

    while(it.hasNext())
    {
      System.out.println(it.next().hashCode());
    }
    
    return hashCodeSum;
  }
  
  
  // Return true if the list is empty.
  public boolean isEmpty() { return (mNumberOfElements == 0); }
  
  public Iterator<T> iterator()
  {
    SkipListSetIterator newIterator = new SkipListSetIterator(getBaseHead());

    return newIterator;
  }
  
  public boolean remove(Object obj) 
  {
    SkipListSetItem t = findItem((T) obj, mBaseLevelHeight);

    if(t.element.compareTo((T) obj) != 0)
      return false;

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
      shrinkHeight();
    }

    return true;
  }
  
  public boolean removeAll(Collection<?> col) 
  {
    int failures = 0;

    for (Object obj : col) 
    {
      if(!remove((T) obj))
        failures++;
    }

    if(failures == col.size())
      return false;
    else
      return true;
  }
  
  public boolean retainAll(Collection<?> col) 
  { 
    Iterator<T> it = this.iterator();
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
  
  public int size() { return (this.mNumberOfElements); }
  
  public Object[] toArray() 
  {
    int i = 0;

    Iterator<T> it = this.iterator();
    Object[] newArray = new Object[this.size()];
    while(it.hasNext())
    {
      newArray[i] = it.next();
      i++;
    }

    return newArray;
  }
  
  public <T> T[] toArray(T[] a) 
  {
    return null;
  }
  
  private class SkipListSetIterator implements Iterator<T>
  {
    // need a currentNode so we can move spaces
    SkipListSetItem currentItem;

    public SkipListSetIterator(SkipListSetItem currentItem)
    {
      this.currentItem = currentItem;
    }   
    
    // Check the right.right
    public boolean hasNext(){ return (currentItem.right != null);}
    
    public T next()
    {
      currentItem = currentItem.right;
      
      return currentItem.element;
    }
    
    // Remove the last returned item in the iteration.
    public void remove()
    {
        SkipListSet.this.remove(currentItem.element);
    }   
  } 
  
  
  private class SkipListSetItem
  {

    SkipListSetItem right, left, down;
    int height;
    T element;

    public SkipListSetItem(T data)
    {
      this.element = data;
      this.height = 0;
      this.right = left = down = null;
    }
    
    public SkipListSetItem(T data, int height)
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

