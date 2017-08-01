import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.LinkedList;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.*;

/**
 *
 * Linked lists are being used to hold multiple values
 * What this means is that when we delete, you will need a key AND a value.
 *
 * NULL values are under the sentinel
 *
 * Created by Aaron on 4/25/17.
 */
public class BTree<K, V> {
    BtreeNode root;
    int height;
    int m;
    K sentinalType;
    //This is Strictly for the project
    String name;
    int n; //Amount of elements
    static int print = 0;

    public BTree(String type, int m, String name) {
        this.m = m;
        this.root = new BtreeNode(m);
        height = 0;
        this.name = name;
        setType(type);
    }


    /**
     * Both print methods are strictly for debugging
     */
    public void print(){
        for(int i = 0 ; i <m; i++){
            if(root.node[i]==null){
                break;
            }
            System.out.println(print+" "+root.node[i].key);

            System.out.println();
            if(root.node[i].children!=null){
                print++;
                print(root.node[i].children);
                print--;
            }
            System.out.println();
        }

    }
    public void print(BtreeNode bt){
        for(int i = 0 ; i <m; i++){
            if(bt.node[i]==null)break;
            System.out.println(print+" "+bt.node[i].key);
            if(bt.node[i].children!=null){
                print++;
                print(bt.node[i].children);
                print--;
            }
        }

        System.out.println();
    }


    public String getName() {
        return name;
    }

    /**
     * Adds Sentinel
     * @param type
     */
    private void setType(String type) {
        switch (type.toUpperCase()){
            case "INT":
                Integer min = Integer.MIN_VALUE;
                this.sentinalType = (K) min;
                root.node[0] = new Entry(Integer.MIN_VALUE, null, null);
                break;

            case "VARCHAR":
                this.sentinalType = (K) " ";
                root.node[0] = new Entry(" ", null, null);
                break;

            case "DECIMAL":
                Double minD = Double.MIN_VALUE;
                this.sentinalType = (K) minD;
                root.node[0] = new Entry(Double.MIN_VALUE, null, null);
        }
    }

    public HashSet<V> select(K key, Condition c){
        HashSet<V> list = new HashSet<>();
        if(key == null){
            key = sentinalType;
        }
        switch(c.getOperator()){

            case LESS_THAN:
                list = getAllLessThan(key, root, list, false, this.height);
                return list;
            case GREATER_THAN:
                list = getAllGreaterThan(key, root, list, false, this.height);
                return list;
            case lESS_THAN_OR_EQUALS:
                list = getAllLessThan(key, root, list, true, this.height);
                return list;
            case GREATER_THAN_OR_EQUALS:
                list = getAllGreaterThan(key, root, list, true, this.height);
                return list;

        }
        return list;
    }

    //Inclusive means greater AND equal
    public HashSet<V> getAllGreaterThan(K key, BtreeNode current, HashSet list, boolean inclusive, int height){
        if(height > 0){
            for (int i = 0; i < m ; i++) {
                if(current.node[i] == null){
                    break;
                }
                if(current.node[i].compareTo(key) < 0 && current.node[i + 1] != null){
                    if(current.node[i + 1].compareTo(key) > 0){
                        if(current.node[i].children != null)
                        getAllGreaterThan(key, current.node[i].children, list, inclusive, height - 1);
                    }
                }
                if(current.node[i].compareTo(key) >= 0){
                        if(current.node[i].children != null)
                        getAllGreaterThan(key, current.node[i].children, list, inclusive, height - 1);
                }
                if(current.node[i+1] == null){
                    if(current.node[i].children != null)
                        getAllGreaterThan(key, current.node[i].children, list, inclusive, height - 1);
                }
            }
        }
        if(height == 0) {
            for (int j = 0; j < m; j++ ) {
                    if (current.node[j] == null) {
                        break;
                    }
                    //I mean who knows, we could be searching for a null value
                    if (current.node[j].equals(sentinalType) && !key.equals(sentinalType)) {
                        continue;
                    } else if (current.node[j].compareTo(key) == 0) {
                        if (inclusive) {
                            list.addAll(current.node[j].getValue());
                        }
                    } else if (current.node[j].compareTo(key) > 0) {
                        list.addAll(current.node[j].getValue());
                    }
                }
        }
        return list;
    }

