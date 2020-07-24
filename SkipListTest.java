//import SkipList.SkipListSet;
import java.util.LinkedList;
import java.util.SortedSet;


public class SkipListTest
{
  public static void main(String[] args)
  {
    SkipListSet<Integer> myList = new SkipListSet<Integer>();    
    LinkedList<Integer> linkedList = new LinkedList<Integer>();

    linkedList.add(12);
    linkedList.add(100);
    linkedList.add(56);
    linkedList.add(49);
    linkedList.add(5);
    myList.addAll(linkedList);

    SortedSet<Integer> myTailSet = myList.subSet(56, 100);
    
    for (Integer element : myTailSet) 
    {
      System.out.println(element);
    }

    System.out.println("The last item within the SkipList is: " + myList.last());
    
    System.out.println("Elements in the list: " + myList.size());
  }


}