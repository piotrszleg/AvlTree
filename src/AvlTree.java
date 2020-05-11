import java.util.Arrays;

class AvlTree<T extends Comparable<T>> {

    private class Node {
        private T value;
        private int height;
        private Node left;
        private Node right;

        Node(T d) {
            setValue(d);
            setHeight(1);
        }

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public Node getLeft() {
            return left;
        }

        public void setLeft(Node left) {
            this.left = left;
        }

        public Node getRight() {
            return right;
        }

        public void setRight(Node right) {
            this.right = right;
        }
    }

    public interface Visitor<T> {
        void visit(T element);
    }

    private Node root;

    public AvlTree(){

    }

    public AvlTree(T[] array){
        for(T element : array){
            insert(element);
        }
    }

    // number of spaces that need to be placed before element at depth n
    // in order for the graph to have a correct shape
    // a(0)=0, a(n)=a(n-1)*2+1 in iterative form
    public int spacesCount(int n){
        return (int)Math.pow(2, n)-1;
    }

    private String repeatCharacter(char character, int length){
        char[] array=new char[length];
        Arrays.fill(array, character);
        return new String(array);
    }

    private String graphRecursive(Node node){
        if(node!=null){
            return node.getValue().toString();
        } else {
            return " ";
        }
    }

    private void graphRecursive(Node node, String[] lines, int depth){
        String space=repeatCharacter(' ', spacesCount(root.getHeight() -depth));
        if(lines[depth]==null){
            lines[depth]=space+graphRecursive(node);
        } else {
            lines[depth]+=space+" "+space+graphRecursive(node);
        }
        if(node!=null) {
            graphRecursive(node.getLeft(), lines, depth + 1);
            graphRecursive(node.getRight(), lines, depth + 1);
        }
    }

    public String graph() {
        String[] lines=new String[root.getHeight() +1];
        graphRecursive(root, lines, 0);
        return String.join("\n", lines);
    }

    int sizeRecursive(Node node){
        if(node==null){
            return 0;
        } else {
            return 1+sizeRecursive(node.getLeft())+sizeRecursive(node.getRight());
        }
    }

    public int size(){
        return sizeRecursive(root);
    }

    private int height(Node N) {
        if (N == null)
            return 0;

        return N.getHeight();
    }

    private void updateHeight(Node node){
        node.setHeight(Math.max(height(node.getLeft()), height(node.getRight())) + 1);
    }

    private Node rightRotate(Node y) {
        Node x = y.getLeft();
        Node T2 = x.getRight();

        // Perform rotation 
        x.setRight(y);
        y.setLeft(T2);

        // Update heights
        updateHeight(y);
        updateHeight(x);

        // Return new root 
        return x;
    }

    private Node leftRotate(Node x) {
        Node y = x.getRight();
        Node T2 = y.getLeft();

        // Perform rotation 
        y.setLeft(x);
        x.setRight(T2);

        //  Update heights 
        x.setHeight(Math.max(height(x.getLeft()), height(x.getRight())) + 1);
        y.setHeight(Math.max(height(y.getLeft()), height(y.getRight())) + 1);

        // Return new root 
        return y;
    }

    // Get Balance factor of node N 
    private int getBalance(Node node) {
        if (node == null)
            return 0;

        return height(node.getLeft()) - height(node.getRight());
    }

    private boolean isAvlBalancedRecursive(Node node){
        if(node!=null){
            if(Math.abs(getBalance(node))>1){
                return false;
            } else {
                return isAvlBalancedRecursive(node.left)
                        && isAvlBalancedRecursive(node.right);
            }
        } else {
            return true;
        }
    }

    boolean isAvlBalanced(){
        return isAvlBalancedRecursive(root);
    }

    private Node fixAfterInsertion(Node node, T key){
        int balance = getBalance(node);

        // left-left case
        if (balance > 1 && key.compareTo(node.getLeft().getValue())<0)
            return rightRotate(node);

        // right-right case
        if (balance < -1 && key.compareTo(node.getRight().getValue())>0)
            return leftRotate(node);

        // left-right case
        if (balance > 1 && key.compareTo(node.getLeft().getValue())>0) {
            node.setLeft(leftRotate(node.getLeft()));
            return rightRotate(node);
        }

        // right-left case
        if (balance < -1 && key.compareTo(node.getRight().getValue())<0) {
            node.setRight(rightRotate(node.getRight()));
            return leftRotate(node);
        }

        return node;
    }

    private Node insert(Node node, T key) {
        if (node == null)
            return new Node(key);

        if (key.compareTo(node.getValue())<0) {
            node.setLeft(insert(node.getLeft(), key));
        } else if (key.compareTo(node.getValue())>0) {
            node.setRight(insert(node.getRight(), key));
        } else {
            // Replace already existing node
            Node newNode = new Node(key);
            newNode.setRight(node.getRight());
            newNode.setLeft(node.getLeft());
            node=newNode;
        }

        updateHeight(node);
        return fixAfterInsertion(node, key);
    }

