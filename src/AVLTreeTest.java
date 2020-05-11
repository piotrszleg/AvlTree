import javax.lang.model.element.Element;

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
    void doubleInsert(){
        AVLTree<Integer> tree=new AVLTree<>();
        tree.insert(5);
        tree.insert(5);
        assertTrue(tree.contains(5));
        tree.delete(5);
        assertFalse(tree.contains(5));
    }

    interface OrderAssertion<T> {
        void test(T previous, T current);
    }

    class TestVisitor<T extends Comparable<T>> implements AVLTree.Visitor<T> {
        AVLTree.VisitingOrder order;
        OrderAssertion<T> orderAssertion;
        AVLTree<T> tested;

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
            tested.visit(this, order);
            // set can only contain one instance of each element
            // so if visited has same size as tested all the visitor visited all the elements
            // first line of visit guarantees that visited won't contain elements that aren't in tested
            assertEquals(tested.size(), visited.size());
        }
    }

    @org.junit.jupiter.api.Test
    void inOrderVisit() {
        new TestVisitor<Integer>(test_tree, AVLTree.VisitingOrder.IN_ORDER,
            (Integer previous, Integer current)->
                assertTrue(previous.compareTo(current)<=0)
        ).run();
    }

    @org.junit.jupiter.api.Test
    void postOrderVisit() {
        new TestVisitor<Integer>(test_tree, AVLTree.VisitingOrder.POST_ORDER,
                (Integer previous, Integer current)->
                    assertTrue(test_tree.elementDepth(previous) >= test_tree.elementDepth(current))
        ).run();
    }

    @org.junit.jupiter.api.Test
    void preOrderVisit() {
        new TestVisitor<Integer>(test_tree, AVLTree.VisitingOrder.PRE_ORDER,
                (Integer previous, Integer current)->
                        assertTrue(test_tree.elementDepth(previous)<=test_tree.elementDepth(current))
        ).run();
    }
}