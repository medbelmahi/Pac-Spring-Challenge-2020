package codingame.pac.bfs;

import codingame.pac.cell.Floor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Mohamed BELMAHI created on 15/05/2020
 */
public class Node<T extends Floor> {
    private T node;
    private Set<T> children;

    Node(T node, Set<T> children) {
       this.node = node;
        this.children = children;
    }

    public Set<T> getChildren() {
        return children;
    }

    public T getNode() {
        return node;
    }
}
