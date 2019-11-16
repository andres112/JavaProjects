package com.company;

public interface SimpleKV {
	public void put(Object k, Object v);
	public Object get(Object k);
	public int join(Object k);
}