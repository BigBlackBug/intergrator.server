package com.icl.integrator.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by BigBlackBug on 2/12/14.
 */
@Service
public class JsonMatcher {

	public boolean matches(JsonNode data, JsonNode reference) {
		if(reference instanceof NullNode){
			return true;
		}
		if (reference.isObject()) {
			Iterator<Map.Entry<String, JsonNode>> fields = reference.fields();
			while (fields.hasNext()) {
				Map.Entry<String, JsonNode> item = fields.next();
				JsonNode jsonNode = data.get(item.getKey());
				if (jsonNode == null) {
					return false;
				}
				if (!matches(jsonNode, item.getValue())) {
					return false;
				}
			}
		} else if (reference.isArray()) {
			List<JsonNode> elements = copyIterator(reference.elements());
			List<JsonNode> dataElements = copyIterator(data.elements());
			if (elements.size() != dataElements.size()) {
				return false;
			}

			for (int i = 0; i < elements.size(); i++) {
				JsonNode element = elements.get(i);
				JsonNode dataElement = dataElements.get(i);
				if (!matches(dataElement, element)) {
					return false;
				}
			}
		} else {
			return data.asText().equals(reference.asText());
		}
		return true;
	}

	private <T> List<T> copyIterator(Iterator<T> iter) {
		List<T> copy = new ArrayList<T>();
		while (iter.hasNext()) {
			copy.add(iter.next());
		}
		return copy;
	}

}
