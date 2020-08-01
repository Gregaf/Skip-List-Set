//import SkipList.SkipListSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.SortedSet;
import java.util.TreeSet;


public class SkipListTest
{
  public static void main(String[] args)
  { 
    TreeSet<Integer> treeSet = new TreeSet<Integer>();

    treeSet.add(12);
    treeSet.add(100);
    treeSet.add(56);
    treeSet.add(49);
    treeSet.add(5);

    SkipListSet<Integer> myList = new SkipListSet<Integer>(treeSet);   
    myList.first();

    Integer[] stuff = myList.toArray(new Integer[myList.size()]);
    
    System.out.println(myList.equals(treeSet));


    for (Integer element : stuff) 
    {
      System.out.println(element);
    }

  }


}