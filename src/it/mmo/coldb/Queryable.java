package it.mmo.coldb;

import org.mapdb.Fun.Tuple2;

public interface Queryable<T> {
	public QueryResult<T> filter(Tuple2<String, String> tup) throws Exception;
}