    //Inclusive means greater AND equal
    public HashSet<V> getAllLessThan(K key, BtreeNode root, HashSet list, boolean inclusive, int height){
        if(height > 0){
            for(Entry e : root.node){
                if(e == null){
                    break;
                }
                if(e.compareTo(key) <= 0){
                    list.addAll(getAllLessThan(key, e.getChild(), list, inclusive, height - 1));
                }

            }
        }
        if(height == 0) {
            for (Entry entry : root.node) {
                if(entry == null){
                    break;
                }
                //I mean who knows, we could be searching for a null value
                if (entry.getKey().equals(sentinalType) && !key.equals(sentinalType)) {
                    continue;
                }
                if (entry.compareTo(key) == 0) {
                    if (inclusive) {
                        list.addAll(entry.getValue());
                        return list;
                    }
                    else {
                        //Because if we hit our value, this node is useless
                        break;
                    }
                }
                if(entry.compareTo(key) < 0){
                    list.addAll(entry.getValue());
                }
            }
        }
        return list;
    }

    public void put(K key, V value) {
        if(key == null){
            key = sentinalType;
        }
        BtreeNode x = put(this.root, key, value, this.height);
        if(x != null){
            splitAtRoot(x);
        }
        n++;
    }

    public LinkedList get(K key){
        if(key == null){
            key = sentinalType;
        }
        try {
            LinkedList value = get(key, root, this.height);
            return value;
        }
        catch (NullPointerException e){
            return null;
        }
    }

    private LinkedList<V> get(K key, BtreeNode root, int height){
        if(height != 0){
            for (int i = 0; i < m; i++) {
                if(root.node[i] == null){
                    return null;
                }
                int cmp = root.node[i].compareTo(key);
                if(cmp == 0){
                   return get(key, root.node[i].getChild(), height - 1);
                }
                else if(cmp < 0) {
                    //We check the children entry
                    try {
                        int compareNext = root.node[i + 1].compareTo(key);
                        if (compareNext > 0) {
                            //Go to the children level
                            return get(key, root.node[i].getChild(), height - 1);
                        }

                    }
                    //We are at last entry
                    catch (NullPointerException e) {
                        return get(key, root.node[i].getChild(), height - 1);
                    }
                }
            }
        }
        //If we are at a leaf
        else if(height == 0){
            for (int i = 0; i < m; i++) {
                if(root.node[i] == null){
                    return null;
                }
                int cmp = root.node[i].compareTo(key);
                if(cmp == 0){
                    return root.node[i].getValue();
                }
            }
        }
        return null;
    }

    public void remove(K key, V value){
        if(key == null){
            key = sentinalType;
        }
        try {
            LinkedList list = get(key);
            if (list.contains(value)) {
                list.remove(value);
            }
        }
        catch (NullPointerException e){
            //It means value does not exist, and
            //since we're deleting it shouldnt matter.
        }
    }

