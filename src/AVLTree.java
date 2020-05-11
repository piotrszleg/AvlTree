import java.util.Arrays;

class AVLTree<T extends Comparable<T>> {

    private class Node {
        T key;
        int height;
        Node left;
        Node right;

        Node(T d) {
            key = d;
            height = 1;
        }
    }

    public interface Visitor<T> {
        void visit(T element);
    }

    private Node root;

    public AVLTree(){

    }

    public AVLTree(T[] array){
        for(T element : array){
            insert(element);
        }
    }

    // a(0)=0, a(n)=a(n-1)*2+1 in iterative form
    public int spaces(int n){
        return (int)Math.pow(2, n)-1;
    }

    private String repeatCharacter(char character, int length){
        char[] array=new char[length];
        Arrays.fill(array, character);
        return new String(array);
    }

    private String graphRecursive(Node node){
        if(node!=null){
            return node.key.toString();
        } else {
            return " ";
        }
    }

    private void graphRecursive(Node node, String[] lines, int depth){
        String space=repeatCharacter(' ', spaces(root.height-depth));
        if(lines[depth]==null){
            lines[depth]=space+graphRecursive(node);
        } else {
            lines[depth]+=space+" "+space+graphRecursive(node);
        }
        if(node!=null) {
            graphRecursive(node.left, lines, depth + 1);
            graphRecursive(node.right, lines, depth + 1);
        }
    }

    public String graph() {
        String[] lines=new String[root.height+1];
        graphRecursive(root, lines, 0);
        return String.join("\n", lines);
    }

    int sizeRecursive(Node node){
        if(node==null){
            return 0;
        } else {
            return sizeRecursive(node.left)+sizeRecursive(node.right);
        }
    }

    public int size(){
        return sizeRecursive(root);
    }

    private int height(Node N) {
        if (N == null)
            return 0;

        return N.height;
    }

    private void updateHeight(Node node){
        node.height=Math.max(height(node.left), height(node.right)) + 1;
    }

    private Node rightRotate(Node y) {
        Node x = y.left;
        Node T2 = x.right;

        // Perform rotation 
        x.right = y;
        y.left = T2;

        // Update heights
        updateHeight(y);
        updateHeight(x);

        // Return new root 
        return x;
    }

    private Node leftRotate(Node x) {
        Node y = x.right;
        Node T2 = y.left;

        // Perform rotation 
        y.left = x;
        x.right = T2;

        //  Update heights 
        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        // Return new root 
        return y;
    }

    // Get Balance factor of node N 
    private int getBalance(Node N) {
        if (N == null)
            return 0;

        return height(N.left) - height(N.right);
    }

    private Node fixAfterInsertion(Node node, T key){
        int balance = getBalance(node);

        // left-left case
        if (balance > 1 && key.compareTo(node.left.key)<0)
            return rightRotate(node);

        // right-right case
        if (balance < -1 && key.compareTo(node.right.key)>0)
            return leftRotate(node);

        // left-right case
        if (balance > 1 && key.compareTo(node.left.key)>0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        // right-left case
        if (balance < -1 && key.compareTo(node.right.key)<0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    private Node insert(Node node, T key) {
        if (node == null)
            return new Node(key);

        if (key.compareTo(node.key)<0)
            node.left = insert(node.left, key);
        else if (key.compareTo(node.key)>0)
            node.right = insert(node.right, key);
        else {
            // Replace already existing node
            Node newNode = new Node(key);
            newNode.right=node.right;
            newNode.left=node.left;
            return newNode;
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
        while (current.left != null) {
            current = current.left;
        }

        return current;
    }

    public T lower(){
        return lowerNode(root).key;
    }

    private Node upperNode(Node node)
    {
        Node current = node;

        // find element the furthest right element in the tree
        while (current.right != null) {
            current = current.right;
        }

        return current;
    }

    public T upper(){
        return upperNode(root).key;
    }

    private Node fixAfterDeletion(Node node){
        int balance = getBalance(node);

        // left-left case
        if (balance > 1 && getBalance(node.left) >= 0)
            return rightRotate(node);

        // left-right case
        if (balance > 1 && getBalance(node.left) < 0)
        {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        // right-right case
        if (balance < -1 && getBalance(node.right) <= 0) {
            return leftRotate(node);
        }

        // right-left case
        if (balance < -1 && getBalance(node.right) > 0)
        {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    private Node deleteNode(Node node, T key)
    {
        if (node == null) {
            return null;
        }

        if (key.compareTo(node.key)<0) {
            // search left subtree
            node.left = deleteNode(node.left, key);
        } else if (key.compareTo(node.key)>0) {
            // search right subtree
            node.right = deleteNode(node.right, key);
        } else {
            // node with only one child or no child
            if ((node.left == null) || (node.right == null))
            {
                Node temp;
                if (node.left!=null) {
                    temp = node.left;
                } else {
                    temp = node.right;
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
                Node lowest = lowerNode(node.right);
                // replace node value with the lowest value
                node.key = lowest.key;
                // delete the lowest in right subtree using this function recursively
                node.right = deleteNode(node.right, lowest.key);
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
        } else if(node.key.equals(element)){
            return true;
        } else {
            return containsRecursive(node.left, element)
                    || containsRecursive(node.right, element);
        }
    }

    public boolean contains(T element){
        return containsRecursive(root, element);
    }

    int elementDepthRecursive(Node node, T element){
        if(node==null){
            return -1;
        }
        int onLeft=elementDepthRecursive(node.left, element);
        if(onLeft!=-1){
            return onLeft;
        } else {
            return elementDepthRecursive(node.right, element);
        }
    }

    int elementDepth(T element){
        return elementDepthRecursive(root, element);
    }

    private void inOrderRecursive(Node node, Visitor<T> visitor) {
        if (node != null) {
            inOrderRecursive(node.left, visitor);
            visitor.visit(node.key);
            inOrderRecursive(node.right, visitor);
        }
    }

    private void preOrderRecursive(Node node, Visitor<T> visitor) {
        if (node != null) {
            visitor.visit(node.key);
            preOrderRecursive(node.left, visitor);
            preOrderRecursive(node.right, visitor);
        }
    }

    private void postOrderRecursive(Node node, Visitor<T> visitor) {
        if (node != null) {
            postOrderRecursive(node.left, visitor);
            postOrderRecursive(node.right, visitor);
            visitor.visit(node.key);
        }
    }

    public enum VisitingOrder {
        IN_ORDER,
        PRE_ORDER,
        POST_ORDER,
    }

    public void visit(Visitor<T> visitor, VisitingOrder order) {
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