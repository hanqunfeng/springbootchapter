package org.example.tree;

import java.util.LinkedList;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * <p>数据结构--二叉树</p>
 * 参考：https://www.jianshu.com/p/fadfd7965865
 * Created by hanqf on 2020/7/8 16:53.
 */
public class BinaryTreeNode {
    /**
     * 数据
     */
    private int data;

    /**
     * 父节点
     */
    private BinaryTreeNode parent;
    /**
     * 左节点
     */
    private BinaryTreeNode left;
    /**
     * 右节点
     */
    private BinaryTreeNode right;

    public BinaryTreeNode(int data, BinaryTreeNode left, BinaryTreeNode right) {
        this.data = data;
        this.left = left;
        this.right = right;
        if (this.left != null) {
            this.left.setParent(this);
        }
        if (this.right != null) {
            this.right.setParent(this);
        }
    }

    /**
     * <p>递归前序遍历</p>
     * <p>
     * 访问当前结点，遍历左子树，再遍历右子树。
     *
     * @param root
     * @author hanqf
     * 2020/7/8 17:03
     */
    public static void PreOrder(BinaryTreeNode root) {
        if (root != null) {
            System.out.print(root.getData());
            PreOrder(root.getLeft());
            PreOrder(root.getRight());
        }
    }

    /**
     * <p>非递归前序遍历</p>
     *
     * @param root
     * @author hanqf
     * 2020/7/8 17:07
     */
    public static void PreOrderNonRecursive(BinaryTreeNode root) {
        if (root == null) {
            return;
        }
        //用一个栈存储当前节点
        Stack<BinaryTreeNode> s = new Stack();
        while (true) {
            while (root != null) {
                System.out.print(root.getData());
                s.push(root);
                root = root.getLeft();
            }
            if (s.isEmpty()) {
                break;
            }
            root = s.pop();
            root = root.getRight();
        }
    }

    /**
     * <p>递归中序遍历</p>
     * 先遍历左子树，访问当前结点，再遍历右子树。
     *
     * @param root
     * @author hanqf
     * 2020/7/8 17:15
     */
    public static void InOrder(BinaryTreeNode root) {
        if (root != null) {
            InOrder(root.getLeft());
            System.out.print(root.getData());
            InOrder(root.getRight());
        }
    }

    /**
     * <p>非递归中序遍历</p>
     *
     * @param root
     * @author hanqf
     * 2020/7/8 17:16
     */
    public static void InOrderNonRecursive(BinaryTreeNode root) {
        if (root == null) {
            return;
        }
        Stack<BinaryTreeNode> s = new Stack();
        while (true) {
            while (root != null) {
                s.push(root);
                root = root.getLeft();
            }
            if (s.isEmpty()) {
                break;
            }
            root = s.pop();
            System.out.print(root.getData());
            root = root.getRight();
        }
    }

    /**
     * <p>递归后序遍历</p>
     * 先遍历左子树，再遍历右子树，访问当前结点。
     *
     * @param root
     * @author hanqf
     * 2020/7/8 17:20
     */
    public static void PostOrder(BinaryTreeNode root) {
        if (root != null) {
            PostOrder(root.getLeft());
            PostOrder(root.getRight());
            System.out.print(root.getData());
        }
    }

    /**
     * <p>非递归后序遍历</p>
     *
     * @param root
     * @author hanqf
     * 2020/7/8 17:21
     */
    public static void PostOrderNonRecursive(BinaryTreeNode root) {
        Stack<BinaryTreeNode> stack = new Stack();
        while (true) {
            if (root != null) {
                //寻找最左叶子结点
                stack.push(root);
                root = root.getLeft();
            } else {
                if (stack.isEmpty()) {
                    return;
                } else {
                    //判断当前结点是否有右子节点
                    if (stack.peek().getRight() == null) {
                        root = stack.pop();
                        System.out.print(root.getData());
                        //判断该结点是否为栈顶右子节点
                        while (root == stack.peek().getRight()) {
                            System.out.print(stack.peek().getData());
                            root = stack.pop();
                            if (stack.isEmpty()) {
                                return;
                            }
                        }
                    }
                }
                if (!stack.isEmpty()) {
                    //遍历结点右子树
                    root = stack.peek().getRight();
                } else {
                    root = null;
                }
            }
        }
    }

