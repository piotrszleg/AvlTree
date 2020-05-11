import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class AvlTreeTest {

    @org.junit.jupiter.api.Test
    void insert() {
        AvlTree<Integer> tree=new AvlTree<>();
        tree.insert(1);
        tree.insert(5);
        tree.insert(2);
        assertTrue(tree.contains(1));
        assertTrue(tree.contains(5));
        assertTrue(tree.contains(2));
        assertTrue(tree.isAvlBalanced());
    }

    final AvlTree<Integer> test_tree=new AvlTree<>(new Integer[]{10, 6, 20, 3, 4});

    @org.junit.jupiter.api.Test
    void lower() {
        assertEquals( test_tree.lower(), 3);
    }

    @org.junit.jupiter.api.Test
    void upper() {
        assertEquals( test_tree.upper(), 20);
    }

    @org.junit.jupiter.api.Test
    void size() {
        assertEquals( test_tree.size(), 5);
    }

    @org.junit.jupiter.api.Test
    void delete() {
        AvlTree<Integer> tree=new AvlTree<>(new Integer[]{10, 6, 20, 3, 4});
        tree.delete(4);
        assertFalse(tree.contains(4));
        tree.delete(6);
        assertFalse(tree.contains(6));
        assertTrue(tree.isAvlBalanced());
    }

    @org.junit.jupiter.api.Test
    void deleteAll(){
        AvlTree<Integer> tree=new AvlTree<>(new Integer[]{4, 6});
        tree.delete(4);
        assertFalse(tree.contains(4));
        tree.delete(6);
        assertFalse(tree.contains(6));
        assertTrue(tree.isAvlBalanced());
    }

    @org.junit.jupiter.api.Test
    void doubleInsert(){
        AvlTree<Integer> tree=new AvlTree<>();
        tree.insert(5);
        tree.insert(5);
        assertTrue(tree.contains(5));
        assertEquals(1, tree.size());
        tree.delete(5);
        assertFalse(tree.contains(5));
        assertEquals(0, tree.size());
        assertTrue(tree.isAvlBalanced());
    }

    @org.junit.jupiter.api.Test
    void doubleDelete(){
        AvlTree<Integer> tree=new AvlTree<>();
        tree.insert(5);
        assertTrue(tree.contains(5));
        tree.delete(5);
        tree.delete(5);
        assertFalse(tree.contains(5));
        assertEquals(0, tree.size());
        assertTrue(tree.isAvlBalanced());
    }

    interface OrderAssertion<T> {
        void test(T previous, T current);
    }

    static class TestVisitor<T extends Comparable<T>> implements AvlTree.Visitor<T> {
        final AvlTree.VisitingOrder order;
        final OrderAssertion<T> orderAssertion;
        final AvlTree<T> tested;

        final AvlTree<T> visited=new AvlTree<>();
        boolean hasPrevious=false;
        T previous;

        public TestVisitor(AvlTree<T> tested, AvlTree.VisitingOrder order, OrderAssertion<T> orderAssertion){
            this.tested=tested;
            this.order=order;
            this.orderAssertion=orderAssertion;
        }
        public void visit(T element){
            assertTrue(tested.contains(element));
            if(hasPrevious) {
                orderAssertion.test(previous, element);
            }
            visited.insert(element);
            previous=element;
            hasPrevious=true;
        }
        void run(){
            tested.accept(this, order);
            // set can only contain one instance of each element
            // so if visited has same size as tested all the visitor visited all the elements
            // first line of visit guarantees that visited won't contain elements that aren't in tested
            assertEquals(tested.size(), visited.size());
        }
    }

    @org.junit.jupiter.api.Test
    void inOrderVisit() {
        new TestVisitor<>(test_tree, AvlTree.VisitingOrder.IN_ORDER,
            (Integer previous, Integer current)->
                assertTrue(previous.compareTo(current)<=0)
        ).run();
    }

    @org.junit.jupiter.api.Test
    void preOrderVisit() {
        new TestVisitor<>(test_tree, AvlTree.VisitingOrder.PRE_ORDER,
                // children should be visited after their parents
                (Integer previous, Integer current)->
                        assertTrue(test_tree.elementDepth(previous)<=test_tree.elementDepth(current))
        ).run();
    }

    @org.junit.jupiter.api.Test
    void postOrderVisit() {
        new TestVisitor<>(test_tree, AvlTree.VisitingOrder.POST_ORDER,
                // children should be visited before their parents
                (Integer previous, Integer current)->
                        assertTrue(test_tree.elementDepth(previous) >= test_tree.elementDepth(current))
        ).run();
    }

    @org.junit.jupiter.api.Test
    void bigDataset(){
        int elementsRange=20;
        int count=100;

        AvlTree<Integer> tree=new AvlTree<>();
        Random random=new Random(123);

        for(int i=0; i<count; i++){
            // more actions (75% in this case) should be insertions
            // so that it is more likely we first add an element and then delete it
            boolean insertionOrDeletion=random.nextFloat()<0.75f;
            Integer element=random.nextInt(elementsRange);
            if(insertionOrDeletion){
                tree.insert(element);
                assertTrue(tree.contains(element));
            } else {
                tree.delete(element);
                assertFalse(tree.contains(element));
            }
            assertTrue(tree.isAvlBalanced());
        }
    }
}