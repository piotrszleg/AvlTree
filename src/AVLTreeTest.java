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

    abstract class TestVisitor<T extends Comparable<T>> implements AVLTree.Visitor<T> {
        boolean hasPrevious=false;
        T previous;
        final AVLTree<T> visited=new AVLTree<>();
        abstract void assertion(T previous, T current);
        public void visit(T element){
            if(hasPrevious) {
                assertion(previous, element);
            }
            visited.insert(element);
            previous=element;
            hasPrevious=true;
        }
    }

    @org.junit.jupiter.api.Test
    void inOrderVisit() {
        test_tree.visit(new TestVisitor<>() {
            @Override
            void assertion(Integer previous, Integer current) {
                assertTrue(previous.compareTo(current)<=0);
            }
        }, AVLTree.VisitingOrder.IN_ORDER);
    }
}