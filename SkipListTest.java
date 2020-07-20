//import SkipList.SkipListSet;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.SortedSet;
import java.util.ArrayList;

public class SkipListTest
{
  public static void main(String[] args)
  {
    SkipListSet<Integer> myList = new SkipListSet<Integer>();    
    LinkedList<Integer> linkedList = new LinkedList<Integer>();
    ArrayList<Integer> arrayList = new ArrayList<Integer>();

    linkedList.add(12);
    linkedList.add(100);
    linkedList.add(56);
    linkedList.add(49);
    linkedList.add(5);
    myList.addAll(linkedList);

    System.out.println("The last item within the SkipList is: " + myList.last());
    
    System.out.println("Elements in the list: " + myList.size());
  }


}