    private BtreeNode put(BtreeNode current, K key, V valueToAdd, int height) {
        Entry v = new Entry(key, valueToAdd, null);
        if (height == 0) {
            for (int i = 0; i <= m; i++) {
                    if(current.node[i] == null){
                       current.node[i] = v;
                       break;
                    }
                    int compareTo = current.node[i].compareTo(key);
                    if (compareTo == 0) {
                        //Values are equals and external node
                        current.node[i].value.add(valueToAdd);
                        break;
                    }
                    else if (compareTo > 0) {
                        current.moveUpChildren(i);
                        current.node[i] = v;
                        break;
                    }
                }
            }
        else if(height > 0){
                //internal
                BtreeNode x = null;
                for (int i = 0; i <= m; i++) {
                    if (current.node[i] == null) {
                        x = put(current.node[i - 1].getChild(), key, valueToAdd, height - 1);
                        break;
                    }
                    int compare = current.node[i].compareTo(key);
                    //Which means we need to go down to the children child.
                    if (compare == 0) {
                        put(current.node[i].getChild(), key, valueToAdd, height - 1);
                        break;
                    } else if (compare < 0) {
                        //We check the one next to it, if it is greater, we go down this entry
                        //If it is not, we continue down the array.
                        try {
                            int compareNext = current.node[i + 1].compareTo(key);
                            if (compareNext > 0) {
                                //Go to the children level
                                x = put(current.node[i].getChild(), key, valueToAdd, height - 1);
                                break;
                            }
                            else{
                                continue;
                            }
                        }
                        //We are at last entry
                        catch (NullPointerException e) {
                            x = put(current.node[i].getChild(), key, valueToAdd, height - 1);
                            break;
                        }
                    }
                }
                    if(x != null){
                        //If it returned a node we had a split
                        Entry nEntry = new Entry(x.node[0].getKey(), null, x);
                            for (int q = 0; q <= m; q++) {
                                if(current.node[q] == null){
                                    current.node[q] = nEntry;
                                    break;
                                }
                                int compared = current.node[q].compareTo(nEntry.getKey());
                                if (compared > 0) {
                                    current.moveUpChildren(q);
                                    current.node[q] = nEntry;
                                    break;
                                }
                         }
                    }
            }
        if(current.isFull()){
            BtreeNode g = splitNode(current);
            //Return new node from split
            return g;
        }
        return null;
    }



    private void splitAtRoot(BtreeNode node){
        BtreeNode r = new BtreeNode(m);
        Entry rootFirst = new Entry(root.node[0].key, null, root);
        Entry rootSecond = new Entry(node.node[0].key, null, node);
        r.node[0] = rootFirst;
        r.node[1] = rootSecond;
        root = r;
        height++;
    }


    private BtreeNode splitNode(BtreeNode node) {
        BtreeNode newNode = new BtreeNode(m);
        int mid = node.node.length / 2;
        for (int i = mid; i < node.node.length; i++) {
            Entry n = new Entry<>(node.node[i].getKey(), node.node[i].getValue(), node.node[i].getChild());
            newNode.node[i - mid] = n;
            node.node[i] = null;
        }
        return newNode;
    }

    class BtreeNode<K, V> {
        Entry[] node;

        public BtreeNode(int m) {
            this.node = new Entry[m];
        }

        public boolean isFull() {
            if (node[node.length - 1] != null) {
                return true;
            }
            return false;
        }

        public int indexOf(Entry v) {
            for (int i = 0; i < node.length; i++) {
                if (node[i] == v) {
                    return i;
                }
            }
            return -1;
        }

        public void moveUpChildren(int position) {
            for (int i = node.length - 2; i >= position; i--) {
                node[i + 1] = node[i];
                if (i == position) {
                    node[i] = null;
                }
            }
        }
    }


    /**
     * This is our entry class to be used in tree
     *
     * @param <K>
     * @param <V>
     */
    class Entry<K, V> implements Comparable<K> {
        K key;
        LinkedList<V> value;
        BtreeNode children;

        public Entry(K key, V value, BtreeNode node) {
            if(value instanceof LinkedList){
                this.value = (LinkedList<V>) value;
            }
            else {
                this.value = new LinkedList<>();
                this.value.add(value);
            }
            this.key = key;
            this.children = node;
        }

        public K getKey() {
            return key;
        }

        public LinkedList<V> getValue() {
            return value;
        }

        public BtreeNode getChild() {
            return children;
        }

        public void setChild(BtreeNode child) {
            this.children = child;
        }

        @Override
        public int compareTo(@NotNull K o) {
            if (key instanceof Integer) {
                Integer ourVal = (Integer) this.key;
                Integer otherVal = (Integer) o;
                return ourVal.compareTo(otherVal);
            } else if (key instanceof String) {
                String ourVal = (String) this.key;
                String otherVal = (String) o;
                return ourVal.compareTo(otherVal);
            } else if (key instanceof Double) {
                Double ourVal = (Double) this.key;
                Double otherVal = (Double) o;
                return ourVal.compareTo(otherVal);
            }
            return 0;
        }
    }
}
