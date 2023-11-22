package com.lexing;

import lombok.Data;

/**
 * <h1>链表反转</h1>
 * Created by hanqf on 2023/10/8 15:05.
 */


public class ListReverse {

    @Data
    //节点
    public static class ListNode {
        int num; //序号
        ListNode next; //下一个节点

        public ListNode(int num, ListNode next) {
            this.num = num;
            this.next = next;
        }
    }

    //反转链表方法

    /**
     * 反转前 1->2->3->4->5
     * 反转后 5->4->3->2->1
     * <br>
     * 它接受一个链表的头节点作为输入，并返回一个新的链表，其中所有节点的顺序都被反转了。
     * 该方法使用三个指针来遍历链表。
     * 其中，`next` 指针用于保存当前节点的下一个节点，`prve` 指针用于保存当前节点的前一个节点，`current` 指针用于保存当前节点。
     * 在遍历链表时，该方法将当前节点的下一个节点赋值给下一个节点，
     * 将前一个节点赋值给当前节点的下一个节点，
     * 然后将前一个节点修改为当前节点，将当前节点修改为下一个节点。
     * 最终，该方法返回一个新的链表的头节点，这个链表的顺序就是原链表的顺序反转后的顺序。
     */

    //方法1：迭代
    public static ListNode iterater(ListNode head) {
        ListNode next;  //下一个节点
        ListNode prve = null; //前一个节点
        ListNode current = head; //当前节点
        while (current != null) { // 当前节点不为null时循环遍历
            next = current.next; //保存当前节点的下一个节点
            current.next = prve; //将当前节点的下一个节点赋值为当前节点的前一个节点
            prve = current;  // 前一个节点修改为当前节点
            current = next;  // 当前节点修改为下一个节点
        }

        return prve; // 返回前一个节点，这里实际上返回的是最后一个节点
    }

    //方法2：递归，从最后一个元素开始往前
    public static ListNode recursion(ListNode head) {
        //递归结束条件
        if (head == null || head.next == null) {
            return head;
        }
        //递归调用找到最后一个节点，然后递归后的代码从最后一个节点开始执行
        ListNode newNode = recursion(head.next);
        head.next.next = head;
        head.next = null;
        return newNode;
    }

    public static void main(String[] args) {
        //声明具有5个节点的链表
        ListNode fiveNode = new ListNode(5, null);
        ListNode fourNode = new ListNode(4, fiveNode);
        ListNode threeNode = new ListNode(3, fourNode);
        ListNode twoNode = new ListNode(2, threeNode);
        ListNode oneNode = new ListNode(1, twoNode);

//        ListNode newNode = iterater(oneNode);
        ListNode newNode = recursion(oneNode);
        System.out.println(newNode);
    }
}
