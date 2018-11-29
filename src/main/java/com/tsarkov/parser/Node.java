package com.tsarkov.parser;

import java.util.ArrayList;

/**
 * Класс для хранения информации об узлах
 */
public class Node {
    String name;
    String value;
    int id;
    Node parent;
    ArrayList<Node> children = new ArrayList<Node>();

    Node(int id, Node parent, String name, String value){
        this.id = id;
        this.parent = parent;
        this.name = name;
        this.value = value;
    }
}
