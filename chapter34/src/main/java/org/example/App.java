package org.example;

import org.example.tree.BinaryTreeNode;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {

        /**
         * 遍历二叉树
         *     1
         *   2   3
         *  4 5 6 7
         */

        //拼装二叉树
        BinaryTreeNode left1_left1 = new BinaryTreeNode(4, null, null);
        BinaryTreeNode left1_right1 = new BinaryTreeNode(5, null, null);
        BinaryTreeNode left1 = new BinaryTreeNode(2, left1_left1, left1_right1);

        BinaryTreeNode right1_left1 = new BinaryTreeNode(6, null, null);
        BinaryTreeNode right1_right1 = new BinaryTreeNode(7, null, null);
        BinaryTreeNode right1 = new BinaryTreeNode(3, right1_left1, right1_right1);

        BinaryTreeNode root = new BinaryTreeNode(1, left1, right1);


        System.out.println("递归前序遍历");
        //基于前序遍历
        BinaryTreeNode.PreOrder(root);
        System.out.println();
        System.out.println("非递归前序遍历");
        BinaryTreeNode.PreOrderNonRecursive(root);
        System.out.println();

        System.out.println("递归中序遍历");
        BinaryTreeNode.InOrder(root);
        System.out.println();

        System.out.println("非递归中序遍历");
        BinaryTreeNode.InOrderNonRecursive(root);
        System.out.println();

        System.out.println("递归后序遍历");
        BinaryTreeNode.PostOrder(root);
        System.out.println();

        System.out.println("非递归后序遍历");
        BinaryTreeNode.PostOrderNonRecursive(root);
        System.out.println();

        System.out.println("层次遍历");
        BinaryTreeNode.LevelOrder(root);
        System.out.println();

        System.out.println("递归总结点数==" + BinaryTreeNode.getAllNodes(root));
        System.out.println("非递归总结点数==" + BinaryTreeNode.getAllNodesNonRecursive(root));
        System.out.println("递归树的深度==" + BinaryTreeNode.getTreeDepth(root));
        System.out.println("非递归树的深度==" + BinaryTreeNode.getTreeDepthNonRecursive(root));



    }
}
