package hr.fer.zemris.java.graphics.objects;

import java.util.ArrayList;
import java.util.List;

public abstract class GraphicalObject {
	private List<GraphicalObject> objects;

	public GraphicalObject() {
		objects = new ArrayList<>();
	}

	public void add(GraphicalObject object) {
		objects.add(object);
	}

	public void remove(GraphicalObject object) {
		objects.remove(object);
	}

	public abstract void accept(IGraphicalObjectVisitor visitor);

	public int numberOfChildren() {
		return objects.size();
	}

	public GraphicalObject getChild(int index) {
		return objects.get(index);
	}

}
