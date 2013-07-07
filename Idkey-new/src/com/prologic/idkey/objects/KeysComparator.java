package com.prologic.idkey.objects;

import java.util.Comparator;

public class KeysComparator implements Comparator<Key>
{

	private int sortingOrder;
	private int sortingType;
	public static final int SORTING_ORDER_ASCENDING = 0;
	public static final int SORTING_ORDER_DECENDING = 1;

	public static final int SORTING_TYPE_NO = 0;
	public static final int SORTING_TYPE_ID = 1;

	public static final int SORTING_TYPE_DATE = 2;
	public static final int SORTING_TYPE_ALL_CAT = 3;

	public KeysComparator(int sortingType,int sortingOrder) 
	{
		this.sortingType = sortingType;
		this.sortingOrder = sortingOrder;
	}

	public void setSortingType(int sortingType) {
		this.sortingType = sortingType;
	}
	public int getSortingType() {
		return sortingType;
	}
	public void setSortingOrder(int sortingOrder) {
		this.sortingOrder = sortingOrder;
	}
	public int getSortingOrder() {
		return sortingOrder;
	}
	@Override
	public int compare(Key lhs, Key rhs) 
	{
		//int compareNo = ((Key) rhs).getNo();

		return lhs.getNo() -  rhs.getNo();


	}

}
