package me.mastercapexd.auth.utils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class CollectionUtils {
	public static <T> List<List<T>> chopList(List<T> list, final int newListSize) {
		List<List<T>> parts = new ArrayList<List<T>>();
		final int oldListSize = list.size();
		for (int i = 0; i < oldListSize; i += newListSize)
			parts.add(new ArrayList<T>(list.subList(i, Math.min(oldListSize, i + newListSize))));

		return parts;
	}

	public static <T> List<T> getListPage(List<T> source, int pageCount, int onePageSize) {
		List<T> pageList = new ArrayList<>();
		int to = (pageCount * onePageSize - 1);
		int from = (to - onePageSize);
		for (int i = to; i > from; i--)
			try {
				pageList.add(source.get(i));
			} catch (IndexOutOfBoundsException ignored) {
				break;
			}

		return pageList;
	}

	public static int getMaxPages(int totalItemCount, int onePageLimit) {
		return (int) Math.ceil((float) totalItemCount / onePageLimit);
	}

	public static <K, V> Entry<K, V> newEntry(K key, V value) {
		return new AbstractMap.SimpleEntry<>(key, value);
	}
}