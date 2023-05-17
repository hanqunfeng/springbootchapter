package org.example.safe.safeclass;

import java.util.ArrayList;
import java.util.List;

/**
 * 不安全的发布
 */
public class UnSafePublish {
	private List<Integer> list = new ArrayList<>(3);

    public UnSafePublish() {
    	list.add(1);
    	list.add(2);
    	list.add(3);
    }

	public List getList() {
		return list;
	}

	public static void main(String[] args) {
		UnSafePublish unSafePublish = new UnSafePublish();
		List<Integer> list = unSafePublish.getList();
		System.out.println(list);
		list.add(4);
		System.out.println(list);
		System.out.println(unSafePublish.getList());
	}
}
