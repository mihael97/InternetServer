//package hr.fer.zemris.java.graphics.objects;
//
//public class GraphicsDemo {
//
//	public static void main(String[] args) {
//		Circle c = new Circle(100, 100, 20);
//		Rectangle r = new Rectangle(90, 70, 20, 50);
//		doStuff(c);
//		doStuff(r);
//		ObjectsGroup group = new ObjectsGroup();
//		group.add(c);
//		group.add(r);
//		doStuff(group);
//	}
//
//	public static void doStuff(GraphicalObject g) {
//		BoundingRect brect = g.getBoundingRect();
//		if (brect.right - brect.left > 200) {
//			System.out.println("Objekt je preširok!");
//		} else {
//			System.out.println("Objekt je prihvatljivih dimenzija.");
//		}
//	}
//}
