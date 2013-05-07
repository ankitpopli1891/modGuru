package com.aakash.app.wi_net.java;

import java.io.Serializable;
import java.util.ArrayList;

public class UniqueList<E> implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private ArrayList<E> list;
	
	public UniqueList() {
		list=new ArrayList<E>();
	}

	public void add(E object)
	{
		if(!list.contains(object))
			list.add(object);
	}
	
	public E remove(int index)
	{
		return list.remove(index);
	}
	
	public void remove(E object)
	{
		list.remove(object);
	}
	
	public E get(int index)
	{
		return list.get(index);
	}
	
	public int size()
	{
		return list.size();
	}
	
	public boolean isEmpty()
	{
		return list.isEmpty();
	}
	
	public boolean contains(Object obj)
	{
		return list.contains(obj);
	}
}
