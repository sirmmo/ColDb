package it.mmo.coldb;

import java.util.ArrayList;

import org.mapdb.Fun.Tuple2;

public class QueryResult<T> extends ArrayList<T> implements Queryable<T> {
	public QueryResult<T> filter(Tuple2<String, String> tup) throws Exception {
		return null;
	}
}
