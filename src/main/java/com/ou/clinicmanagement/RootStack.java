package com.ou.clinicmanagement;

import javafx.scene.Parent;

import java.util.Stack;

public class RootStack {
    private static final Stack<Parent> stack = new Stack<>();
    public static void push (Parent root) {
        stack.push(root);
    }

    public static Parent pop () {
        return stack.pop();
    }

    public static int size() {
        return stack.size();
    }

    public static void clearPrevious () {
        Parent latest = pop();
        stack.clear();
        stack.push(latest);
    }

    public static void clear () {
        stack.clear();
    }
}
