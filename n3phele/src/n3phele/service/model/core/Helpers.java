package n3phele.service.model.core;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import n3phele.service.model.Variable;

public class Helpers {
	
	public static URI stringToURI(String uri) {
		return uri==null?null: URI.create(uri);
	}
	
	public static String URItoString(URI uri) {
		return uri==null?null: uri.toString();
	}
	
	@SuppressWarnings("unchecked")
	public static <E> List<E> safeIterator(List<E> list) {
		return list == null ? (List<E>) nullList : list;	
	}
	@SuppressWarnings("rawtypes")
	final private static List nullList = new ArrayList(0);
	public static boolean isBlankOrNull(String s) {
		return s == null || s.isEmpty();
	}
}