    public void insert(T element){
        root=insert(root, element);
    }

    private Node lowerNode(Node node)
    {
        Node current = node;

        // find element the furthest left element in the tree
        while (current.getLeft() != null) {
            current = current.getLeft();
        }

        return current;
    }

    public T lower(){
        return lowerNode(root).getValue();
    }

    private Node upperNode(Node node)
    {
        Node current = node;

        // find element the furthest right element in the tree
        while (current.getRight() != null) {
            current = current.getRight();
        }

        return current;
    }

    public T upper(){
        return upperNode(root).getValue();
    }

    private Node fixAfterDeletion(Node node){
        int balance = getBalance(node);

        // left-left case
        if (balance > 1 && getBalance(node.getLeft()) >= 0)
            return rightRotate(node);

        // left-right case
        if (balance > 1 && getBalance(node.getLeft()) < 0)
        {
            node.setLeft(leftRotate(node.getLeft()));
            return rightRotate(node);
        }

        // right-right case
        if (balance < -1 && getBalance(node.getRight()) <= 0) {
            return leftRotate(node);
        }

        // right-left case
        if (balance < -1 && getBalance(node.getRight()) > 0)
        {
            node.setRight(rightRotate(node.getRight()));
            return leftRotate(node);
        }

        return node;
    }

    private Node deleteNode(Node node, T key)
    {
        if (node == null) {
            return null;
        }

        if (key.compareTo(node.getValue())<0) {
            // search left subtree
            node.setLeft(deleteNode(node.getLeft(), key));
        } else if (key.compareTo(node.getValue())>0) {
            // search right subtree
            node.setRight(deleteNode(node.getRight(), key));
        } else {
            // node with only one child or no child
            if ((node.getLeft() == null) || (node.getRight() == null))
            {
                Node temp;
                if (node.getLeft() !=null) {
                    temp = node.getLeft();
                } else {
                    temp = node.getRight();
                }

                // the node has no children
                if (temp == null)
                {
                    return null;
                }
                else {
                    // replace the node with its only child
                    node = temp;
                }
            }
            else
            {
                // get the lowest node in the right subtree
                Node lowest = lowerNode(node.getRight());
                // replace node value with the lowest value
                node.setValue(lowest.getValue());
                // delete the lowest in right subtree using this function recursively
                node.setRight(deleteNode(node.getRight(), lowest.getValue()));
            }
        }

        updateHeight(node);
        return fixAfterDeletion(node);
    }

    public void delete(T element) {
        root = deleteNode(root, element);
    }

    private boolean containsRecursive(Node node, T element){
        if(node==null){
            return false;
        } else if(node.getValue().equals(element)){
            return true;
        } else {
            return containsRecursive(node.getLeft(), element)
                    || containsRecursive(node.getRight(), element);
        }
    }

    public boolean contains(T element){
        return containsRecursive(root, element);
    }

    int elementDepthRecursive(Node node, T element){
        if(node==null){
            return -1;
        }
        int onLeft=elementDepthRecursive(node.getLeft(), element);
        if(onLeft!=-1){
            return onLeft;
        } else {
            return elementDepthRecursive(node.getRight(), element);
        }
    }

    int elementDepth(T element){
        return elementDepthRecursive(root, element);
    }

    private void inOrderRecursive(Node node, Visitor<T> visitor) {
        if (node != null) {
            inOrderRecursive(node.getLeft(), visitor);
            visitor.visit(node.getValue());
            inOrderRecursive(node.getRight(), visitor);
        }
    }

    private void preOrderRecursive(Node node, Visitor<T> visitor) {
        if (node != null) {
            visitor.visit(node.getValue());
            preOrderRecursive(node.getLeft(), visitor);
            preOrderRecursive(node.getRight(), visitor);
        }
    }

    private void postOrderRecursive(Node node, Visitor<T> visitor) {
        if (node != null) {
            postOrderRecursive(node.getLeft(), visitor);
            postOrderRecursive(node.getRight(), visitor);
            visitor.visit(node.getValue());
        }
    }

    public enum VisitingOrder {
        IN_ORDER,
        PRE_ORDER,
        POST_ORDER,
    }

    public void accept(Visitor<T> visitor, VisitingOrder order) {
        switch (order) {
            case IN_ORDER:
                inOrderRecursive(root, visitor);
                break;
            case PRE_ORDER:
                preOrderRecursive(root, visitor);
                break;
            case POST_ORDER:
                postOrderRecursive(root, visitor);
                break;
        }
    }
}