    /**
     * <p>层次遍历</p>
     *
     * @param root
     * @author hanqf
     * 2020/7/8 18:08
     */
    public static void LevelOrder(BinaryTreeNode root) {
        BinaryTreeNode temp;
        ConcurrentLinkedQueue<BinaryTreeNode> queue = new ConcurrentLinkedQueue();
        if (root == null) {
            return;
        }
        queue.add(root);
        while (!queue.isEmpty()) {
            temp = queue.remove();
            //处理当前结点
            System.out.print(temp.getData());
            if (temp.getLeft() != null) {
                queue.add(temp.getLeft());
            }
            if (temp.getRight() != null) {
                queue.add(temp.getRight());
            }
        }
    }

    /**
     * <p>递归计算总节点数</p>
     *
     * @param root
     * @return int
     * @author hanqf
     * 2020/7/9 10:58
     */
    public static int getAllNodes(BinaryTreeNode root) {
        int num = 0;
        if (root != null) {
            num = 1;
            num += getAllNodes(root.left);
            num += getAllNodes(root.right);
        }
        return num;
    }

    /**
     * <p>非递归计算总节点数</p>
     *
     * @param root
     * @return int
     * @author hanqf
     * 2020/7/9 10:11
     */
    public static int getAllNodesNonRecursive(BinaryTreeNode root) {
        int num = 0;
        if (root == null) {
            return num;
        }
        //用一个栈存储当前节点
        Stack<BinaryTreeNode> s = new Stack();
        while (true) {
            while (root != null) {
                num++;
                s.push(root);
                root = root.getLeft();
            }
            if (s.isEmpty()) {
                break;
            }
            root = s.pop();
            root = root.getRight();
        }
        return num;
    }

    /**
     * <p>递归计算树的深度(高度)</p>
     * 根的深度为1
     *
     * @param root
     * @return int
     * @author hanqf
     * 2020/7/9 10:28
     */
    public static int getTreeDepth(BinaryTreeNode root) {
        if (root == null) {
            return 0;
        }
        int left = getTreeDepth(root.left) + 1;
        int right = getTreeDepth(root.right) + 1;
        return Math.max(left, right);
    }

    /**
     * <p>非递归计算树的深度(高度)</p>
     *
     * @param root
     * @return int
     * @author hanqf
     * 2020/7/9 10:58
     */
    public static int getTreeDepthNonRecursive(BinaryTreeNode root) {
        if (root == null) {
            return 0;
        }

        BinaryTreeNode current = null;
        LinkedList<BinaryTreeNode> list = new LinkedList<>();
        list.offer(root);
        int cur, last;
        int level = 0;
        while (!list.isEmpty()) {
            cur = 0;//记录本层已经遍历的节点个数
            last = list.size();//当遍历完当前层以后，队列里元素全是下一层的元素，队列的长度是这一层的节点的个数
            while (cur < last)//当还没有遍历到本层最后一个节点时循环
            {
                current = list.poll();//出队一个元素
                cur++;
                //把当前节点的左右节点入队（如果存在的话）
                if (current.left != null) {
                    list.offer(current.left);
                }
                if (current.right != null) {
                    list.offer(current.right);
                }
            }
            level++;//每遍历完一层level+1
        }
        return level;
    }

    public BinaryTreeNode getParent() {
        return parent;
    }

    public void setParent(BinaryTreeNode parent) {
        this.parent = parent;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    public BinaryTreeNode getLeft() {
        return left;
    }

    public void setLeft(BinaryTreeNode left) {
        this.left = left;
    }

    public BinaryTreeNode getRight() {
        return right;
    }

    public void setRight(BinaryTreeNode right) {
        this.right = right;
    }
}
