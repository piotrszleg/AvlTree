import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class AVLTreeTest {

    @org.junit.jupiter.api.Test
    void insert() {
        AVLTree<Integer> tree=new AVLTree<>();
        tree.insert(1);
        tree.insert(5);
        tree.insert(2);
        assertTrue(tree.contains(1));
        assertTrue(tree.contains(5));
        assertTrue(tree.contains(2));
    }

    final AVLTree<Integer> test_tree=new AVLTree<>(new Integer[]{10, 6, 20, 3, 4});

    @org.junit.jupiter.api.Test
    void lower() {
        assertEquals( test_tree.lower(), 3);
    }

    @org.junit.jupiter.api.Test
    void upper() {
        assertEquals( test_tree.upper(), 20);
    }

    @org.junit.jupiter.api.Test
    void delete() {
        AVLTree<Integer> tree=new AVLTree<>(new Integer[]{10, 6, 20, 3, 4});
        tree.delete(4);
        assertFalse(tree.contains(4));
        tree.delete(6);
        assertFalse(tree.contains(6));
    }

    @org.junit.jupiter.api.Test
    void deleteAll(){
        AVLTree<Integer> tree=new AVLTree<>(new Integer[]{4, 6});
        tree.delete(4);
        assertFalse(tree.contains(4));
        tree.delete(6);
        assertFalse(tree.contains(6));
    }

    @org.junit.jupiter.api.Test
    void doubleInsert(){
        AVLTree<Integer> tree=new AVLTree<>();
        tree.insert(5);
        tree.insert(5);
        assertTrue(tree.contains(5));
        assertEquals(1, tree.size());
        tree.delete(5);
        assertFalse(tree.contains(5));
        assertEquals(0, tree.size());
    }

    @org.junit.jupiter.api.Test
    void doubleDelete(){
        AVLTree<Integer> tree=new AVLTree<>();
        tree.insert(5);
        assertTrue(tree.contains(5));
        tree.delete(5);
        tree.delete(5);
        assertFalse(tree.contains(5));
        assertEquals(0, tree.size());
    }

    interface OrderAssertion<T> {
        void test(T previous, T current);
    }

    static class TestVisitor<T extends Comparable<T>> implements AVLTree.Visitor<T> {
        final AVLTree.VisitingOrder order;
        final OrderAssertion<T> orderAssertion;
        final AVLTree<T> tested;

        final AVLTree<T> visited=new AVLTree<>();
        boolean hasPrevious=false;
        T previous;

        public TestVisitor(AVLTree<T> tested, AVLTree.VisitingOrder order, OrderAssertion<T> orderAssertion){
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
        new TestVisitor<>(test_tree, AVLTree.VisitingOrder.IN_ORDER,
            (Integer previous, Integer current)->
                assertTrue(previous.compareTo(current)<=0)
        ).run();
    }

    @org.junit.jupiter.api.Test
    void preOrderVisit() {
        new TestVisitor<>(test_tree, AVLTree.VisitingOrder.PRE_ORDER,
                // children should be visited after their parents
                (Integer previous, Integer current)->
                        assertTrue(test_tree.elementDepth(previous)<=test_tree.elementDepth(current))
        ).run();
    }

    @org.junit.jupiter.api.Test
    void postOrderVisit() {
        new TestVisitor<>(test_tree, AVLTree.VisitingOrder.POST_ORDER,
                // children should be visited before their parents
                (Integer previous, Integer current)->
                        assertTrue(test_tree.elementDepth(previous) >= test_tree.elementDepth(current))
        ).run();
    }

    @org.junit.jupiter.api.Test
    void bigDataset(){
        int elementsRange=20;
        int count=100;

        AVLTree<Integer> tree=new AVLTree<>();
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
        }
    }
}