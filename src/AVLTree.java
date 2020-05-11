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

    public int spaces(int n){
        return (int)Math.pow(2, n)-1;
    }

    private String repeatCharacter(char character, int length){
        char[] array=new char[length];
        for(int i=0; i<length; i++){
            array[i]=character;
        }
        return new String(array);
    }

    private String graphRecursive(Node node){
        if(node!=null){
            return node.key.toString();
        } else {
            return " ";
        }
    }

    private void toStringRecursive(Node node, String[] lines, int depth){
        String space=repeatCharacter(' ', spaces(root.height-depth));
        if(lines[depth]==null){
            lines[depth]=space+graphRecursive(node);
        } else {
            lines[depth]+=space+" "+space+graphRecursive(node);
        }
        if(node!=null) {
            toStringRecursive(node.left, lines, depth + 1);
            toStringRecursive(node.right, lines, depth + 1);
        }
    }

    public String graph() {
        String[] lines=new String[root.height+1];
        toStringRecursive(root, lines, 0);
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

        /* 2. Update height of this ancestor node */
        node.height = 1 + Math.max(height(node.left),
                height(node.right));
  
        /* 3. Get the balance factor of this ancestor 
              node to check whether this node became 
              unbalanced */
        int balance = getBalance(node);

        // If this node becomes unbalanced, then there 
        // are 4 cases Left Left Case 
        if (balance > 1 && key.compareTo(node.left.key)<0)
            return rightRotate(node);

        // Right Right Case 
        if (balance < -1 && key.compareTo(node.right.key)>0)
            return leftRotate(node);

        // Left Right Case 
        if (balance > 1 && key.compareTo(node.left.key)>0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        // Right Left Case 
        if (balance < -1 && key.compareTo(node.right.key)<0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    public void insert(T element){
        root=insert(root, element);
    }

    private Node lowerNode(Node node)
    {
        Node current = node;

        /* loop down to find the leftmost leaf */
        while (current.left != null)
            current = current.left;

        return current;
    }

    public T lower(){
        return lowerNode(root).key;
    }

    private Node upperNode(Node node)
    {
        Node current = node;

        /* loop down to find the leftmost leaf */
        while (current.right != null)
            current = current.right;

        return current;
    }

    public T upper(){
        return upperNode(root).key;
    }

    private Node deleteNode(Node root, T key)
    {
        // STEP 1: PERFORM STANDARD BST DELETE
        if (root == null)
            return root;

        // If the key to be deleted is smaller than
        // the root's key, then it lies in left subtree
        if (key.compareTo(root.key)<0)
            root.left = deleteNode(root.left, key);

            // If the key to be deleted is greater than the
            // root's key, then it lies in right subtree
        else if (key.compareTo(root.key)>0)
            root.right = deleteNode(root.right, key);

            // if key is same as root's key, then this is the node
            // to be deleted
        else
        {

            // node with only one child or no child
            if ((root.left == null) || (root.right == null))
            {
                Node temp = null;
                if (temp == root.left)
                    temp = root.right;
                else
                    temp = root.left;

                // No child case
                if (temp == null)
                {
                    temp = root;
                    root = null;
                }
                else // One child case
                    root = temp; // Copy the contents of
                // the non-empty child
            }
            else
            {

                // node with two children: Get the inorder
                // successor (smallest in the right subtree)
                Node temp = lowerNode(root.right);

                // Copy the inorder successor's data to this node
                root.key = temp.key;

                // Delete the inorder successor
                root.right = deleteNode(root.right, temp.key);
            }
        }

        // If the tree had only one node then return
        if (root == null)
            return root;

        // STEP 2: UPDATE HEIGHT OF THE CURRENT NODE
        root.height = Math.max(height(root.left), height(root.right)) + 1;

        // STEP 3: GET THE BALANCE FACTOR OF THIS NODE (to check whether
        // this node became unbalanced)
        int balance = getBalance(root);

        // If this node becomes unbalanced, then there are 4 cases
        // Left Left Case
        if (balance > 1 && getBalance(root.left) >= 0)
            return rightRotate(root);

        // Left Right Case
        if (balance > 1 && getBalance(root.left) < 0)
        {
            root.left = leftRotate(root.left);
            return rightRotate(root);
        }

        // Right Right Case
        if (balance < -1 && getBalance(root.right) <= 0)
            return leftRotate(root);

        // Right Left Case
        if (balance < -1 && getBalance(root.right) > 0)
        {
            root.right = rightRotate(root.right);
            return leftRotate(root);
        }

        return root;
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